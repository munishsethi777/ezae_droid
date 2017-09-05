package in.learntech.rights;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.StringConstants;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,IServiceHandler {
    private static final String SUCCESS = "success";
    private static final String DASHBOARD_DATA = "dashboardData";
    private static final String MESSAGE = "message";
    private ServiceHandler mAuthTask = null;
    private UserMgr mUserMgr ;
    private TextView mScores;
    private TextView mProfileRank;
    private TextView mPendingTrainings;
    private TextView mCompletedTrainings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mUserMgr = UserMgr.getInstance(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initViews();
        populateDashboardCounts();
    }

    private void initViews(){
        mScores = (TextView) findViewById(R.id.textView_score);
        mProfileRank = (TextView) findViewById(R.id.textView_profile_rank);
        mPendingTrainings = (TextView) findViewById(R.id.textView_pending_trainings);
        mCompletedTrainings = (TextView) findViewById(R.id.textView_completed_trainings);

    }
    private void populateDashboardCounts(){
        int loggedInUserSeq = mUserMgr.getLoggedInUserSeq();
        int loggedInUserCompanySeq = mUserMgr.getLoggedInUserCompanySeq();
        Object[] args = {loggedInUserSeq,46};
        String loginUrl = MessageFormat.format(StringConstants.GET_DASHBOARD_COUNTS,args);
        mAuthTask = new ServiceHandler(loginUrl,this);
        mAuthTask.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trainings) {
            // Handle the camera action
        } else if (id == R.id.nav_achivements) {

        } else if (id == R.id.nav_messages) {

        } else if (id == R.id.nav_notes) {

        } else if (id == R.id.nav_update_profile) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        //showProgress(false);
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(SUCCESS) == 1 ? true : false;
            message = response.getString(MESSAGE);
            if(success){
                JSONObject dashboardData = response.getJSONObject(DASHBOARD_DATA);
                String totalScores = dashboardData.getString("totalScores");
                String completedTrainings = dashboardData.getString("completedTrainings");
                JSONObject pendingTraingsJSON = dashboardData.getJSONObject("pendingTrainings");

                String maxScore = pendingTraingsJSON.getString("maxScore");
                String profileRank = dashboardData.getString("userRank");
                String pendingCount = pendingTraingsJSON.getString("pendingCount");

                String totalScoreStr = totalScores+"/" + maxScore + System.lineSeparator() + "Score Earned";
                mScores.setText(totalScoreStr);
                if(profileRank == "null"){
                    profileRank = "0";
                }
                String profileRankStr = profileRank + System.lineSeparator() + "Profile Rank";
                mProfileRank.setText(profileRankStr);

                String pendingTrainingsStr = pendingCount + System.lineSeparator() + "Pending Trainings";
                mPendingTrainings.setText(pendingTrainingsStr);

                String completedTrainingsStr = completedTrainings + System.lineSeparator() + "Compltd Trainings";
                mCompletedTrainings.setText(completedTrainingsStr);
            }
        }catch (Exception e){

            message = "Error :- " + e.getMessage();
        }
        if(message != null){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
