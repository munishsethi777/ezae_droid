package in.learntech.rights.DataStoreMgr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import in.learntech.rights.BusinessObjects.User;
import in.learntech.rights.utils.PreferencesUtil;


/**
 * Created by baljeetgaheer on 02/09/17.
 */

public class DBUtil extends SQLiteOpenHelper {
    // Database Info
    private static final String TAG = "satya.DBUtil";
    private static final String DATABASE_NAME = "jumpkingapp";
    private static final int DATABASE_VERSION = 4;
    private static DBUtil sInstance;
    private static String CREATE_TABLE;
    private static Context context;

    public DBUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static synchronized DBUtil getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null){
            sInstance = new DBUtil(context.getApplicationContext());
            DBUtil.context = context;
        }
        return sInstance;
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = UserDataStore.CREATE_TABLE;
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_QUESTION_PROGRESS = QuestionProgressDataStore.CREATE_TABLE;
        db.execSQL(CREATE_QUESTION_PROGRESS);

        String CREATE_COMPANY_USERS = CompanyUserDataStore.CREATE_TABLE;
        db.execSQL(CREATE_COMPANY_USERS);

        String CREATE_PENDING_MODULES = ModuleDataStore.CREATE_TABLE;
        db.execSQL(CREATE_PENDING_MODULES);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            PreferencesUtil.getInstance( DBUtil.context).resetPreferences();
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + QuestionProgressDataStore.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + CompanyUserDataStore.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ModuleDataStore.TABLE_NAME);
            onCreate(db);
        }
    }

    public void add(String tableName,ContentValues values) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(tableName, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add " + tableName + " to database");
        } finally {
            db.endTransaction();
        }
    }
    private String PRIMARY_KEY_COL = "id";

    public long addOrUpdateUser(String tableName,ContentValues values,String value) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            if(!value.equals("0")){
                int rows = db.update(tableName, values, PRIMARY_KEY_COL + "= ?", new String[]{value});

                // Check if update succeeded
                if (rows == 1) {
                    userId = Integer.parseInt(value);
                    db.setTransactionSuccessful();
                }
            }
            else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(tableName, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
            throw e;
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public Cursor getAll(String sql) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
    public Cursor executeQuery(String sql) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
    public int getCount(String SQL){
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCount= db.rawQuery(SQL, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    public boolean deleteAll(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = db.delete(tableName,null,null)>0;
        return flag;
    }

    public boolean delete(String tableName,String whereClause,String args[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = db.delete(tableName,whereClause,args)>0;
        return flag;
    }
}

