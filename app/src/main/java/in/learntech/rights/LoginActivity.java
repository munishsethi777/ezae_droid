package in.learntech.rights;

import java.io.IOException;
import java.text.MessageFormat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.StringConstants;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,IServiceHandler {
    View bg;
    //Views
    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mGcmid;
    private ServiceHandler mAuthTask = null;
    private UserMgr mUserMgr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Sign In");
        }

        bg = findViewById(R.id.activity_loginsignup_style12);
        mPasswordView = (EditText) findViewById(R.id.password_view);
        mUsernameView = (EditText) findViewById(R.id.username_view);
        mUserMgr = UserMgr.getInstance(this);
        int loggedInUserSeq = mUserMgr.getLoggedInUserSeq();
        if(loggedInUserSeq > 0){
            goToDashboardActivity();
        }
        String url = BuildConfig.IMAGE_URL + "login-signup/style-12/Login-Register-12-960.jpg";

        Glide.with(this)
                .load(url)
                .thumbnail(0.01f)
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        bg.setBackground(resource);
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loginsignup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_search:
                Toast.makeText(this, "action search clicked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Toast.makeText(this, "action setting clicked!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtForgotPassword:
                Toast.makeText(this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnSignIn:
                loginWithGCMID();
                //Toast.makeText(this, "Sign In button clicked!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void processServiceResponse(JSONObject response){
        mAuthTask = null;
        //showProgress(false);
        boolean success = false;
        String message = "Logged in successfully";
        try{
            success = response.getInt("success") == 1 ? true : false;
            message = response.getString("message");
            if(success){
                mUserMgr.saveUserFromResponse(response);
                goToDashboardActivity();
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void loginWithGCMID() {
        new AsyncTask<Void, Void, String>() {
            GoogleCloudMessaging gcm;
            String regid;
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                    regid = instanceID.getToken("18881196144",
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    // Persist the regID - no need to register again.
                    //storeRegistrationId(getContext(), regid);
                } catch (IOException ex) {

                    String message = ex.getMessage();

                }
                return regid;
            }
            @Override
            protected void onPostExecute(String regId) {
                mGcmid = regId;
                attemptLogin();
            }
        }.execute(null, null, null);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Object[] args = {username,password,mGcmid};
            String loginUrl = MessageFormat.format(StringConstants.LOGIN_URL,args);
            mAuthTask = new ServiceHandler(loginUrl,this);
            mAuthTask.execute();
        }
    }



    private void goToDashboardActivity(){
        Intent intent = new Intent(this,DashboardActivity.class);
        startActivity(intent);
    }
}
