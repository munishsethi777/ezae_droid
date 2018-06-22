package in.learntech.rights.DataStoreMgr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.learntech.rights.BusinessObjects.PendingToUpload;
import in.learntech.rights.BusinessObjects.QuestionProgress;

/**
 * Created by baljeetgaheer on 09/03/18.
 */

public class ModuleDataStore {
    private Context mContext;
    private DBUtil mDBUtil;
    public ModuleDataStore (Context context) {
        mContext = context;
        mDBUtil = DBUtil.getInstance(mContext);
    }
    public static final String COLUMN_SEQ = "id";
    public static final String COLUMN_MODULE_SEQ = "moduleseq";
    public static final String COLUMN_LEARNING_PLAN_SEQ = "learningplanseq";
    public static final String COLUMN_USER_SEQ = "userseq";
    public static final String COLUMN_DATED = "dated";
    public static final String TABLE_NAME = "pendingmodules";
    public static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "(" + COLUMN_SEQ + " INTEGER PRIMARY KEY, "
            + COLUMN_MODULE_SEQ + " INTEGER,"
            + COLUMN_LEARNING_PLAN_SEQ + " INTEGER," +
            COLUMN_USER_SEQ + " INTEGER, " +
            COLUMN_DATED + " LONG)";

    public static final String FIND_MODULE_PROGRESSES = "Select * from pendingmodules where "
            + COLUMN_USER_SEQ + "= {0,number,#}";

    public long save(PendingToUpload module){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_SEQ,module.getUserSeq());
        values.put(COLUMN_MODULE_SEQ,module.getModuleSeq());
        values.put(COLUMN_LEARNING_PLAN_SEQ,module.getLearningPlanSeq());
        values.put(COLUMN_DATED,module.getDated().getTime());
        int seq = module.getId();
        return mDBUtil.addOrUpdateUser(this.TABLE_NAME,values,String.valueOf(seq));
    }

    private PendingToUpload populateObject(Cursor c){
        int id = c.getInt(0);
        int moduleSeq = c.getInt(1);
        int lpSeq = c.getInt(2);
        int userSeq = c.getInt(3);
        long dated = c.getLong(4);
        PendingToUpload module = new PendingToUpload();
        module.setId(id);
        module.setModuleSeq(moduleSeq);
        module.setLearningPlanSeq(lpSeq);
        module.setUserSeq(userSeq);
        module.setDated(new Date(dated));
        return module;
    }

    public List<PendingToUpload> getPendingModules(int userSeq){
        Integer[] args  = {userSeq};
        String query = MessageFormat.format(FIND_MODULE_PROGRESSES,args);
        Cursor c = mDBUtil.executeQuery(query);
        List<PendingToUpload> questionProgressesList = CursorToList(c);
        return  questionProgressesList;
    }

    private List<PendingToUpload> CursorToList(Cursor c){
        List<PendingToUpload> pendingModules = new ArrayList<PendingToUpload>();
        if(c.moveToFirst()){
            do{
                PendingToUpload module = populateObject(c);
                pendingModules.add(module);
            }while(c.moveToNext());
        }
        c.close();
        return  pendingModules;
    }

    public boolean deletePendingModules(int userSeq, int moduleSeq,int LearningPlanSeq){
        String whereClause = COLUMN_USER_SEQ + " = ? and " + COLUMN_MODULE_SEQ + "= ? and " + COLUMN_LEARNING_PLAN_SEQ + "= ? ";
        String args[] = {String.valueOf(userSeq),String.valueOf(moduleSeq),String.valueOf(LearningPlanSeq)};
        boolean flag = mDBUtil.delete(TABLE_NAME, whereClause, args);
        return flag;
    }

}
