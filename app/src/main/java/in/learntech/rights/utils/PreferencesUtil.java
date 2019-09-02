package in.learntech.rights.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import in.learntech.rights.messages.MessageChatActivity;

/**
 * Created by baljeetgaheer on 2/18/2016.
 */
public class PreferencesUtil {
    private Context mContext = null;
    private static PreferencesUtil mPreferencesUtil = null;
    private static Activity mActivity;
    public PreferencesUtil(Context context){
        mContext = context;
    }

    public static PreferencesUtil getInstance(Context context){
        if(mPreferencesUtil == null) {
            mPreferencesUtil = new PreferencesUtil(context);
        }
        return mPreferencesUtil;
    }
    public void setPreferences(String name ,String value) {
        SharedPreferences.Editor editor  = mContext.getSharedPreferences(StringConstants.PREFS_NAME, mContext.MODE_PRIVATE).edit();
        editor.putString(name,value);
        editor.commit();
    }
    public void removePreferences(String name) {
        SharedPreferences.Editor editor  = mContext.getSharedPreferences(StringConstants.PREFS_NAME, mContext.MODE_PRIVATE).edit();
        editor.remove(name);
        editor.commit();
    }

    public String getPreferences(String name){
        SharedPreferences prefs = mContext.getSharedPreferences(StringConstants.PREFS_NAME, mContext.MODE_PRIVATE);
        String value = prefs.getString(name, null);//"No name defined" is the default value.
        return value;
    }
    public boolean getPreferencesBool(String name){
        SharedPreferences prefs = mContext.getSharedPreferences(StringConstants.PREFS_NAME, mContext.MODE_PRIVATE);
        boolean value = prefs.getBoolean(name, false);//"No name defined" is the default value.
        return value;
    }


    public void setPreferencesBool(String name ,boolean value) {
        SharedPreferences.Editor editor  = mContext.getSharedPreferences(StringConstants.PREFS_NAME, mContext.MODE_PRIVATE).edit();
        editor.putBoolean(name,value);
        editor.commit();
    }

    public void resetPreferences(){
        SharedPreferences.Editor editor  = mContext.getSharedPreferences(StringConstants.PREFS_NAME, mContext.MODE_PRIVATE).edit();
        editor.clear().commit();
    }


    public void  setLoggedInUserSeq(long userSeq){
        setPreferences(StringConstants.LOGGED_IN_USER_SEQ, String.valueOf(userSeq));
    }

    public void  setLoggedInUserCompanySeq(long companySeq){
        setPreferences(StringConstants.LOGGED_IN_USER_COMPANY_SEQ, String.valueOf(companySeq));
    }

    public void  setHasTokenUpdated(){
        setPreferencesBool(StringConstants.HAS_TOKEN_UPDATED, true);
    }

    public boolean hasTokenUpdated(){
        boolean value = getPreferencesBool(StringConstants.HAS_TOKEN_UPDATED);
        return value;
    }

    public int getLoggedInUserCompanySeq(){
        String value = getPreferences(StringConstants.LOGGED_IN_USER_COMPANY_SEQ);
        int userSeq = 0;
        if(value != null){
            userSeq = Integer.parseInt(value);
        }
        return userSeq;
    }

    public int getLoggedInUserSeq(){
        String value = getPreferences(StringConstants.LOGGED_IN_USER_SEQ);
        int userSeq = 0;
        if(value != null){
            userSeq = Integer.parseInt(value);
        }
        return userSeq;
    }


    public boolean isShortcutCreated(){
        return getPreferencesBool(StringConstants.SHORTCUT);
    }

     public boolean isNotificationState(){
         SharedPreferences prefs = mContext.getSharedPreferences(StringConstants.PREFS_NAME, mContext.MODE_PRIVATE);
         boolean value = prefs.getBoolean(StringConstants.NOTIFICATION_STATE, false);
         return value;
     }
    public void setNotificationState(boolean state){
        setPreferencesBool(StringConstants.NOTIFICATION_STATE, true);
    }
     public void setNotificationData(String entitySeq,String entityType,String fromUserName) {
         setPreferences(StringConstants.NOTIFICATION_ENTITY_SEQ, entitySeq);
         setPreferences(StringConstants.NOTIFICATION_ENTITY_TYPE, entityType);
         setPreferences(StringConstants.FROM_USER_NAME, fromUserName);
     }
    public void setNotificationData(JSONObject jsonObject){
        try {
            Integer entitySeq = jsonObject.getInt("entitySeq");
            String entityType = jsonObject.getString("entityType");
            String fromUserName = "";
            if(jsonObject.has("fromUserName")){
                fromUserName = jsonObject.getString("fromUserName");
            }
            setPreferences(StringConstants.NOTIFICATION_ENTITY_SEQ, entitySeq.toString());
            setPreferences(StringConstants.NOTIFICATION_ENTITY_TYPE, entityType);
            setPreferences(StringConstants.FROM_USER_NAME, fromUserName);
        }catch (JSONException e){

        }

    }

    public Object[] getNotificationData(){
        SharedPreferences prefs = mContext.getSharedPreferences(StringConstants.PREFS_NAME, mContext.MODE_PRIVATE);
        String entitySeq = prefs.getString(StringConstants.NOTIFICATION_ENTITY_SEQ, null);//"No name defined" is the default value.
        String  entityType = prefs.getString(StringConstants.NOTIFICATION_ENTITY_TYPE,null);
        String  fromUserName = prefs.getString(StringConstants.FROM_USER_NAME,null);
        Object notificationData[] = new String[2];
        notificationData[0] = entitySeq;
        notificationData[1] = entityType;
        notificationData[2] = fromUserName;
        return notificationData;
    }

    public void resetNotificationData(){
        removePreferences(StringConstants.NOTIFICATION_STATE);
        removePreferences(StringConstants.NOTIFICATION_ENTITY_SEQ);
        removePreferences(StringConstants.NOTIFICATION_ENTITY_TYPE);
    }

    public void setCurrentActivityName(String activityName){
        setPreferences(StringConstants.CURRENT_ACTIVITY_NAME, activityName);
    }

    public String getCurrentActivityName(){
        String value = getPreferences(StringConstants.CURRENT_ACTIVITY_NAME);
        return value;
    }

    public static void setCurrentActivity(Activity activity){
        mActivity = activity;
    }
    public static Activity getCurrentActivity(){
        return mActivity;
    }

    public void setVersion(int version){
        setPreferences(StringConstants.VERSION,String.valueOf(version));
    }

    public int getVersion(){
        String value = getPreferences(StringConstants.VERSION);
        int version = 1;
        if(value != null){
            version = Integer.parseInt(value);
        }
        return version;
    }

}
