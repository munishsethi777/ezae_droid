package in.learntech.rights;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.utils.StringConstants;

public class MyTrainings extends AppCompatActivity implements View.OnClickListener{
    private static final String[] pageTitle = {"LEARNING PLANS","MY MODULES"};
    private int mUserSeq;
    private int mCompanySeq;
    private UserMgr mUserMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trainings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("My Trainings");
        }
        mUserMgr = UserMgr.getInstance(this);
        mUserSeq = mUserMgr.getLoggedInUserSeq();
        mCompanySeq = mUserMgr.getLoggedInUserCompanySeq();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_loginsignup4);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_loginsignup4);
        MyTrainingsAdapter adapter = new MyTrainingsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_moduleLaunch:
                goUserTrainingActivity(view);
                break;
            default:
                break;
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    public class MyTrainingsAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;

        public MyTrainingsAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            Fragment moduleFragment = MyTrainings_MyModulesFragment.newInstance(mUserSeq,mCompanySeq);
            Fragment lpFragment = MyTrainings_LearningPlansFragment.newInstance(mUserSeq,mCompanySeq);
            fragments.add(lpFragment);
            fragments.add(moduleFragment);
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int arrayPos) {
            return pageTitle[arrayPos];
        }
    }

    private void goUserTrainingActivity(View view){
        int lpSeq = (int)view.getTag(R.string.lp_seq);
        int moduleSeq = (int)view.getTag(R.string.module_seq);
        Intent intent = new Intent(this,UserTrainingActivity.class);
        intent.putExtra(StringConstants.LP_SEQ,lpSeq);
        intent.putExtra(StringConstants.MODULE_SEQ,moduleSeq);
        startActivity(intent);
    }

}
