package in.learntech.rights.Managers;

import android.content.Context;

import org.json.JSONObject;

import in.learntech.rights.BusinessObjects.User;
import in.learntech.rights.DataStoreMgr.UserDataStore;
import in.learntech.rights.utils.PreferencesUtil;
import in.learntech.rights.utils.StringConstants;

/**
 * Created by baljeetgaheer on 02/09/17.
 */

public class UserMgr {
    private static UserMgr sInstance;
    private static UserDataStore userDataStore;
    private static PreferencesUtil mPreferencesUtil;

    public static synchronized UserMgr getInstance(Context context) {
        if (sInstance == null){
            sInstance = new UserMgr();
            userDataStore = new UserDataStore(context);
            mPreferencesUtil = PreferencesUtil.getInstance(context);
        }
        return sInstance;
    }


    public void saveUserFromResponse(JSONObject response)throws Exception{
        JSONObject userJson = response.getJSONObject("user");
        int userSeq = userJson.getInt("id");
        String userName = userJson.getString("username");
        String email = userJson.getString("email");
        int companySeq = userJson.getInt("companyseq");
        String userImage = userJson.getString("userImage");
        boolean isManager = false;
        User existingUser = getUserByUserSeq(userSeq);
        User user = null;
        if(existingUser !=  null){
            user = existingUser;
        }else{
            user = new User();
        }
        user.setUserSeq(userSeq);
        user.setUserName(userName);
        user.setFullName(null);
        user.setEmail(email);
        user.setCompanySeq(companySeq);
        user.setManager(isManager);
        user.setUserImageUrl(userImage);
        userDataStore.save(user);
        mPreferencesUtil.setLoggedInUserSeq(userSeq);
        mPreferencesUtil.setLoggedInUserCompanySeq(companySeq);
    }

    public User getUserByUserSeq(int userSeq){
        User user = userDataStore.getUserByUserSeq(userSeq);
        return user;
    }

    public User getLoggedInUser(){
        int userSeq  = mPreferencesUtil.getLoggedInUserSeq();
        User user = userDataStore.getUserByUserSeq(userSeq);
        return user;
    }

    public String getLoggedInUserName(){
        User user = this.getLoggedInUser();
        return user.getUserName();
    }

    public int getLoggedInUserSeq(){
        int userSeq  = mPreferencesUtil.getLoggedInUserSeq();
        return userSeq;
    }

    public int getLoggedInUserCompanySeq(){
        int companySeq  = mPreferencesUtil.getLoggedInUserCompanySeq();
        return companySeq;
    }

    public String getLoggedInUserImageUrl(){
        int userSeq  = mPreferencesUtil.getLoggedInUserSeq();
        User user = userDataStore.getUserByUserSeq(userSeq);
        String userImage = StringConstants.USER_DUMMY_IMAGE_NAME;
        if(user.getUserImageUrl() != null && !user.getUserImageUrl().equals("null")){
            userImage = user.getUserImageUrl();
        }
        return StringConstants.IMAGE_URL + "UserImages/"+userImage;
    }

    public void resentPreferences(){
        mPreferencesUtil.resetPreferences();
    }

    public boolean isUserLoggedIn(){
        return this.getLoggedInUserSeq() > 0;
    }

    public boolean isUserExistsWithUsername(String userName){
        return userDataStore.isUserExistsWithUsername(userName);
    }


}
