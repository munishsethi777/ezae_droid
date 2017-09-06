package in.learntech.rights;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Objects;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.StringConstants;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,IServiceHandler {
    private static final String SUCCESS = "success";
    private static final String DASHBOARD_DATA = "dashboardData";
    private static final String MESSAGE = "message";
    public static final String GET_DASHBOARD_COUNT = "getDashboardCount";
    public static final String GET_LEARNING_PLANS = "getLearningPlans";
    private ServiceHandler mAuthTask = null;
    private UserMgr mUserMgr ;
    private TextView mScores;
    private TextView mProfileRank;
    private TextView mPendingTrainings;
    private TextView mCompletedTrainings;
    private String mCallName;
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
        String dashboardCountUrl = MessageFormat.format(StringConstants.GET_DASHBOARD_COUNTS,args);
        String learningPlanUrl = MessageFormat.format(StringConstants.GET_LEARNING_PLANS,args);
        mAuthTask = new ServiceHandler(dashboardCountUrl,this, GET_DASHBOARD_COUNT);
        mAuthTask.execute();
        mAuthTask = new ServiceHandler(learningPlanUrl,this, GET_LEARNING_PLANS);
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
            Intent intent = new Intent(this,MyTrainings.class);
            startActivity(intent);
        } else if (id == R.id.nav_achivements) {
            Intent intent = new Intent(this,MyAchievements.class);
            startActivity(intent);
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
                if(mCallName.equals(GET_DASHBOARD_COUNT)){
                    populateDashboardCounts(response);
                }else if(mCallName.equals(GET_LEARNING_PLANS)){
                    populateLearningPlans(response);
                }
            }
        }catch (Exception e){

            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void populateDashboardCounts(JSONObject response)throws  Exception{
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

    private void populateLearningPlans(JSONObject response)throws Exception{
        JSONArray learningPlansData = response.getJSONArray("learningPlans");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout mainLayout = (ConstraintLayout) this.findViewById(R.id.dashboard_layout);
        LinearLayout linerLayout = (LinearLayout) mainLayout.findViewById(R.id.activeLearningPlansContainer);
        LinearLayout fragmentLayout = null;
        int count = 0;
        for (int i=0; i < learningPlansData.length(); i++) {
            JSONObject jsonObject = learningPlansData.getJSONObject(i);
            String learningPlanName = jsonObject.getString("learningPlanName");
            int progress = jsonObject.getInt("percentCompleted");
            int view_progress_id = R.id.progressBarALP_1_2;
            int text_view_id = R.id.textViewALP_1_2;
            int percent_view_id = R.id.textView_percent2;
            if(count == 0){
                fragmentLayout = (LinearLayout)inflater.inflate(R.layout.dashboard_activeplan_fragment, null);
                view_progress_id = R.id.progressBarALP_1_1;
                text_view_id = R.id.textViewALP_1_1;
                percent_view_id = R.id.textView_percent1;
            }
            ProgressBar view_progress = (ProgressBar) fragmentLayout.findViewById(view_progress_id);
            view_progress.setProgress(progress);
            TextView lpName = (TextView) fragmentLayout.findViewById(text_view_id);
            lpName.setText(learningPlanName);
            TextView percent_text = (TextView) fragmentLayout.findViewById(percent_view_id);
            percent_text.setText(progress + "%");
            count++;
            boolean isLast = i == learningPlansData.length()-1;
            if(count == 2 || isLast) {
                if(count != 2 && isLast){
                    View layout = fragmentLayout.findViewById(R.id.activePlansLayout1_2);
                    layout.setVisibility(View.GONE);
                }
                linerLayout.addView(fragmentLayout);
                count = 0;
            }

        }

    }
//
//    private LinearLayout getLinearLayout(int i){
//        LinearLayout parent = new LinearLayout(this);
//        parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        parent.setOrientation(LinearLayout.VERTICAL);
//        parent.setId(i);
//        return parent;
//    }
//
//    private LinearLayout getVerticalLinearLayout(){
//        LinearLayout verticalParent = new LinearLayout(this);
//        verticalParent.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
//        verticalParent.setOrientation(LinearLayout.VERTICAL);
//        return verticalParent;
//    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }
}
