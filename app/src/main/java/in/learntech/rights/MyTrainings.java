package in.learntech.rights;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import in.learntech.rights.Managers.UserMgr;

public class MyTrainings extends AppCompatActivity {
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
            actionBar.setTitle("Welcome");
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




}
