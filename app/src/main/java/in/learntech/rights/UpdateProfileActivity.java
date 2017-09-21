package in.learntech.rights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private LinearLayout mProfileLayout;
    private EditText mView;
    private int mUserSeq;
    private int mCompanySeq;
    private String mCallName;
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
        mProfileLayout = (LinearLayout)findViewById(R.id.layout_updateProfile) ;
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
            for (int i = 2; i < count; i++) {
                ViewGroup parent = (ViewGroup) mProfileLayout.getChildAt(i);
                View view = parent.getChildAt(1);
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
        JSONArray customFieldArr = userDetail.getJSONArray("customFields");
        for(int i=0;i<customFieldArr.length();i++){
            JSONObject customField = customFieldArr.getJSONObject(i);
            int fieldSeq = customField.getInt("fieldSeq");
            String fieldTitle = customField.getString("fieldTitle");
            String fieldType = customField.getString("fieldType");
            String fieldValue = customField.getString("fieldValue");
            int required = customField.getInt("isRequired");
            LinearLayout linearLayout = mLayoutHelper.getViewByType(fieldType,fieldValue);
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
            mProfileLayout.addView(linearLayout);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.button_updateProfile){
            updateProfile();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}