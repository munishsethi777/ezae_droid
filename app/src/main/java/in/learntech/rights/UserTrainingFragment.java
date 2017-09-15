package in.learntech.rights;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.SimpleResource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.learntech.rights.Controls.SwipeDirection;
import in.learntech.rights.Managers.QuestionProgressMgr;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;
import in.learntech.rights.utils.sorting.ListFragment;

/**
 * Created by wahyu on 15/11/16.
 */

@SuppressLint("ValidFragment")
public class UserTrainingFragment extends Fragment implements IServiceHandler {
    public static final String SINGLE = "single";
    public static final String MULTI = "multi";
    public static final String LONG_QUESTION = "longQuestion";
    public static final String YES_NO = "yesNo";
    public static final String SAVE_ACTIVITY = "saveActivity";
    public static final String SAVE_QUIZ_PROGRESS = "saveQuizProgress";
    int wizard_page_position;
    private Activity mActivity;
    private JSONArray mQuizProgress;
    private JSONArray mAnswers;
    private String mQuestionType;
    private ConstraintLayout mParentLayout;
    private LinearLayout mOptionsLayout;
    private boolean isQuizProgressExists;
    private JSONObject currentQuestion;
    private ArrayList mSelectedAnsSeqs;
    private QuestionProgressMgr mQuesProgressMgr;
    private Button submitButton;
    private UserTrainingActivity mParentActivity;
    private JSONArray allQuestions ;
    private TextView textView_long_question;
    private Switch switchYesNo;
    private ListFragment listFragment;
    private ServiceHandler mAuthTask;
    private String mCallName;
    private boolean isSavedActivityData;
    private int mUserSeq;
    private int mCompanySeq;
    public  UserTrainingFragment(int position,JSONArray questions) {
        this.wizard_page_position = position;
        this.allQuestions = questions;
        isSavedActivityData = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout_id = R.layout.user_training_fragment;
        mActivity = getActivity();
        mParentLayout= (ConstraintLayout)inflater.inflate(layout_id, container, false);
        TextView textView_question = (TextView)mParentLayout.findViewById(R.id.textView_question);
        mOptionsLayout = (LinearLayout)mParentLayout.findViewById(R.id.optionsLayout);
        mQuesProgressMgr = QuestionProgressMgr.getInstance(getActivity());
        UserMgr userMgr = UserMgr.getInstance(getActivity());
        mUserSeq = userMgr.getLoggedInUserSeq();
        mCompanySeq = userMgr.getLoggedInUserCompanySeq();
        try{
            currentQuestion = allQuestions.getJSONObject(wizard_page_position);
            mQuestionType = currentQuestion.getString("type");
            mAnswers = currentQuestion.getJSONArray("answers");
            mQuizProgress = currentQuestion.getJSONArray("progress");
            JSONArray serverProgress = mQuizProgress;
            JSONArray localProgress =  mQuesProgressMgr.getProgressJsonArr(currentQuestion.getInt("seq"));
            mQuizProgress = LayoutHelper.mergeTwoJsonArray(serverProgress,localProgress);
            isQuizProgressExists = mQuizProgress.length() > 0;
            mParentActivity = (UserTrainingActivity)getActivity();
            mSelectedAnsSeqs =  new ArrayList();
            if(mQuestionType.equals(SINGLE) || mQuestionType.equals(MULTI)){
                addSingleMultiOptionsViews();
            }else if(mQuestionType.equals(LONG_QUESTION)){
                addLongQuestionView();
            }else if(mQuestionType.equals(YES_NO)){
                addYesNoViews();
            }else if(mQuestionType.equals("sequencing")){
                addSequencesViewFragment();
            }
            textView_question.setText(wizard_page_position + 1 + ". " +currentQuestion.getString("title") + " ?");
            addButton();
            submitButton = (Button)mParentLayout.findViewById(R.id.button_submit_progress);
            handleSubmitButton(isQuizProgressExists);
            if(wizard_page_position == 0) {
                executeSaveActivityCall();
            }

        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return mParentLayout;
    }

    private void loadImageRequest(ImageView img, String url) {
        Glide.with(getActivity())
                .load(url)
                .thumbnail(0.01f)
                .centerCrop()
                .into(img);
    }

    private void addYesNoViews()throws Exception{
        String selectedAnsTitle = "";
        if(isQuizProgressExists){
            JSONObject progress = mQuizProgress.getJSONObject(0);
            int ansSeq = progress.getInt("answerSeq");
            JSONObject ans = getAnswerBySeqFromArr(ansSeq);
            if(ans != null)
            selectedAnsTitle = ans.getString("title");
        }
        switchYesNo = new Switch(mActivity);
        switchYesNo.setChecked(selectedAnsTitle.equals("yes"));
        switchYesNo.setEnabled(!isQuizProgressExists);
        mOptionsLayout.addView(switchYesNo);
    }

    private void addLongQuestionView()throws Exception{
        String selectedAnsText = "";
        if(isQuizProgressExists){
            JSONObject progress = mQuizProgress.getJSONObject(0);
            selectedAnsText = progress.getString("answerText");
        }
        textView_long_question = new EditText(mActivity);
        textView_long_question.setSingleLine(false);
        textView_long_question.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        textView_long_question.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textView_long_question.setLines(10);
        textView_long_question.setVerticalScrollBarEnabled(true);
        textView_long_question.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView_long_question.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        textView_long_question.setText(selectedAnsText);
        textView_long_question.setId(R.id.textView_long_question);
        textView_long_question.setTextSize(14);
        textView_long_question.setEnabled(!isQuizProgressExists);
        mOptionsLayout.addView(textView_long_question);
    }

    private void addSingleMultiOptionsViews()throws Exception{
        RadioGroup radioGroup = new RadioGroup(mActivity);
        boolean checked = false;
        for (int i=0; i < mAnswers.length(); i++) {
            JSONObject answer = mAnswers.getJSONObject(i);
            int seq = answer.getInt(("seq"));
            if(isQuizProgressExists){
                checked = isAnswerExistsInProgressArr(seq);
            }
            String title = answer.getString("title");
            String feedback = answer.getString("feedback");
            int marks = answer.getInt("marks");
            int negativeMarks = answer.getInt("negativeMarks");
            if(mQuestionType.equals(MULTI)){
                CheckBox checkBox = new CheckBox(mActivity);
                checkBox.setText(title);
                checkBox.setTag(seq);
                checkBox.setChecked(checked);
                mOptionsLayout.addView(checkBox);
                checkBox.setEnabled(!isQuizProgressExists);
                addClickListener(checkBox);
            }else{
                RadioButton radioButton = new RadioButton(mActivity);
                radioButton.setText(title);
                radioButton.setTag(seq);
                radioButton.setChecked(checked);
                radioButton.setEnabled(!isQuizProgressExists);
                radioGroup.addView(radioButton);
                addClickListener(radioButton);
            }

        }
        if(mQuestionType.equals(SINGLE)){
            mOptionsLayout.addView(radioGroup);

        }
    }

    private void addButton(){
        Button button = new Button(mActivity);
        button.setText("Submit");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        button.setLayoutParams(params);
        //mParentLayout.addView(button);
    }

    private void addSequencesViewFragment()throws Exception {
        JSONArray ansArr = new JSONArray(currentQuestion.getString("answers"));
        if(isQuizProgressExists){
            ansArr = new JSONArray();
            for(int i = 0;i < mQuizProgress.length();i++) {
                JSONObject progress = mQuizProgress.getJSONObject(i);
                JSONObject ansJson = getAnswerBySeqFromArr(progress.getInt("answerSeq"));
                ansArr.put(ansJson);
            }
        }
        listFragment = ListFragment.newInstance(ansArr,!isQuizProgressExists);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.optionsLayout, listFragment, "fragment").commit();
    }

    private boolean addSortedItemSeq()throws Exception{
        ArrayList<Pair<Long,String>> itemList = listFragment.getSortedItemArray();
        int i = 0;
        boolean flag = false;
        boolean isInRightOrder = true;
        for (Pair<Long,String>item : itemList) {
            mSelectedAnsSeqs.add(item.first);
            JSONObject ansJson = mAnswers.getJSONObject(i);
            Long seq = ansJson.getLong("seq");
            if (!flag){
                if (!seq.equals(item.first)) {
                    isInRightOrder = false;
                    flag = true;
                }
             }
            i++;
        }
        return isInRightOrder;
    }

    private void addClickListener(View view ){
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mSelectedAnsSeqs.add(v.getTag().toString());
            }
        });
    }

    private void handleSubmitButton(boolean flag){
        submitButton.setEnabled(!flag);
        if(flag) {
            submitButton.setBackgroundColor(getResources().getColor(R.color.button_light_gray));
            mParentActivity.viewPager.setAllowedSwipeDirection(SwipeDirection.all);
        }else{
            submitButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    LayoutHelper.showToast(getActivity(),"Submitted");
                    saveProgress();
                }
            });
            submitButton.setBackgroundColor(getResources().getColor(R.color.button_magenta));
        }

    }

    public void saveProgress(){
        try {
            String questionType = currentQuestion.getString("type");
            Integer score = 0;
            HashMap<Integer,Integer> scores = new HashMap<>();
            if (questionType.equals("longQuestion")) {
                String ansText = textView_long_question.getText().toString();
                score = currentQuestion.getInt("maxMarks");
                mQuesProgressMgr.saveQuestionProgress(currentQuestion,ansText,score);
            }else{
                if(questionType.equals("yesNo")){
                    String ansTitle = "no";
                    if(switchYesNo.isEnabled()){
                        ansTitle = "yes";
                    }
                    JSONObject ans = getAnswerByTitleFromArr(ansTitle);
                    Object seq = ans.get("seq");
                    mSelectedAnsSeqs.add(0,seq);
                }
                if(questionType.equals("sequencing")){
                    boolean isInRightOrder = addSortedItemSeq();
                    if(isInRightOrder){
                        score = currentQuestion.getInt("maxMarks");
                    }
                    Long ansSeq = (Long)mSelectedAnsSeqs.get(0);
                    scores.put(ansSeq.intValue(),score);
                }else{
                    scores = getSelectedAnswersScore();
                }
                mQuesProgressMgr.saveQuestionProgress(currentQuestion,mSelectedAnsSeqs,scores);
            }
            handleSubmitButton(true);
            if(wizard_page_position == allQuestions.length()-1){
                executeTrainingSubmitCall();
            }
        }catch (Exception e){
            LayoutHelper.showToast(getActivity(),e.getMessage());
        }
    }

    private void executeTrainingSubmitCall(){
        try {
            int moduleSeq = currentQuestion.getInt("moduleSeq");
            int learningPlanSeq = currentQuestion.getInt("learningPlanSeq");
            JSONArray progressArr = mQuesProgressMgr.getProgressListByModule(moduleSeq,learningPlanSeq);
            String jsonArrString = progressArr.toString();
            jsonArrString = URLEncoder.encode(jsonArrString, "UTF-8");
            Object[] args = {mUserSeq,mCompanySeq,jsonArrString};
            String notificationUrl = MessageFormat.format(StringConstants.SUBMIT_QUIZ_PROGRESS,args);
            mAuthTask = new ServiceHandler(notificationUrl, this, SAVE_QUIZ_PROGRESS, getActivity());
            mAuthTask.execute();
        }catch (Exception e){
            LayoutHelper.showToast(getActivity(),e.getMessage());
        }
    }

    private void executeSaveActivityCall(){
        if(!isQuizProgressExists && !isSavedActivityData) {
            try {
                int moduleSeq = currentQuestion.getInt("moduleSeq");
                int learningPlanSeq = currentQuestion.getInt("learningPlanSeq");
                JSONObject activityJson = mQuesProgressMgr.getActivityData(moduleSeq, learningPlanSeq);
                String jsonString = activityJson.toString();
                jsonString = URLEncoder.encode(jsonString, "UTF-8");
                Object[] args = {mUserSeq,mCompanySeq,jsonString};
                String notificationUrl = MessageFormat.format(StringConstants.SAVE_ACTIVITY, args);
                mAuthTask = new ServiceHandler(notificationUrl, this, SAVE_ACTIVITY, getActivity());
                mAuthTask.execute();
            } catch (Exception e) {
                LayoutHelper.showToast(getActivity(), e.getMessage());
            }
        }
    }

    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        boolean success;
        String message;
        try {
            success = response.getInt(StringConstants.SUCCESS) == 1 ? true : false;
            message = response.getString(StringConstants.MESSAGE);
            if(success){
                if(mCallName.equals(SAVE_QUIZ_PROGRESS)) {
                    int moduleSeq = currentQuestion.getInt("moduleSeq");
                    int learningPlanSeq = currentQuestion.getInt("learningPlanSeq");
                    mQuesProgressMgr.deleteByModule(moduleSeq, learningPlanSeq);
                    LayoutHelper.showToast(getActivity(),message);
                    goToTrainingActivity();
                }else{
                    isSavedActivityData = true;
                }
            }
        }catch (Exception e){
            message = e.getMessage();
            LayoutHelper.showToast(getActivity(),message);
        }

    }

    private void  goToTrainingActivity(){
        Intent intent = new Intent(getActivity(),MyTrainings.class);
        startActivity(intent);
    }
    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    private JSONObject getAnswerBySeqFromArr(int seq)throws Exception{
        for (int i=0; i < mAnswers.length(); i++) {
            JSONObject ansJson = mAnswers.getJSONObject(i);
            int ansSeq = ansJson.getInt("seq");
            if(ansSeq == seq){
                return ansJson;
            }
        }
        return null;
    }

    private JSONObject getAnswerByTitleFromArr(String title)throws Exception{
        for (int i=0; i < mAnswers.length(); i++) {
            JSONObject ansJson = mAnswers.getJSONObject(i);
            String ansTitle = ansJson.getString("title");
            if(ansTitle.equals(title)){
                return ansJson;
            }
        }
        return null;
    }

    private HashMap<Integer,Integer> getSelectedAnswersScore()throws Exception{
        HashMap<Integer,Integer> scores = new HashMap<Integer,Integer>() ;
        for(Object ansSeq : mSelectedAnsSeqs){
            int seq = Integer.parseInt((String)ansSeq);
            JSONObject ans = getAnswerBySeqFromArr(seq);
            if(ans != null)
            scores.put(seq,ans.getInt("marks"));
        }
        return scores;
    }
    private boolean isAnswerExistsInProgressArr(int seq)throws Exception{
        boolean flag = false;
        for (int i=0; i < mQuizProgress.length(); i++) {
            JSONObject ansJson = mQuizProgress.getJSONObject(i);
            int ansSeq = ansJson.getInt("answerSeq");
            if(ansSeq == seq){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
