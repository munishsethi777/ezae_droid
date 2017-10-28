package in.learntech.rights.DataStoreMgr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.MessageFormat;

import in.learntech.rights.BusinessObjects.User;

/**
 * Created by baljeetgaheer on 04/09/17.
 */

public class UserDataStore{

    private Context mContext;
    private DBUtil mDBUtil;




    public static final String COLUMN_SEQ = "id";
    public static final String COLUMN_USER_SEQ = "userseq";
    public static final String COLUMN_NAME = "username";
    public static final String COLUMN_FULL_NAME = "fullname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_COMPANY_SEQ = "companyseq";
    public static final String COLUMN_IS_MANAGER = "ismanager";
    public static final String COLUMN_USER_IMAGE_URL = "userimageurl";
    public static final String COLUMN_PROFILES = "profiles";

    public static final String TABLE_NAME = "users";

    public static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "(" + COLUMN_SEQ + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_EMAIL + " TEXT, "
            + COLUMN_FULL_NAME + " TEXT, "
            + COLUMN_USER_SEQ + " INTEGER, "
            + COLUMN_COMPANY_SEQ + " INTEGER, "
            + COLUMN_IS_MANAGER + " BOOLEAN, " + COLUMN_USER_IMAGE_URL + " TEXT ,"
            + COLUMN_PROFILES + " TEXT )";
    public static final String FIND_USER_BY_SEQ = "Select * from users where " + COLUMN_USER_SEQ + "={0}";
    public static final String COUNT_USER_BY_USER_NAME = "Select count(*) from users where " + COLUMN_NAME + "={0}";

    public UserDataStore(Context context){
        mContext = context;
        mDBUtil = DBUtil.getInstance(mContext);
    }
    public long save(User user){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_SEQ,user.getUserSeq());
        values.put(COLUMN_NAME,user.getUserName());
        values.put(COLUMN_FULL_NAME,user.getFullName());
        values.put(COLUMN_EMAIL,user.getEmail());
        values.put(COLUMN_COMPANY_SEQ,user.getCompanySeq());
        values.put(COLUMN_IS_MANAGER,user.isManager());
        values.put(COLUMN_USER_IMAGE_URL,user.getUserImageUrl());
        values.put(COLUMN_PROFILES,user.getProfiles());
        int seq = user.getId();
        return mDBUtil.addOrUpdateUser(this.TABLE_NAME,values,String.valueOf(seq));
    }



    public User getUserByUserSeq(int userSeq){
        Object[] args  = {userSeq};
        String query = MessageFormat.format(FIND_USER_BY_SEQ,args);
        Cursor c = mDBUtil.executeQuery(query);
        if(c.moveToFirst()) {
            User user = populateObject(c);
            return user;
        }
        return  null;
    }
    public boolean isUserExistsWithUsername(String userName){
        Object[] args  = {"'"+userName+"'"};
        String query = MessageFormat.format(COUNT_USER_BY_USER_NAME,args);
        int count = mDBUtil.getCount(query);
        return  count > 0;
    }

    private User populateObject( Cursor c){
        int id = c.getInt(0);
        String userName = c.getString(1);
        String email = c.getString(2);
        String fullName = c.getString(3);
        int userSeq = c.getInt(4);
        int companySeq = c.getInt(5);
        String isManager = c.getString(6);
        String imageUrl = c.getString(7);
        String userProfiles = c.getString(8);

        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setUserSeq(userSeq);
        user.setCompanySeq(companySeq);
        user.setManager(isManager == "1");
        user.setUserImageUrl(imageUrl);
        user.setProfiles(userProfiles);
        return user;
    }

}
