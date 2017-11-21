package in.learntech.rights.Managers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.learntech.rights.BusinessObjects.CompanyUser;
import in.learntech.rights.DataStoreMgr.CompanyUserDataStore;
import in.learntech.rights.utils.PreferencesUtil;

/**
 * Created by baljeetgaheer on 31/10/17.
 */

public class CompanyUserManager {
    private static CompanyUserManager sInstance;
    private static CompanyUserDataStore companyUsersDataStore;
    private static PreferencesUtil mPreferencesUtil;

    public static synchronized CompanyUserManager getInstance(Context context) {
        if (sInstance == null){
            sInstance = new CompanyUserManager();
            companyUsersDataStore = new CompanyUserDataStore(context);
            mPreferencesUtil = PreferencesUtil.getInstance(context);
        }
        return sInstance;
    }

    public void saveUsersFromResponse(JSONObject response)throws Exception {
        JSONArray jsonArray = response.getJSONArray("users");
        companyUsersDataStore.deleteAll();
        int companySeq = mPreferencesUtil.getLoggedInUserCompanySeq();
        for (int i=0;i < jsonArray.length() ; i++) {
            JSONObject userJson = jsonArray.getJSONObject(i);
            int seq = userJson.getInt("seq");
            String type = userJson.getString("type");
            String userName = userJson.getString("username");
            String image = userJson.getString("image");
            String fullName = userJson.getString("name");
            CompanyUser companyUser = new CompanyUser();
            companyUser.setSeq(seq);
            companyUser.setUserName(userName);
            companyUser.setType(type);
            companyUser.setImageName(image);
            companyUser.setCompanySeq(companySeq);
            companyUser.setFullName(fullName);
            companyUsersDataStore.save(companyUser);
        }
    }

    public ArrayList<CompanyUser> getCompanyUsersForLoggedInUser(){
        return companyUsersDataStore.getCompanyUsersByCompanySeq(
                mPreferencesUtil.getLoggedInUserCompanySeq());
    }

}
