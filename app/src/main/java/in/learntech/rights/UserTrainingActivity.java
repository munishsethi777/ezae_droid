package in.learntech.rights;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.StringConstants;

public class UserTrainingActivity extends AppCompatActivity implements IServiceHandler ,View.OnClickListener {


    private ViewPager viewPager;
    private View indicator1;
    private View indicator2;
    private View indicator3;
    private View indicator4;
    private Intent mIntent;
    private int mLpSeq;
    private int mModuleSeq;
    private int mUserSeq;
    private UserMgr mUserMgr;
    private ServiceHandler mAuthTask;
    private int WIZARD_PAGES_COUNT = 4;
    private JSONArray mModuleQuestionsJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_training_layout);
        mIntent = getIntent();
        mLpSeq = mIntent.getIntExtra(StringConstants.LP_SEQ,0);
        mModuleSeq = mIntent.getIntExtra(StringConstants.MODULE_SEQ,0);
        mUserMgr = UserMgr.getInstance(this);
        mUserSeq = mUserMgr.getLoggedInUserSeq();
        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        indicator3 = findViewById(R.id.indicator3);
        indicator4 = findViewById(R.id.indicator4);
        executeGetModuleDetailsCall();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        updateIndicators(0);
    }

    private void executeGetModuleDetailsCall(){
        Object[] args = {mUserSeq,mModuleSeq,mLpSeq};
        String notificationUrl = MessageFormat.format(StringConstants.GET_MODULE_DETAILS,args);
        mAuthTask = new ServiceHandler(notificationUrl,this);
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
            JSONObject jsonObject = response.getJSONObject("module");
            if(success){
                mModuleQuestionsJson = jsonObject.getJSONArray("questions");
                WIZARD_PAGES_COUNT = mModuleQuestionsJson.length();
                viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
                viewPager.addOnPageChangeListener(new WizardPageChangeListener());
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
            return new UserTrainingFragment(position,mModuleQuestionsJson);
        }

        @Override
        public int getCount() {
            return WIZARD_PAGES_COUNT;
        }

    }

    private class WizardPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            updateIndicators(position);
        }
    }

    public void updateIndicators(int position) {
        switch (position) {
            case 0:
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                break;
            case 1:
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                break;
            case 2:
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                break;
            case 3:
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_dot));
                break;
        }
    }

    @Override
    public void onClick(View view) {

    }
}
