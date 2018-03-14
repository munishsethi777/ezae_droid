package in.learntech.rights;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import in.learntech.rights.Controls.SwipeDirection;
import in.learntech.rights.Managers.ModuleMgr;
import in.learntech.rights.Managers.QuestionProgressMgr;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.AppStatus;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;
import in.learntech.rights.utils.seekbar.CustomSeekBar;
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
    public static final String SUBMITTED_SUCCESSFULLY = "Submitted successfully";
    public static final String SEQUENCING = "sequencing";
    public static final String WEB_PAGE = "web_page";
    public static final String DOC = "doc";
    public static final String MEDIA = "media";
    public static final String LIKAT_SCALE = "likartScale";
    public static final String ESTIMATE_PERCENTAGE = "estimatePercentage";
    public int wizard_page_position;
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
    private Button okButton;
    private JSONObject mModuleJson;
    private UserTrainingActivity mParentActivity;
    private JSONArray allQuestions ;
    private TextView mTextView_Long_question;
    private Switch switchYesNo;
    private ListFragment listFragment;
    private ServiceHandler mAuthTask;
    private String mCallName;
    private boolean isSavedActivityData;
    private int mUserSeq;
    private int mCompanySeq;
    private TextView textVew_feedback_success;
    private TextView textVew_feedback_error;
    List<String> feedbacks_success_list;
    List<String> feedbacks_error_list;
    private String mModuleType;
    private RadioGroup radioGroup;
    private WebView webView;
    public  UserTrainingFragment(int position,JSONObject moduleJson) {
        this.wizard_page_position = position;
        mModuleJson = moduleJson;
        isSavedActivityData = false;
        try{
            allQuestions = moduleJson.getJSONArray("questions");
        }catch (Exception e){}
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
        feedbacks_success_list = new ArrayList<>();
        feedbacks_error_list = new ArrayList<>();
        try{
            currentQuestion = allQuestions.getJSONObject(wizard_page_position);
            mQuestionType = currentQuestion.getString("type");
            mAnswers = currentQuestion.getJSONArray("answers");
            mQuizProgress = currentQuestion.getJSONArray("progress");
            int moduleSeq = currentQuestion.getInt("moduleSeq");
            int learningPlanSeqSeq = currentQuestion.getInt("learningPlanSeq");
            JSONArray serverProgress = mQuizProgress;
            JSONArray localProgress =  mQuesProgressMgr.getProgressJsonArr(currentQuestion.getInt("seq"),moduleSeq,learningPlanSeqSeq);
            mQuizProgress = LayoutHelper.mergeTwoJsonArray(serverProgress,localProgress);
            isQuizProgressExists = mQuizProgress.length() > 0;
            mParentActivity = (UserTrainingActivity)getActivity();
            mSelectedAnsSeqs =  new ArrayList();
            mModuleType = mModuleJson.getString("moduletype");
            submitButton = (Button)mParentLayout.findViewById(R.id.button_submit_progress);
            okButton = (Button)mParentLayout.findViewById(R.id.button_feedbackOkay);
            if(mQuestionType.equals(SINGLE) || mQuestionType.equals(MULTI)){
                addSingleMultiOptionsViews();
            }else if(mQuestionType.equals(LONG_QUESTION)){
                addLongQuestionView();
            }else if(mQuestionType.equals(YES_NO)){
                addYesNoViews();
            }else if(mQuestionType.equals(SEQUENCING)){
                addSequencesViewFragment();
            }else if(mQuestionType.equals(WEB_PAGE) ||
                    mQuestionType.equals(DOC) ||
                    mQuestionType.equals(MEDIA)){
                    addWebView();
            }else if(mQuestionType.equals(LIKAT_SCALE)){
                addCustomSeekBar();
            }else if(mQuestionType.equals(ESTIMATE_PERCENTAGE)){
                addSeekBar();
            }
            textView_question.setText(wizard_page_position + 1 + ". " +currentQuestion.getString("title"));
            addButton();
            submitButton = (Button)mParentLayout.findViewById(R.id.button_submit_progress);
            textVew_feedback_success = (TextView)mParentLayout.findViewById(R.id.textView_feedbackSuccess);
            textVew_feedback_error = (TextView)mParentLayout.findViewById(R.id.textView_feedbackError);
            handleSubmitButton(isQuizProgressExists);
            boolean isShowFeedback = mModuleJson.getInt("isshowfeedback") > 0;
            if(isQuizProgressExists && isShowFeedback){
                showFeedback();
            }
            if(wizard_page_position == 0) {
                executeSaveActivityCall();
            }
        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return mParentLayout;
    }

    private void addYesNoViews()throws Exception{
        String selectedAnsTitle = "";
        boolean isChecked = false;
        if(isQuizProgressExists){
            JSONObject progress = mQuizProgress.getJSONObject(0);
            int ansSeq = progress.getInt("answerSeq");
            JSONObject ans = getAnswerBySeqFromArr(ansSeq);
            if(ans != null)
            selectedAnsTitle = ans.getString("title");
            isChecked = selectedAnsTitle.equals("yes");
            mSelectedAnsSeqs.add(ans.getString("seq"));
        }
        switchYesNo = new Switch(mActivity);
        switchYesNo.setChecked(isChecked);
        switchYesNo.setEnabled(!isQuizProgressExists);
        mOptionsLayout.addView(switchYesNo);
        if(isQuizProgressExists){
            getSelectedAnswersScore();
        }
    }

    private void addLongQuestionView()throws Exception{
        String selectedAnsText = "";
        if(isQuizProgressExists){
            JSONObject progress = mQuizProgress.getJSONObject(0);
            selectedAnsText = progress.getString("answerText");
            feedbacks_success_list.add(SUBMITTED_SUCCESSFULLY);
        }
        mTextView_Long_question = new EditText(mActivity);
        mTextView_Long_question.setSingleLine(false);
        mTextView_Long_question.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        mTextView_Long_question.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mTextView_Long_question.setLines(10);
        mTextView_Long_question.setVerticalScrollBarEnabled(true);
        mTextView_Long_question.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTextView_Long_question.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        mTextView_Long_question.setText(selectedAnsText);
        mTextView_Long_question.setId(R.id.textView_long_question);
        mTextView_Long_question.setTextSize(14);
        mTextView_Long_question.setEnabled(!isQuizProgressExists);
        mTextView_Long_question.setGravity(Gravity.TOP);
        mOptionsLayout.addView(mTextView_Long_question);
    }

    private void addSingleMultiOptionsViews()throws Exception{
        radioGroup = new RadioGroup(mActivity);
        boolean checked = false;
        for (int i=0; i < mAnswers.length(); i++) {
            JSONObject answer = mAnswers.getJSONObject(i);
            int seq = answer.getInt(("seq"));
            if(isQuizProgressExists){
                checked = isAnswerExistsInProgressArr(seq);
                if(checked) {
                    mSelectedAnsSeqs.add(String.valueOf(seq));
                }
            }
            String title = answer.getString("title");
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
        if(isQuizProgressExists) {
            getSelectedAnswersScore();
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
            JSONArray existingAns = new JSONArray();
            for(int i = 0;i < mQuizProgress.length();i++) {
                JSONObject progress = mQuizProgress.getJSONObject(i);
                JSONObject ansJson = getAnswerBySeqFromArr(progress.getInt("answerSeq"));
                existingAns.put(ansJson);
            }
            if(ansArr.toString().equals(existingAns.toString())){
                feedbacks_success_list.add("Correct Sequence");
                ;
            }else{
                feedbacks_error_list.add("Incorrect Sequence");
            }
            ansArr = existingAns;
        }
        listFragment = ListFragment.newInstance(ansArr,!isQuizProgressExists);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.optionsLayout, listFragment, "fragment").commit();
    }

    private void addCustomSeekBar()throws Exception{
        ArrayMap<Integer,String> itemsMap = new ArrayMap<>();
        int selectedValue = 0;
        if(isQuizProgressExists){
            JSONObject progress = mQuizProgress.getJSONObject(0);
            selectedValue = progress.getInt("answerSeq");
            feedbacks_success_list.add("Submitted");
        }else {
            JSONObject defaultAns = mAnswers.getJSONObject(0);
            selectedValue = defaultAns.getInt("seq");
        }
        mSelectedAnsSeqs.add(selectedValue);
        for(int i =0;i<mAnswers.length();i++) {
            JSONObject ans = mAnswers.getJSONObject(i);
            itemsMap.put(ans.getInt("seq"), ans.getString("title"));
        }
        CustomSeekBar customSeekBar = new CustomSeekBar(getActivity().getApplicationContext(),itemsMap,Color.DKGRAY);
        customSeekBar.addSeekBar(mOptionsLayout,selectedValue);
        SeekBar seekBar = customSeekBar.getSeekBar();
        final LinearLayout seekBarLayoutBar = customSeekBar.getSeekBarLayout();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView textView = (TextView)seekBarLayoutBar.getChildAt(i);
                int selectedAnsSeq = textView.getId();
                mSelectedAnsSeqs.clear();
                mSelectedAnsSeqs.add(selectedAnsSeq);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        if(isQuizProgressExists){
            seekBar.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    private void addWebView()throws Exception{
        String detail = currentQuestion.getString("detail");
        webView = (WebView) mParentLayout.findViewById(R.id.webView);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        if(mQuestionType.equals(DOC)){
            webView.setWebViewClient(new WebViewClient() {
                //once the page is loaded get the html element by class or id and through javascript hide it.
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    webView.loadUrl("javascript:(function() { " +
                            "document.querySelector('[role=\"toolbar\"]').remove();})()");
                }
            });
            String url = "http://docs.google.com/gview?url="+StringConstants.DOC_URL+detail+"&embedded=true";
            webView.loadUrl(url);

        }else{
            if(mQuestionType.equals(MEDIA)){
                if(detail.contains("<iframe")){
                    String frameStr = "<iframe";
                    String str = detail.substring(0,frameStr.length());
                    String str1 = detail.substring(frameStr.length()+1);
                    str += " style=\"width:100%;\" ";
                    str += " " +str1;
                    detail = str;
                }
            }
            webView.setWebViewClient(new Browser());
            webView.setWebChromeClient(new MyWebClient());
            webView.loadData(detail,"text/html; charset=utf-8", "utf-8");

        }
        submitButton.setText("Mark as read");
    }

    private void addSeekBar()throws Exception {
        SeekBar seekBar = new SeekBar(getActivity());
        seekBar.setMax(100);
        final TextView textView = new TextView(getActivity());
        textView.setText("0");
        ViewGroup.LayoutParams params = textView.getLayoutParams();
        textView.setTextSize(15);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                       @Override
                       public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                           setSeekBarTextLocation(progress,seekBar,textView);
                           //textView.setY(100); just added a value set this properly using screen with height aspect ratio , if you do not set it by default it will be there below seek bar
                           mTextView_Long_question = textView;
                       }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
        mOptionsLayout.addView(seekBar);
        mOptionsLayout.addView(textView);
        if(isQuizProgressExists){
            JSONObject progress = mQuizProgress.getJSONObject(0);
            int progressValue = progress.getInt("answerText");
            seekBar.setProgress(progressValue);
            setSeekBarTextLocation(progressValue,seekBar,textView);
            enableDisableAllViews(false);
        }
    }

    private void setSeekBarTextLocation(int progress,SeekBar seekBar,TextView textView){
        int val = (progress * (seekBar.getWidth()-4 * seekBar.getThumbOffset())) / seekBar.getMax();
        textView.setText("" + progress);
        textView.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
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
                    saveProgress(currentQuestion,false);
                    enableDisableAllViews(false);
                }
            });
            submitButton.setBackgroundColor(getResources().getColor(R.color.button_yellow));
        }

    }

    public void saveProgress(JSONObject currentQuestion,boolean isTimeUp){
        try {
            Integer score = 0;
            HashMap<Integer,Integer> scores = new HashMap<>();
            if(isTimeUp || mQuestionType.equals(WEB_PAGE) || mQuestionType.equals(DOC) || mQuestionType.equals(MEDIA)) {
                mQuesProgressMgr.saveQuestionProgress(currentQuestion, null, score, mParentActivity.mStartDate,isTimeUp);
            }else{
                if (mQuestionType.equals(LONG_QUESTION) || mQuestionType.equals(ESTIMATE_PERCENTAGE)) {
                    String ansText = mTextView_Long_question.getText().toString();
                    score = currentQuestion.getInt("maxMarks");
                    mQuesProgressMgr.saveQuestionProgress(currentQuestion, ansText, score, mParentActivity.mStartDate,isTimeUp);
                    feedbacks_success_list.add(SUBMITTED_SUCCESSFULLY);
                } else {
                    if (mQuestionType.equals(YES_NO)) {
                        String ansTitle = "no";
                        if (switchYesNo.isChecked()) {
                            ansTitle = "yes";
                        }
                        JSONObject ans = getAnswerByTitleFromArr(ansTitle);
                        Object seq = ans.get("seq");
                        mSelectedAnsSeqs.add(0, seq);
                    }
                    if (mQuestionType.equals(SEQUENCING)) {
                        boolean isInRightOrder = addSortedItemSeq();
                        if (isInRightOrder) {
                            score = currentQuestion.getInt("maxMarks");
                            feedbacks_success_list.add(0,"Correct Sequence");
                        }else{
                            feedbacks_error_list.add("Incorrect Sequence");
                        }
                        Long ansSeq = (Long) mSelectedAnsSeqs.get(0);
                        scores.put(ansSeq.intValue(), score);
                    } else {
                        scores = getSelectedAnswersScore();
                    }
                    mQuesProgressMgr.saveQuestionProgress(currentQuestion, mSelectedAnsSeqs, scores, mParentActivity.mStartDate);
                }
            }
            handleSubmitButton(true);
            if((!isTimeUp && wizard_page_position == allQuestions.length()-1)
                    || mParentActivity.mSubmitQuestionCount == allQuestions.length()-1){
                executeTrainingSubmitCall();
            }
            showFeedback();
            mParentActivity.mStartDate = new Date();
            mParentActivity.mSubmitQuestionCount++;
        }catch (Exception e){
            LayoutHelper.showToast(getActivity(),e.getMessage());
        }
    }

    private void executeTrainingSubmitCall(){
        try {
            boolean isNetworkAvailable = AppStatus.getInstance(getContext()).isOnline();
            int moduleSeq = currentQuestion.getInt("moduleSeq");
            int learningPlanSeq = currentQuestion.getInt("learningPlanSeq");
            if(isNetworkAvailable){
                JSONArray progressArr = mQuesProgressMgr.getProgressListByModule(moduleSeq,learningPlanSeq);
                String jsonArrString = progressArr.toString();
                jsonArrString = URLEncoder.encode(jsonArrString, "UTF-8");
                Object[] args = {mUserSeq,mCompanySeq,jsonArrString};
                String notificationUrl = MessageFormat.format(StringConstants.SUBMIT_QUIZ_PROGRESS,args);
                mAuthTask = new ServiceHandler(notificationUrl, this, SAVE_QUIZ_PROGRESS, getActivity());
                mAuthTask.setShowProgress(false);
                mAuthTask.execute();
            }else{
                ModuleMgr moduleMgr = ModuleMgr.getInstance(getContext());
                moduleMgr.savePendingModules(moduleSeq,learningPlanSeq);
                Toast.makeText(getContext(),"Training compelted successfully",Toast.LENGTH_LONG).show();
                mParentActivity.goToTrainingActivity();
            }

        }catch (Exception e){
            LayoutHelper.showToast(getActivity(),e.getMessage());
        }
    }

    private void executeSaveActivityCall(){
        if(!isQuizProgressExists && !isSavedActivityData) {
            try {
                int moduleSeq = currentQuestion.getInt("moduleSeq");
                int learningPlanSeq = currentQuestion.getInt("learningPlanSeq");
                List<String> randomQuestionKeys = mParentActivity.randomQuestionKeys;
                JSONObject activityJson = mQuesProgressMgr.getActivityData(moduleSeq, learningPlanSeq,randomQuestionKeys);
                String jsonString = activityJson.toString();
                jsonString = URLEncoder.encode(jsonString, "UTF-8");
                Object[] args = {mUserSeq,mCompanySeq,jsonString};
                String notificationUrl = MessageFormat.format(StringConstants.SAVE_ACTIVITY, args);
                mAuthTask = new ServiceHandler(notificationUrl, this, SAVE_ACTIVITY, getActivity());
                mAuthTask.setShowProgress(false);
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
                    //LayoutHelper.showToast(getActivity(),message);
                    mParentActivity.goToTrainingActivity();
                }else{
                    isSavedActivityData = true;
                }
            }
        }catch (Exception e){
            message = e.getMessage();
            LayoutHelper.showToast(getActivity(),message);
        }

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
            int seq = Integer.parseInt(ansSeq.toString());
            JSONObject ans = getAnswerBySeqFromArr(seq);
            int score = ans.getInt("marks");
            scores.put(seq,score);
            String feedback = ans.getString("feedback");
            if(mModuleType.equals("quiz")) {
                if (score > 0) {
                    if (feedback == null || !feedback.equals("null")) {
                        feedback = "Correct";
                    }
                    feedbacks_success_list.add(feedback);
                } else {
                    if (feedback == null || !feedback.equals("null")) {
                        feedback = "Incorrect";
                    }
                    feedbacks_error_list.add(feedback);
                }
            }else{
                feedbacks_success_list.add("Submitted");
            }
        }
        return scores;
    }

    private void showFeedback()throws Exception{
        boolean isShowFeedback = mModuleJson.getInt("isshowfeedback") > 0;
        if(isShowFeedback) {
            String successText = "";
            for (String feedback : feedbacks_success_list) {
                successText += feedback + System.lineSeparator();
            }

            String errorText = "";
            for (String feedback : feedbacks_error_list) {
                errorText += feedback + System.lineSeparator();
            }
            if (successText != null && !successText.equals("")) {
                successText = successText.substring(0, successText.length() - 1);
                textVew_feedback_success.setText(successText);
                textVew_feedback_success.setVisibility(View.VISIBLE);
                okButton.setVisibility(View.VISIBLE);
                okButton.setOnClickListener(new okClick());
            }
            if (errorText != null && !errorText.equals("")) {
                errorText = errorText.substring(0, errorText.length() - 1);
                textVew_feedback_error.setText(errorText);
                textVew_feedback_error.setVisibility(View.VISIBLE);
                okButton.setVisibility(View.VISIBLE);
                okButton.setOnClickListener(new okClick());
            }

        }
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

    private void enableDisableAllViews(boolean isEnable){
        for ( int i = 0; i < mOptionsLayout.getChildCount();  i++ ){
            View view = mOptionsLayout.getChildAt(i);
            view.setEnabled(isEnable);
        }
        if(radioGroup != null) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View view = radioGroup.getChildAt(i);
               view.setEnabled(isEnable);
            }
        }
    }

    private class okClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(wizard_page_position == allQuestions.length()-1) {
                mParentActivity.goToTrainingActivity();
            }else{
                wizard_page_position++;
                mParentActivity.viewPager.setCurrentItem(wizard_page_position);
            }
        }
    }


    class Browser
            extends WebViewClient
    {
        Browser() {}

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
        {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }

    public class MyWebClient
            extends WebChromeClient
    {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyWebClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (getActivity() == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getActivity().getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            getActivity().setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getActivity().getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getActivity().getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(webView != null) {
            webView.onResume();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(webView != null) {
            webView.onPause();
        }
    }


}
