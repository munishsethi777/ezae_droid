package in.learntech.rights.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.ViewGroup;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.utils.StringConstants;

public class ServiceHandler extends AsyncTask<Void, Void, JSONObject>{

    private final String mApiUrl;
    private final IServiceHandler mIActivity;
    private final String mRequestType;
    private final String mCall;
    private final Activity mActivity;
    private  ProgressDialog mProgressDialog;
    private boolean isShowProgress;

    public void setShowProgress(boolean isShowProgress){
        this.isShowProgress = isShowProgress;
    }
    public ServiceHandler(String apiUrl,IServiceHandler serviceHandler,Activity activity) {
        mApiUrl = apiUrl;
        mIActivity = serviceHandler;
        mRequestType = StringConstants.GET;
        mCall = null;
        mActivity = activity;
        isShowProgress = true;
        initProgressDialog();
    }

    public ServiceHandler(String apiUrl,IServiceHandler serviceHandler,String call,Activity activity) {
        mApiUrl = apiUrl;
        mIActivity = serviceHandler;
        mRequestType = StringConstants.GET;
        mCall = call;
        mActivity = activity;
        isShowProgress = true;
        initProgressDialog();
    }

    @Override
    protected JSONObject doInBackground(Void... params){
        HttpURLConnection urlConnection = null;
        URL url = null;
        JSONObject object = null;
        InputStream inStream = null;
        boolean success = false;
        try {
            url = new URL(mApiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode(); //can call this instead of con.connect()
            if (responseCode >= 400 && responseCode <= 499) {
                inStream = urlConnection.getErrorStream();
            }else{
                urlConnection.connect();
                inStream = urlConnection.getInputStream();
            }
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null){
                response += temp;
            }
            object = (JSONObject) new JSONTokener(response).nextValue();
            Thread.sleep(100);
        } catch (Exception e) {
            if(e instanceof UnknownHostException){
                object = new JSONObject();
                try{
                    object.put("success",0);
                    object.put("message",StringConstants.CONNECTION_ERROR);
                }catch (Exception ex){
                    return null;
                }

            }
        }
        return object;
    }
    @Override
    protected void onPreExecute() {
        if(mProgressDialog != null && isShowProgress)
            mProgressDialog.show();
    }
    @Override
    protected void onPostExecute(final JSONObject response) {
        mIActivity.setCallName(mCall);
        mIActivity.processServiceResponse(response);
        if(mProgressDialog != null && isShowProgress)
            mProgressDialog.dismiss();
    }


    @Override
    protected void onCancelled() {

    }

    private void initProgressDialog() {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Wait...");
    }






}