package in.learntech.rights.Managers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.learntech.rights.BusinessObjects.QuestionProgress;
import in.learntech.rights.DataStoreMgr.QuestionProgressDataStore;
import in.learntech.rights.utils.PreferencesUtil;

/**
 * Created by baljeetgaheer on 11/09/17.
 */

public class QuestionProgressMgr {
    private static QuestionProgressMgr sInstance;
    private static QuestionProgressDataStore dataStore;
    private static PreferencesUtil mPreferencesUtil;

    public static synchronized QuestionProgressMgr getInstance(Context context) {
        if (sInstance == null){
            sInstance = new QuestionProgressMgr();
            dataStore = new QuestionProgressDataStore(context);
            mPreferencesUtil = PreferencesUtil.getInstance(context);
        }
        return sInstance;
    }

    public void saveQuestionProgress(JSONObject question,ArrayList ansSeqs,int Score)throws Exception{
        int moduleSeq = question.getInt("moduleSeq");
        int questionSeq = question.getInt("seq");
        int learningPlanSeq = question.getInt("learningPlanSeq");
        for(Object ansSeq : ansSeqs){
            QuestionProgress questionProgress = new QuestionProgress();
            questionProgress.setModuleSeq(moduleSeq);
            questionProgress.setQuestionSeq(questionSeq);
            int selectedAnsSeq = Integer.valueOf(ansSeq.toString());
            questionProgress.setAnsSeq(selectedAnsSeq);
            questionProgress.setStartDate(new Date());
            questionProgress.setEndDate(new Date());
            questionProgress.setTimeUp(false);
            questionProgress.setUploaded(false);
            questionProgress.setLearningPlanSeq(learningPlanSeq);
            int userSeq = mPreferencesUtil.getLoggedInUserSeq();
            questionProgress.setUserSeq(userSeq);
            dataStore.save(questionProgress);
        }

    }
    public void saveQuestionProgress(JSONObject question,String anText)throws Exception{
            int moduleSeq = question.getInt("moduleSeq");
            int questionSeq = question.getInt("seq");
            int learningPlanSeq = question.getInt("learningPlanSeq");
            QuestionProgress questionProgress = new QuestionProgress();
            questionProgress.setModuleSeq(moduleSeq);
            questionProgress.setQuestionSeq(questionSeq);
            questionProgress.setAnsSeq(0);
            questionProgress.setAnsText(anText);
            questionProgress.setStartDate(new Date());
            questionProgress.setEndDate(new Date());
            questionProgress.setTimeUp(false);
            questionProgress.setUploaded(false);
            questionProgress.setLearningPlanSeq(learningPlanSeq);
            int userSeq = mPreferencesUtil.getLoggedInUserSeq();
            questionProgress.setUserSeq(userSeq);
            dataStore.save(questionProgress);
    }

    public JSONArray getProgressJsonArr(int questionSeq){
        int userSeq = mPreferencesUtil.getLoggedInUserSeq();
        List<QuestionProgress> questionProgressList =
                dataStore.getProgressByQuestionSeq(userSeq,questionSeq);

        return toJsonArray(questionProgressList);
    }

    public JSONArray getProgressListByModule(int moduleSeq,int learningPlanSeq){
        int userSeq = mPreferencesUtil.getLoggedInUserSeq();
        List<QuestionProgress> questionProgressList =
                dataStore.getProgressListByModule(userSeq,moduleSeq,learningPlanSeq);
        return toJsonArray(questionProgressList);
    }

    private JSONArray toJsonArray(List<QuestionProgress>questionProgresses){
        JSONArray progressArr = new JSONArray();
        for (QuestionProgress questionProgress : questionProgresses){
            try {
                JSONObject progressJson = new JSONObject();
                progressJson.put("moduleSeq",questionProgress.getModuleSeq());
                progressJson.put("learningPlanSeq",questionProgress.getLearningPlanSeq());
                progressJson.put("answerSeq",questionProgress.getAnsSeq());
                progressJson.put("answerText",questionProgress.getAnsText());
                progressJson.put("questionSeq",questionProgress.getQuestionSeq());
                progressJson.put("progress",100);
                progressJson.put("dated",questionProgress.getEndDate());
                progressJson.put("startDate",questionProgress.getStartDate());
                progressJson.put("isTimeUp",questionProgress.isTimeUp());
                progressJson.put("userSeq",questionProgress.getUserSeq());
                progressArr.put(progressJson);
            }catch (Exception e){
                String message = e.getMessage();
            }
        }
        return  progressArr;
    }

    public boolean deleteByModule(int moduleSeq,int learningPlanSeq){
        int userSeq = mPreferencesUtil.getLoggedInUserSeq();
        return  dataStore.deleteByModule(userSeq,moduleSeq,learningPlanSeq);
    }

    public JSONObject getActivityData(int moduleSeq,int learningPlanSeq){
        int userSeq = mPreferencesUtil.getLoggedInUserSeq();
        JSONObject activityJson = new JSONObject();
        try {
            activityJson.put("userSeq",userSeq);
            activityJson.put("moduleSeq",moduleSeq);
            activityJson.put("learningPlanSeq",learningPlanSeq);
            activityJson.put("score",0);
            activityJson.put("progress",0);
        }catch (Exception e){
            String message = e.getMessage();
        }
        return  activityJson;
    }


}
