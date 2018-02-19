package in.learntech.rights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import in.learntech.rights.Managers.UserMgr;

public class NotificationActivity extends AppCompatActivity {
    private UserMgr mUserMgr;
    Toolbar mToolbar;
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
        int userSeq = mUserMgr.getLoggedInUserSeq();
        int companySeq = mUserMgr.getLoggedInUserCompanySeq();
        NotificationsFragment mFragment = NotificationsFragment.newInstance(userSeq,companySeq);
        getFragmentManager().beginTransaction().replace(R.id.notificationLayout,mFragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
