package in.learntech.rights;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import in.learntech.rights.Managers.UserMgr;

public class NotificationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private UserMgr mUserMgr;
    Toolbar mToolbar;
    private int userSeq;
    private int companySeq;
    public SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }
        mUserMgr = UserMgr.getInstance(this);
        userSeq = mUserMgr.getLoggedInUserSeq();
        companySeq = mUserMgr.getLoggedInUserCompanySeq();
        NotificationsFragment mFragment = NotificationsFragment.newInstance(userSeq,companySeq,this);
        getFragmentManager().beginTransaction().replace(R.id.notificationLayout,mFragment).commit();
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        NotificationsFragment mFragment = NotificationsFragment.newInstance(userSeq,companySeq,this);
        getFragmentManager().beginTransaction().replace(R.id.notificationLayout,mFragment).commit();
    }
}
