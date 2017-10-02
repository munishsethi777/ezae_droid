package in.learntech.rights.Managers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.learntech.rights.BusinessObjects.QuestionProgress;
import in.learntech.rights.DataStoreMgr.QuestionProgressDataStore;
import in.learntech.rights.utils.DateUtil;
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

    public void saveQuestionProgress(JSONObject question,ArrayList ansSeqs,HashMap<Integer,Integer> scores,Date sDate)throws Exception{
        int moduleSeq = question.getInt("moduleSeq");
        int questionSeq = question.getInt("seq");
        int learningPlanSeq = question.getInt("learningPlanSeq");
        for(Object ansSeq : ansSeqs){
            QuestionProgress questionProgress = new QuestionProgress();
            questionProgress.setModuleSeq(moduleSeq);
            questionProgress.setQuestionSeq(questionSeq);
            int selectedAnsSeq = Integer.valueOf(ansSeq.toString());
            questionProgress.setAnsSeq(selectedAnsSeq);
            questionProgress.setStartDate(sDate);
            questionProgress.setEndDate(new Date());
            questionProgress.setTimeUp(false);
            questionProgress.setUploaded(false);
            questionProgress.setLearningPlanSeq(learningPlanSeq);
            int score = 0;
            if(scores.containsKey(selectedAnsSeq)){
                score = scores.get(selectedAnsSeq);
            }
            questionProgress.setScore(score);
            int userSeq = mPreferencesUtil.getLoggedInUserSeq();
            questionProgress.setUserSeq(userSeq);
            dataStore.save(questionProgress);
        }

    }

    public void saveQuestionProgress(JSONObject question,String anText,int score,Date sDate,boolean isTimeUp)throws Exception{
            int moduleSeq = question.getInt("moduleSeq");
            int questionSeq = question.getInt("seq");
            int learningPlanSeq = question.getInt("learningPlanSeq");
            QuestionProgress questionProgress = new QuestionProgress();
            questionProgress.setModuleSeq(moduleSeq);
            questionProgress.setQuestionSeq(questionSeq);
            questionProgress.setAnsSeq(0);
            questionProgress.setAnsText(anText);
            questionProgress.setStartDate(sDate);
            questionProgress.setEndDate(new Date());
            questionProgress.setTimeUp(isTimeUp);
            questionProgress.setUploaded(false);
            questionProgress.setLearningPlanSeq(learningPlanSeq);
            questionProgress.setScore(score);
            int userSeq = mPreferencesUtil.getLoggedInUserSeq();
            questionProgress.setUserSeq(userSeq);
            dataStore.save(questionProgress);
    }

    public JSONArray getProgressJsonArr(int questionSeq, int moduleSeq,int learningPlanSeq){
        int userSeq = mPreferencesUtil.getLoggedInUserSeq();
        List<QuestionProgress> questionProgressList =
                dataStore.getProgressByQuestionSeq(userSeq,questionSeq,moduleSeq,learningPlanSeq);
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
                String endDateStr = DateUtil.dateToString(questionProgress.getEndDate());
                progressJson.put("dated",endDateStr);
                String startDateStr = DateUtil.dateToString(questionProgress.getStartDate());
                progressJson.put("startDate",startDateStr);
                int timeUp = 0;
                if(questionProgress.isTimeUp()){
                    timeUp = 1;
                }
                progressJson.put("isTimeUp",timeUp);
                progressJson.put("userSeq",questionProgress.getUserSeq());
                progressJson.put("score",questionProgress.getScore());
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

    public long getTimeConsumed(JSONArray quesJsonArr)throws Exception{
        long diffInMinutes = 0;
        for(int i = 0;i < quesJsonArr.length();i++) {
            JSONObject ques = quesJsonArr.getJSONObject(i);
            int moduleSeq = ques.getInt("moduleSeq");
            int lpSeq = ques.getInt("learningPlanSeq");
            JSONArray quesProgress = ques.getJSONArray("progress");
            if(quesProgress.length() > 0){
                JSONObject progJsonn = quesProgress.getJSONObject(0);
                diffInMinutes += getTimeDiffFromQuesProg(progJsonn);
            }
            JSONArray localProgress = getProgressJsonArr(ques.getInt("seq"),moduleSeq,lpSeq);
            if(localProgress.length() > 0){
                JSONObject localProgJson = localProgress.getJSONObject(0);
                diffInMinutes += getTimeDiffFromQuesProg(localProgJson);
            }
        }
        return diffInMinutes;
    }

    public int getTotalSubmittedQuesCount(JSONArray quesJsonArr)throws  Exception{
        int totalSubmittedCount = 0;
        for(int i = 0;i < quesJsonArr.length();i++) {
            JSONObject ques = quesJsonArr.getJSONObject(i);

            int moduleSeq = ques.getInt("moduleSeq");
            int lpSeq = ques.getInt("learningPlanSeq");
            JSONArray quesProgress = ques.getJSONArray("progress");
            if(quesProgress.length() > 0){
                totalSubmittedCount++;
            }
            JSONArray localProgress = getProgressJsonArr(ques.getInt("seq"),moduleSeq,lpSeq);
            if(localProgress.length() > 0){
                totalSubmittedCount++;
            }
        }
        return totalSubmittedCount;
    }

    private long getTimeDiffFromQuesProg(JSONObject quesJson)throws Exception{
        String startDateStr = quesJson.getString("startDate");
        String endDateStr = quesJson.getString("dated");
        Date startDate = DateUtil.stringToDate(startDateStr);
        Date endDate = DateUtil.stringToDate(endDateStr);
        return endDate.getTime() - startDate.getTime();
    }
}
