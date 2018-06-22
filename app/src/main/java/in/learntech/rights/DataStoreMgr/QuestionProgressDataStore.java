package in.learntech.rights.DataStoreMgr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.learntech.rights.BusinessObjects.QuestionProgress;

/**
 * Created by baljeetgaheer on 11/09/17.
 */

public class QuestionProgressDataStore {
    private Context mContext;
    private DBUtil mDBUtil;

    public static final String COLUMN_SEQ = "id";
    public static final String COLUMN_USER_SEQ = "userseq";
    public static final String COLUMN_MODULE_SEQ = "moduleseq";
    public static final String COLUMN_QUESTION_SEQ = "questionseq";
    public static final String COLUMN_ANS_SEQ = "ansseq";
    public static final String COLUMN_ANS_TEXT = "anstext";
    public static final String COLUMN_IS_TIME_UP = "istimeUp";
    public static final String COLUMN_START_DATE = "startdate";
    public static final String COLUMN_END_DATE = "enddate";
    public static final String COLUMN_IS_UPLOADED = "isuploaded";
    public static final String COLUMN_LEARNING_PLAN_SEQ = "learningplanseq";
    public static final String COLUMN_SCORE = "score";
    public static final String TABLE_NAME = "questionprogress";
    public static final String FIND_BY_QUESTION_SEQ = "Select * from questionprogress where " + COLUMN_USER_SEQ + "= {0,number,#} and "
            + COLUMN_QUESTION_SEQ + "= {1,number,#} and "
            + COLUMN_MODULE_SEQ + "= {2,number,#} and "
            + COLUMN_LEARNING_PLAN_SEQ + "= {3,number,#} ";
    public static final String FIND_MODULE_PROGRESSES = "Select * from questionprogress where "
            + COLUMN_USER_SEQ + "= {0,number,#} and "
            + COLUMN_MODULE_SEQ + "= {1,number,#} and "
            + COLUMN_LEARNING_PLAN_SEQ + "= {2,number,#} ";


    public static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "(" + COLUMN_SEQ + " INTEGER PRIMARY KEY, "
            + COLUMN_USER_SEQ + " INTEGER, "
            + COLUMN_MODULE_SEQ + " INTEGER, "
            + COLUMN_QUESTION_SEQ + " INTEGER, "
            + COLUMN_ANS_SEQ + " INTEGER, "
            + COLUMN_ANS_TEXT + " STRING, "
            + COLUMN_IS_TIME_UP + " BOOLEAN, "
            + COLUMN_START_DATE + " LONG, "
            + COLUMN_END_DATE + " LONG, "
            + COLUMN_IS_UPLOADED  + " BOOLEAN, "
            + COLUMN_LEARNING_PLAN_SEQ + " INTEGER, "
            + COLUMN_SCORE + " INTEGER ) ";

    public QuestionProgressDataStore(Context context){
        mContext = context;
        mDBUtil = DBUtil.getInstance(mContext);
    }

    public long save(QuestionProgress progress){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_SEQ,progress.getUserSeq());
        values.put(COLUMN_MODULE_SEQ,progress.getModuleSeq());
        values.put(COLUMN_QUESTION_SEQ,progress.getQuestionSeq());
        values.put(COLUMN_ANS_SEQ,progress.getAnsSeq());
        values.put(COLUMN_ANS_TEXT,progress.getAnsText());
        values.put(COLUMN_IS_TIME_UP,progress.isTimeUp());
        values.put(COLUMN_START_DATE,progress.getStartDate().getTime());
        values.put(COLUMN_END_DATE,progress.getEndDate().getTime());
        values.put(COLUMN_IS_UPLOADED,progress.isUploaded());
        values.put(COLUMN_LEARNING_PLAN_SEQ,progress.getLearningPlanSeq());
        values.put(COLUMN_SCORE,progress.getScore());
        int seq = progress.getId();
        return mDBUtil.addOrUpdateUser(this.TABLE_NAME,values,String.valueOf(seq));
    }

    public List<QuestionProgress> getProgressByQuestionSeq(int userSeq, int questionSeq,int moduleSeq, int lpSeq){
        Integer[] args  = {userSeq,questionSeq,moduleSeq,lpSeq};
        String query = MessageFormat.format(FIND_BY_QUESTION_SEQ,args);
        Cursor c = mDBUtil.executeQuery(query);
        List<QuestionProgress> questionProgressesList = new ArrayList<QuestionProgress>();
        if(c.moveToFirst()){
            do{
                QuestionProgress questionProgress = populateObject(c);
                questionProgressesList.add(questionProgress);
            }while(c.moveToNext());
        }
        c.close();
        return  questionProgressesList;
    }

    public List<QuestionProgress> getProgressListByModule(int userSeq, int moduleSeq,int LearningPlanSeq){
        Integer[] args  = {userSeq,moduleSeq,LearningPlanSeq};
        String query = MessageFormat.format(FIND_MODULE_PROGRESSES,args);
        Cursor c = mDBUtil.executeQuery(query);
        List<QuestionProgress> questionProgressesList = CursorToList(c);
        return  questionProgressesList;
    }



    private List<QuestionProgress> CursorToList(Cursor c){
        List<QuestionProgress> questionProgressesList = new ArrayList<QuestionProgress>();
        if(c.moveToFirst()){
            do{
                QuestionProgress questionProgress = populateObject(c);
                questionProgressesList.add(questionProgress);
            }while(c.moveToNext());
        }
        c.close();
        return  questionProgressesList;
    }

    private QuestionProgress populateObject( Cursor c){
        int id = c.getInt(0);
        int userSeq = c.getInt(1);
        int moduleSeq = c.getInt(2);
        int questionSeq = c.getInt(3);
        int ansSeq = c.getInt(4);
        String ansText = c.getString(5);
        int isTimeUp = c.getInt(6);
        Long startDate = c.getLong(7);
        Long endDate = c.getLong(8);
        int isUploaded = c.getInt(9);
        int learningPlanSeq = c.getInt(10);
        int score = c.getInt(11);
        QuestionProgress questionProgress = new QuestionProgress();
        questionProgress.setId(id);
        questionProgress.setUserSeq(userSeq);
        questionProgress.setModuleSeq(moduleSeq);
        questionProgress.setQuestionSeq(questionSeq);
        questionProgress.setAnsSeq(ansSeq);
        questionProgress.setAnsText(ansText);
        questionProgress.setTimeUp(isTimeUp == 1);
        questionProgress.setStartDate(new Date(startDate));
        questionProgress.setEndDate(new Date(endDate));
        questionProgress.setUploaded(isUploaded == 1);
        questionProgress.setLearningPlanSeq(learningPlanSeq);
        questionProgress.setScore(score);
        return questionProgress;
    }

    public boolean deleteByModule(int userSeq, int moduleSeq,int LearningPlanSeq){
        String whereClause = COLUMN_USER_SEQ + " = ? and " + COLUMN_MODULE_SEQ + "= ? and " + COLUMN_LEARNING_PLAN_SEQ + "= ? ";
        String args[] = {String.valueOf(userSeq),String.valueOf(moduleSeq),String.valueOf(LearningPlanSeq)};
        boolean flag = mDBUtil.delete(TABLE_NAME, whereClause, args);
        return flag;
    }

}
