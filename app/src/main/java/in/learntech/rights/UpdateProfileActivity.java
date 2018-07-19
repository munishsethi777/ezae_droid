package in.learntech.rights;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;

public class UpdateProfileActivity extends AppCompatActivity implements IServiceHandler , View.OnClickListener{
    public static final String UPDATE_USER_PROFILE = "updateUserProfile";
    public static final String GET_PROFILE_DETAIL = "getProfileDetail";
    private UserMgr mUserMgr;
    private ServiceHandler mAuthTask;
    private LayoutHelper mLayoutHelper;
    private EditText mEmailView;
    private ImageView mUserImageView;
    private LinearLayout mProfileLayout;
    private EditText mView;
    private int mUserSeq;
    private int mCompanySeq;
    private String mCallName;
    public static final int GET_FROM_GALLERY = 3;
    private Bitmap userImageBitMap;
    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;
    private String mPicturePath;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mUserMgr = UserMgr.getInstance(this);
        LayoutInflater li = (LayoutInflater)getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        mLayoutHelper = new LayoutHelper(this,li,null);
        mEmailView = (EditText)findViewById(R.id.editText_email);
        mProfileLayout = (LinearLayout)findViewById(R.id.layout_updateProfile);
        mUserImageView = (ImageView)findViewById(R.id.imageView_user);


        executeGetProfileDetail();
    }

    private void executeGetProfileDetail(){
        mUserSeq = mUserMgr.getLoggedInUserSeq();
        mCompanySeq = mUserMgr.getLoggedInUserCompanySeq();
        Object[] args = {mUserSeq,mCompanySeq};
        String getUserDetailURL = MessageFormat.format(StringConstants.GET_USER_DETAIL,args);
        mAuthTask = new ServiceHandler(getUserDetailURL,this, GET_PROFILE_DETAIL,this);
        mAuthTask.execute();
    }

    private void updateProfile(){
        int count = mProfileLayout.getChildCount();
        JSONObject customFieldValue = new JSONObject();
        JSONObject userJson = new JSONObject();
        boolean cancel = false;
        View focusView = null;
        try{
            for (int i = 1; i < count; i++) {
                ViewGroup parent = (ViewGroup) mProfileLayout.getChildAt(i);
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(0);
                if(linearLayout == null){
                    continue;
                }
                View view = linearLayout.getChildAt(1);
                if (view instanceof EditText) {
                    EditText editText = (EditText)view;
                    JSONObject args = (JSONObject) view.getTag();
                    String id = args.getString("id");
                    int required = args.getInt("required");
                    String value = ((EditText) view).getText().toString();
                    if(required > 0 && TextUtils.isEmpty(value)){
                        editText.setError(getString(R.string.error_field_required));
                        focusView = editText;
                        cancel = true;
                    }
                    customFieldValue.put(id,value);
                }
            }
            if (cancel) {
                focusView.requestFocus();
            } else {
                userJson.put("emailId", mEmailView.getText());
                userJson.put("customFields", customFieldValue);
                String jsonString = userJson.toString();
                jsonString = URLEncoder.encode(jsonString, "UTF-8");
                Object[] args = {mUserSeq, mCompanySeq, jsonString};
                String notificationUrl = MessageFormat.format(StringConstants.UPDATE_USER_PROFILE, args);
                mAuthTask = new ServiceHandler(notificationUrl, this, UPDATE_USER_PROFILE, this);
                mAuthTask.setFileUploadRequest(true);
                BitmapDrawable drawable = (BitmapDrawable) mUserImageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                //Bitmap bitmap = ((BitmapDrawable)mUserImageView.getDrawable()).getBitmap();
                mAuthTask.setBitmap(bitmap);
                mAuthTask.execute();
            }
        }catch (Exception e){
            LayoutHelper.showToast(this,e.getMessage());
        }

    }

    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        boolean success;
        String message;
        try{
            success = response.getInt("success") == 1 ? true : false;
            message = response.getString("message");
            if(success){
                if(mCallName.equals(GET_PROFILE_DETAIL)) {
                    createCustomFieldViews(response.getJSONObject("userDetail"));
                }else{
                    UserMgr.getInstance(this).saveUserFromResponse(response);
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        LayoutHelper.showToast(this,message);
    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    private void createCustomFieldViews(JSONObject userDetail)throws Exception{
        String email = userDetail.getString("emailid");
        mEmailView.setText(email);
        String userImageUrl = mUserMgr.getLoggedInUserImageUrl();
        mLayoutHelper.loadImageRequest(mUserImageView,userImageUrl,false);
        JSONArray customFieldArr = userDetail.getJSONArray("customFields");
        for(int i=0;i<customFieldArr.length();i++){
            JSONObject customField = customFieldArr.getJSONObject(i);
            int fieldSeq = customField.getInt("fieldSeq");
            String fieldTitle = customField.getString("fieldTitle");
            String fieldType = customField.getString("fieldType");
            String fieldValue = customField.getString("fieldValue");
            int required = customField.getInt("isRequired");
            LinearLayout parentLinearLayout = mLayoutHelper.getViewByType(fieldType,fieldValue);
            LinearLayout linearLayout = (LinearLayout) parentLinearLayout.getChildAt(0);
            TextView labelTextView = (TextView) linearLayout.getChildAt(0);
            labelTextView.setText(fieldTitle);
            mView = (EditText)linearLayout.getChildAt(1);
            if(!fieldType.toLowerCase().equals("date")){
                mView.setText(fieldValue);
            }
            JSONObject args = new JSONObject();
            args.put("id",fieldSeq);
            args.put("required",required);
            mView.setTag(args);
            mView.setHint(fieldTitle);
            mProfileLayout.addView(parentLinearLayout);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.button_updateProfile){
            updateProfile();
        }else if(id == R.id.imageView_user ){
            clickpic();
        }else if(id == R.id.profile_logout){
            logoutConfirm();
        }else if(id == R.id.profile_changepassword){
            Intent intent = new Intent(this,ChangePasswordActivity.class);
            startActivity(intent);
        }
    }
    private void logout(){
        mUserMgr.resentPreferences();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void logoutConfirm() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Do you really want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    int column_index;
    String imagePath;

    private void clickpic() {
        // Check Camera
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"),GET_FROM_GALLERY);
        if(checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
            startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == GET_FROM_GALLERY && resultCode == RESULT_OK) {
//            Uri imageUri = data.getData();
//            try {
//                userImageBitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                mUserImageView.setImageBitmap(userImageBitMap);
//            }catch (IOException e){
//                String message = e.getMessage();
//                Toast.makeText(this,message,Toast.LENGTH_LONG);
//            }
//        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if(resultCode == Activity.RESULT_OK){
                mPicturePath = data.getStringExtra("picturePath");
                //perform Crop on the Image Selected from Galler
                    performCrop(mPicturePath);
            }
        }

        if (requestCode == RESULT_CROP ) {
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                // Set The Bitmap Data To ImageView
                mUserImageView.setImageBitmap(selectedBitmap);
                //mUserImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = null;
            contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult

            startActivityForResult(cropIntent, RESULT_CROP);
        }

        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                    startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

}
