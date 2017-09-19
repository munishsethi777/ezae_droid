package in.learntech.rights;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import in.learntech.rights.Controls.CustomViewPager;
import in.learntech.rights.Controls.SwipeDirection;
import in.learntech.rights.Managers.QuestionProgressMgr;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;

public class UserTrainingActivity extends AppCompatActivity implements IServiceHandler ,View.OnClickListener {


    public CustomViewPager viewPager;
    private Intent mIntent;
    private int mLpSeq;
    private int mModuleSeq;
    private int mUserSeq;
    private UserMgr mUserMgr;
    private ServiceHandler mAuthTask;
    private int WIZARD_PAGES_COUNT;
    private JSONArray mModuleQuestionsJson;
    private TextView mModuleTitleTextview;
    private TextView mQuestionNoTextView;
    private TextView mQuestionMarksTextView;
    private QuestionProgressMgr mQuesMgr;
    private JSONObject mModuleJson;
    private LinearLayout linearLayoutIndicator;
    private TextView textView_timer;
    public int mSubmitQuestionCount;
    private Fragment mChildFragment;
    public Date mStartDate;
    private CountDownTimer countTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_training_layout);
        mIntent = getIntent();
        mLpSeq = mIntent.getIntExtra(StringConstants.LP_SEQ,0);
        mModuleSeq = mIntent.getIntExtra(StringConstants.MODULE_SEQ,0);
        mUserMgr = UserMgr.getInstance(this);
        mUserSeq = mUserMgr.getLoggedInUserSeq();
        mModuleTitleTextview = (TextView)findViewById(R.id.textView_module_title);
        mQuestionNoTextView = (TextView)findViewById(R.id.textView_question_no);
        mQuestionMarksTextView = (TextView)findViewById(R.id.textView_marks);
        executeGetModuleDetailsCall();
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        mQuesMgr = QuestionProgressMgr.getInstance(this);
        linearLayoutIndicator = (LinearLayout)findViewById(R.id.layout_indicator);
        textView_timer = (TextView)findViewById(R.id.textView_timer);
    }

    private void executeGetModuleDetailsCall(){
        Object[] args = {mUserSeq,mModuleSeq,mLpSeq};
        String notificationUrl = MessageFormat.format(StringConstants.GET_MODULE_DETAILS,args);
        mAuthTask = new ServiceHandler(notificationUrl,this,this);
        mAuthTask.execute();
    }

    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(StringConstants.SUCCESS) == 1 ? true : false;
            message = response.getString(StringConstants.MESSAGE);
            mModuleJson = response.getJSONObject("module");
            if(success){
                mSubmitQuestionCount = 0;
                mModuleTitleTextview.setText(mModuleJson.getString("title"));
                mModuleQuestionsJson = mModuleJson.getJSONArray("questions");

                WIZARD_PAGES_COUNT = mModuleQuestionsJson.length();
                viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
                viewPager.addOnPageChangeListener(new WizardPageChangeListener());
                addIndicators();
                updateIndicators(0);
                mStartDate = new Date();
                int progress = 0;
                long timeAllowed = mModuleJson.getLong("timeallowed");
                if(timeAllowed > 0 ){
                    timeAllowed = TimeUnit.MINUTES.toMillis(timeAllowed);
                }
                if(mModuleJson.has("activityData")) {
                    JSONObject activity = mModuleJson.getJSONObject("activityData");
                    progress = activity.getInt("progress");
                }
                if(timeAllowed > 0 && progress < 100){
                    long timeConsumed = mQuesMgr.getTimeConsumed(mModuleQuestionsJson);
                    timeAllowed = timeAllowed - timeConsumed;
                    startTimer(timeAllowed);
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setCallName(String call) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mChildFragment = new UserTrainingFragment(position,mModuleJson);
            return mChildFragment;
        }

        @Override
        public int getCount() {
            return WIZARD_PAGES_COUNT;
        }

    }

    private class WizardPageChangeListener implements ViewPager.OnPageChangeListener {
        private boolean scrollStarted, checkDirection;

        @Override
        public void onPageScrollStateChanged(int position) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            updateIndicators(position);
        }
    }

    public void updateIndicators(int position) {
        for(int i = 0 ; i < WIZARD_PAGES_COUNT;i++){
                LinearLayout layout = (LinearLayout)linearLayoutIndicator.getChildAt(i);
                View view  = layout.getChildAt(0);
                if(i == position){
                    view.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                }else{
                    view.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                }
        }
        String questionNoStr = position + 1 + " of " + mModuleQuestionsJson.length() + " Questions";
        mQuestionNoTextView.setText(questionNoStr);
        try{
            JSONObject ques = mModuleQuestionsJson.getJSONObject(position);
            JSONArray progressArray = ques.getJSONArray("progress");
            JSONArray localProgress = mQuesMgr.getProgressJsonArr(ques.getInt("seq"),mModuleSeq,mLpSeq);
            progressArray = LayoutHelper.mergeTwoJsonArray(progressArray,localProgress);
            mQuestionMarksTextView.setText("Marks: " + ques.getInt("maxMarks"));


            if(progressArray.length() > 0){
                viewPager.setAllowedSwipeDirection(SwipeDirection.all);
            }else{
                viewPager.setAllowedSwipeDirection(SwipeDirection.left);
            }

        }catch (Exception e){
            LayoutHelper.showToast(getApplicationContext(),e.getMessage());
        }

    }

    private void addIndicators(){
        for(int i = 0 ; i< WIZARD_PAGES_COUNT;i++){
            LinearLayout indicator = (LinearLayout) getLayoutInflater().inflate(R.layout.indicator,null);
            linearLayoutIndicator.addView(indicator);
        }
    }
    @Override
    public void onClick(View view) {

    }

    private void startTimer(long timeInMill){
        textView_timer.setVisibility(View.VISIBLE);
        //long timeInMill = TimeUnit.MINUTES.toMillis(time);
        countTimer = new CountDownTimer(timeInMill, 1000) {
            public void onTick(long millisUntilFinished) {
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
                textView_timer.setText("Time left: " + hms);
            }
            public void onFinish() {
                LayoutHelper.showToast(getApplicationContext(),"Time Over!");
                savePendingQuesProgress();
                goToTrainingActivity();
                textView_timer.setText("done!");
            }
        }.start();
    }

    public void  goToTrainingActivity(){
        Intent intent = new Intent(this,MyTrainings.class);
        startActivity(intent);
    }

    public void savePendingQuesProgress(){
        try {
            mSubmitQuestionCount = mQuesMgr.getTotalSubmittedQuesCount(mModuleQuestionsJson);
            int count = mSubmitQuestionCount;
            for(int i = count;i<mModuleQuestionsJson.length();i++){
                JSONObject jsonObject = mModuleQuestionsJson.getJSONObject(i);
                UserTrainingFragment fragment = (UserTrainingFragment) mChildFragment;
                fragment.saveProgress(jsonObject,true);
            }
        }catch (Exception e){
            LayoutHelper.showToast(getApplicationContext(),e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(countTimer != null)
            countTimer.cancel();
    }
}
