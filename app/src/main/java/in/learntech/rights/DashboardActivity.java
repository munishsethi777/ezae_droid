package in.learntech.rights;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.app.progresviews.ProgressWheel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.MessageFormat;
import java.util.HashMap;

import in.learntech.rights.BusinessObjects.CompanyUser;
import in.learntech.rights.BusinessObjects.User;
import in.learntech.rights.Chatroom.ChatRoomActivity;
import in.learntech.rights.Events.*;
import in.learntech.rights.Leaderboard.LeaderBoardFragment;
import in.learntech.rights.Leaderboard.LeaderboardModel;
import in.learntech.rights.Managers.CompanyUserManager;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.messages.MessageActivity;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;
import in.learntech.rights.utils.sorting.MySwipeRefreshLayout;

public class DashboardActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener,NavigationView.OnNavigationItemSelectedListener,IServiceHandler,View.OnClickListener , LeaderBoardFragment.OnListFragmentInteractionListener {
    private static final String SUCCESS = "success";
    private static final String DASHBOARD_DATA = "dashboardData";
    private static final String DASHBOARD_COUNTS = "dashboardCounts";
    private static final String MESSAGE = "message";
    public static final String GET_DASHBOARD_COUNT = "getDashboardCount";
    public static final String GET_COUNTS = "getCounts";
    public static final String GET_LEARNING_PLANS = "getLearningPlans";
    public static final String SYNC_USERS = "syncUsers";
    public static final String GET_PROFILES_AND_MODULES = "getProfilesAndModules";
    private ServiceHandler mAuthTask = null;
    private ServiceHandler mAuthGetCountTask = null;
    private ServiceHandler mAuthSynchUserTask = null;
    private UserMgr mUserMgr ;
    private TextView mScores;
    private TextView mProfileRank;
    private TextView mPendingTrainings;
    private TextView mCompletedTrainings;
    private String mCallName;
    private static final String[] pageTitle = {"Notifications"};
    private int mLoggedInUserSeq;
    private int mLoggedInCompanySeq;
    private ImageView mUserImageView;
    private ImageView userImageView;
    private TextView mUserNameView;
    private TextView mUserEmailView;
    private TextView mUserProfilesView;
    private TextView mUserName;
    private TextView mProfiles;
    private TextView notificationCountTextView;
    private TextView learningPlanCountTextView;
    private TextView messagesCountTextView;

    private LinearLayout mMenuHeaderLayout;
    private LayoutHelper mLayoutHelper;
    private CompanyUserManager mCompanyUserMgr;
    private SwipeRefreshLayout swipeLayout;
    private Spinner mSpinner;
    private ImageView mCompanyImage;
    private Boolean isEnabledCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mUserMgr = UserMgr.getInstance(this);
        mCompanyUserMgr = CompanyUserManager.getInstance(this);
        mLoggedInUserSeq = mUserMgr.getLoggedInUserSeq();
        mLoggedInCompanySeq = mUserMgr.getLoggedInUserCompanySeq();
        //mSpinner = (Spinner)findViewById(R.id.spinner_profilesAndModules);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mLayoutHelper = new LayoutHelper(this,null,null);
        initViews();
        isEnabledCompany = true;
        //executeCalls(true);
        //android.app.Fragment fragment = NotificationsFragment.newInstance(mLoggedInUserSeq,mLoggedInCompanySeq);
        //getFragmentManager().beginTransaction().replace(R.id.layout_notifications,fragment).commit();
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }

    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        executeCalls(true);
        populateUserProfile();
    }

    private void initViews(){
        mMenuHeaderLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.nav_header_dashboard,null);
        mScores = (TextView) findViewById(R.id.textView_score);
        mProfileRank = (TextView) findViewById(R.id.textView_profile_rank);
        mPendingTrainings = (TextView) findViewById(R.id.textView_pending_trainings);
        //mCompletedTrainings = (TextView) findViewById(R.id.textView_completed_trainings);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        notificationCountTextView = (TextView)findViewById(R.id.notificationCount);
        learningPlanCountTextView = (TextView)findViewById(R.id.learningPlanCount);
        messagesCountTextView = (TextView)findViewById(R.id.messagesCount);
        mUserImageView = (ImageView)hView.findViewById(R.id.imageView_user);
        mUserName = (TextView)findViewById(R.id.textView_username_dash);
        mProfiles = (TextView)findViewById(R.id.textView_profiles_dash);
        userImageView = (ImageView)findViewById(R.id.imageView_userImage);
        mUserNameView = (TextView)hView.findViewById(R.id.textView_userName);
        mUserEmailView = (TextView)hView.findViewById(R.id.textView_userEmail);
        mUserProfilesView = (TextView)hView.findViewById(R.id.textView_profiles);
        mCompanyImage = (ImageView) findViewById(R.id.companyImageView);
        User user = mUserMgr.getLoggedInUser();
        mUserNameView.setText(user.getFullName());
        mUserName.setText(user.getFullName());
        mProfiles.setText(user.getProfiles());
        mUserEmailView.setText(user.getEmail());
        if(user.getProfiles() != null && user.getProfiles() != "") {
            mUserProfilesView.setText(user.getProfiles());
        }
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        executeCalls(false);
    }

    private void populateUserProfile(){
        String userImageUrl = mUserMgr.getLoggedInUserImageUrl();
        mLayoutHelper.loadImageRequest(mUserImageView,userImageUrl,true);
        mLayoutHelper.loadImageRequest(userImageView,userImageUrl,true);
        String companyImageUrl = mUserMgr.getLoggedInUserCompanyImageUrl();
        mCompanyImage.setVisibility(View.INVISIBLE);
        if(companyImageUrl != null) {
            mCompanyImage.setVisibility(View.VISIBLE);
            mLayoutHelper.loadImageRequest(mCompanyImage, companyImageUrl, false);
        }

    }
    private void executeCalls(boolean isShowProgress){
        int loggedInUserSeq = mUserMgr.getLoggedInUserSeq();
        int loggedInUserCompanySeq = mUserMgr.getLoggedInUserCompanySeq();
        Object[] args = {loggedInUserSeq,loggedInUserCompanySeq};
        String dashboardCountUrl = MessageFormat.format(StringConstants.GET_DASHBOARD_COUNTS,args);
        String getCountsUrl = MessageFormat.format(StringConstants.GET_COUNTS,args);
        String syncUsersUrl = MessageFormat.format(StringConstants.SYNCH_USERS,args);

        mAuthTask = new ServiceHandler(dashboardCountUrl,this, GET_DASHBOARD_COUNT,this);
        mAuthTask.setShowProgress(isShowProgress);
        mAuthTask.execute();

        mAuthGetCountTask = new ServiceHandler(getCountsUrl,this, GET_COUNTS,this);
        mAuthGetCountTask.setShowProgress(isShowProgress);
        //mAuthGetCountTask.execute();

        //SYNC USERS
        mAuthSynchUserTask = new ServiceHandler(syncUsersUrl,this, SYNC_USERS,this);
        mAuthSynchUserTask.setShowProgress(false);
        //mAuthSynchUserTask.execute();

    }

//    private void populateProfileAndModules(JSONObject response)throws Exception{
//        JSONArray profileAndModuleArr = response.getJSONArray("profilesAndModules");
//        String[] spinnerArray = new String[profileAndModuleArr.length()];
//        final HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();
//        for (int i = 0; i < profileAndModuleArr.length(); i++)
//        {
//            JSONObject json = profileAndModuleArr.getJSONObject(i);
//            spinnerMap.put(i,json.getString("id"));
//            spinnerArray[i] = json.getString("name");
//        }
//        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item, spinnerArray);
//        adapter.setDropDownViewResource(R.layout.spinner_item);
//        mSpinner.setAdapter(adapter);
//        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
//            public void onItemSelected(AdapterView<?> parent, View view, int pos,
//                                       long id) {
//                ((TextView) view).setTextColor(Color.BLACK);
//                String selectedId = spinnerMap.get(pos);
//                LeaderBoardFragment itemFragment = LeaderBoardFragment.newInstance(selectedId);
//                setFragment(itemFragment);
//            }
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }

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
        //getMenuInflater().inflate(R.menu.dashboard, menu);

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
            Intent intent = new Intent(this,MessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notes) {
            Intent notesIntent = new Intent(this,NotesActivity.class);
            startActivity(notesIntent);
        } else if (id == R.id.nav_update_profile) {
            Intent notesIntent = new Intent(this,UpdateProfileActivity.class);
            startActivity(notesIntent);
        } else if (id == R.id.nav_logout) {
            logoutConfirm();
        } else if(id == R.id.nav_change_password){
            Intent intent = new Intent(this,ChangePasswordActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_chatroom){
            Intent intent = new Intent(this,ChatRoomActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_Events){
            Intent intent = new Intent(this, in.learntech.rights.Events.MainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){
        mUserMgr.resentPreferences();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void logoutConfirm() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Do you really want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
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
                    populateDashboardStates(response);
                }if(mCallName.equals(GET_COUNTS)){
                    populateDashboardCounts(response);
                }else if(mCallName.equals(GET_LEARNING_PLANS)){
                    //populateLearningPlans(response);
                }else if(mCallName.equals(SYNC_USERS)){
                    mCompanyUserMgr.saveUsersFromResponse(response);
                    message = null;
                }else if(mCallName.equals(GET_PROFILES_AND_MODULES)){
                    //populateProfileAndModules(response);
                    message = null;
                }
            }else{
                if(response.has("isenabled")){
                    boolean isEnabled =  response.getBoolean("isenabled");
                    if(!isEnabled){
                        logout();
                        return;
                    }
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }



    private void populateDashboardStates(JSONObject response)throws  Exception{
        JSONObject dashboardData = response.getJSONObject(DASHBOARD_DATA);
        String totalScores = dashboardData.getString("totalScores");
        String completedTrainings = dashboardData.getString("completedTrainings");
        JSONObject pendingTraingsJSON = dashboardData.getJSONObject("pendingTrainings");

        String maxScore = pendingTraingsJSON.getString("maxScore");
        String profileRank = dashboardData.getString("userRank");
        String pendingCount = pendingTraingsJSON.getString("pendingCount");
        String points = dashboardData.getString("points");
        String totalScoreStr = totalScores+"/" + maxScore;
        mScores.setText(totalScoreStr);
        if(profileRank == "null"){
            profileRank = "0";
        }
        String profileRankStr = profileRank;
        mProfileRank.setText(profileRankStr);

        String pendingTrainingsStr = pendingCount;
        mPendingTrainings.setText(points);

        String completedTrainingsStr = completedTrainings;
        //mCompletedTrainings.setText(completedTrainingsStr);
        mAuthGetCountTask.execute();
        mAuthSynchUserTask.execute();
    }

    private void populateDashboardCounts(JSONObject response)throws  Exception{
        JSONObject dashboardData = response.getJSONObject(DASHBOARD_COUNTS);
        Integer pendingLpCount = dashboardData.getInt("pendingLpCount");
        Integer notificationCount = dashboardData.getInt("notificationCount");
        Integer messagesCount = dashboardData.getInt("messages");
        if(notificationCount > 0){
            notificationCountTextView.setText("+"+notificationCount.toString());
        }else{
            notificationCountTextView.setText("");
        }
        if(pendingLpCount > 0) {
            learningPlanCountTextView.setText("+"+pendingLpCount.toString());
        }else{
            learningPlanCountTextView.setText("");
        }
        if(messagesCount > 0) {
            messagesCountTextView.setText("+"+messagesCount.toString());
        }else{
            messagesCountTextView.setText("");
        }
        if(swipeLayout != null){
            swipeLayout.setRefreshing(false);
        }
    }

//    private void populateLearningPlans(JSONObject response)throws Exception{
//        JSONArray learningPlansData = response.getJSONArray("learningPlans");
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
//        ConstraintLayout mainLayout = (ConstraintLayout) this.findViewById(R.id.dashboard_layout);
//        //LinearLayout linerLayout = (LinearLayout) mainLayout.findViewById(R.id.activeLearningPlansContainer);
//        LinearLayout fragmentLayout = null;
//        int count = 0;
//        for (int i=0; i < learningPlansData.length(); i++) {
//            JSONObject jsonObject = learningPlansData.getJSONObject(i);
//            String learningPlanName = jsonObject.getString("learningPlanName");
//            int progress = jsonObject.getInt("percentCompleted");
//            int view_progress_id = R.id.progressBarALP_1_2;
//            int text_view_id = R.id.textViewALP_1_2;
//            //int percent_view_id = R.id.textView_percent2;
//            if(count == 0){
//                fragmentLayout = (LinearLayout)inflater.inflate(R.layout.dashboard_activeplan_fragment, null);
//                view_progress_id = R.id.progressBarALP_1_1;
//                text_view_id = R.id.textViewALP_1_1;
//                //percent_view_id = R.id.textView_percent1;
//            }
//            ProgressWheel progressWheel = (ProgressWheel)fragmentLayout.findViewById(view_progress_id);
//            //ProgressBar view_progress = (ProgressBar) fragmentLayout.findViewById(view_progress_id);
//            //view_progress.setProgress(progress);
//            progressWheel.setPercentage(progress*4);
//            progressWheel.setStepCountText(progress + "%");
//
//            TextView lpName = (TextView) fragmentLayout.findViewById(text_view_id);
//            lpName.setText(learningPlanName);
//            //TextView percent_text = (TextView) fragmentLayout.findViewById(percent_view_id);
//            //percent_text.setText(progress + "%");
//            count++;
//            boolean isLast = i == learningPlansData.length()-1;
//            if(count == 2 || isLast) {
//                if(count != 2 && isLast){
//                    View layout = fragmentLayout.findViewById(R.id.activePlansLayoutGroup2);
//                    layout.setVisibility(View.GONE);
//                }
//                //linerLayout.addView(fragmentLayout);
//                count = 0;
//            }
//        }
//    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imageView_trainings) {
            Intent intent = new Intent(this,MyTrainings.class);
            startActivity(intent);
        } else if (id == R.id.imageView_achievements) {
            Intent intent = new Intent(this,MyAchievements.class);
            startActivity(intent);
        } else if (id == R.id.imageView_messages) {
            Intent intent = new Intent(this,MessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.imageView_notes) {
            Intent notesIntent = new Intent(this,NotesActivity.class);
            startActivity(notesIntent);
        } else if (id == R.id.imageView_updateProfile) {
            Intent notesIntent = new Intent(this,UpdateProfileActivity.class);
            startActivity(notesIntent);
        }else if (id == R.id.imageView_notifications) {
            Intent intent = new Intent(this,NotificationActivity.class);
            startActivity(intent);
        }else if (id == R.id.imageView_chat) {
            Intent intent = new Intent(this,ChatRoomActivity.class);
            startActivity(intent);
        }else if (id == R.id.imageView_password) {
            Intent intent = new Intent(this,ChangePasswordActivity.class);
            startActivity(intent);
        }else if (id == R.id.imageView_calendar) {
            Intent intent = new Intent(this, in.learntech.rights.Events.MainActivity.class);
            startActivity(intent);
        }else if (id == R.id.imageView_logout) {
            logout();
        }else if(id == R.id.imageView_userImage){
            Intent intent = new Intent(this, UpdateProfileActivity.class);
            startActivity(intent);
        }

    }

    //Leader board container
    @Override
    public void onListFragmentInteraction(LeaderboardModel item) {

    }



}
