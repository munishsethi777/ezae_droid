package in.learntech.rights.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by baljeetgaheer on 2/18/2016.
 */
public class PreferencesUtil {
    private Context mContext = null;
    private static PreferencesUtil mPreferencesUtil = null;
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


    public void  setLoggedInUserSeq(long userSeq){
        setPreferences(StringConstants.LOGGED_IN_USER_SEQ, String.valueOf(userSeq));
    }

    public void  setLoggedInUserCompanySeq(long companySeq){
        setPreferences(StringConstants.LOGGED_IN_USER_COMPANY_SEQ, String.valueOf(companySeq));
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
