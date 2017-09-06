package in.learntech.rights;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.MessageFormat;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.ImageViewCircleTransform;
import in.learntech.rights.utils.StringConstants;

public class MyAchievements extends AppCompatActivity implements IServiceHandler {

    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";

    private static final String DASHBOARD_DATA = "dashboardData";
    private static final String BADGES = "badges";

    public static final String GET_MY_ACHIEVEMENTS = "myAchievements";
    public static final String GET_MY_ACHIEVEMENT_BADGES = "myAchievementMyBadges";

    private UserMgr mUserMgr ;
    private ServiceHandler mAuthTask = null;
    private String mCallName;

    private TextView mScores;
    private TextView mProfileRank;
    private TextView mPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_achievements);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        ImageView img1 = (ImageView) findViewById(R.id.image1);
//        String urlPost1 = BuildConfig.IMAGE_URL + "activity/style-1/Activity-1-img-1.jpg";
//
//        loadImageCircleRequest(img1, urlPost1);
        mUserMgr = UserMgr.getInstance(this);
        initViews();
        makeServiceCalls();
    }
    private void initViews(){
        mScores = (TextView) findViewById(R.id.score);
        mProfileRank = (TextView) findViewById(R.id.rank);
        mPoints = (TextView) findViewById(R.id.points);

    }

    private void makeServiceCalls(){
        int loggedInUserSeq = mUserMgr.getLoggedInUserSeq();
        int loggedInUserCompanySeq = mUserMgr.getLoggedInUserCompanySeq();
        Object[] args = {loggedInUserSeq,loggedInUserCompanySeq};
        String achievementsCountUrl = MessageFormat.format(StringConstants.GET_MYACHIEVEMENT_COUNTS,args);
        String myBadgesURL = MessageFormat.format(StringConstants.GET_MYACHIEVEMENT_MY_BADGES,args);
        mAuthTask = new ServiceHandler(achievementsCountUrl,this, GET_MY_ACHIEVEMENTS);
        mAuthTask.execute();
        mAuthTask = new ServiceHandler(myBadgesURL,this, GET_MY_ACHIEVEMENT_BADGES);
        mAuthTask.execute();
    }



    @Override
    public void processServiceResponse(JSONObject response) {
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(SUCCESS) == 1 ? true : false;
            message = response.getString(MESSAGE);
            if(success){
                if(mCallName.equals(GET_MY_ACHIEVEMENTS)){
                    populateAchievementCounts(response);
                }else if(mCallName.equals(GET_MY_ACHIEVEMENT_BADGES)){
                    populateBadges(response);
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    private void populateAchievementCounts(JSONObject response)throws  Exception{
        JSONObject achievementData = response.getJSONObject(DASHBOARD_DATA);
        String totalScores = achievementData.getString("totalScores");
        JSONObject pendingTraingsJSON = achievementData.getJSONObject("pendingTrainings");
        String maxScore = pendingTraingsJSON.getString("maxScore");
        String profileRank = achievementData.getString("userRank");
        String points = achievementData.getString("points");

        mScores.setText(totalScores+"/" + maxScore);
        if(profileRank == "null"){
            profileRank = "0";
        }
        mProfileRank.setText(profileRank);
        mPoints.setText(points);
    }

    private void populateBadges(JSONObject response)throws Exception{
        JSONArray badgesJSONArray = response.getJSONArray(BADGES);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);

        ConstraintLayout mainLayout = (ConstraintLayout) this.findViewById(R.id.myachievements_layout);
        LinearLayout mainLinearLayout = (LinearLayout) mainLayout.findViewById(R.id.mainLayout);
        LinearLayout fragmentLayout = null;
        int count = 0;
        for (int i=0; i < 0; i++) {
            JSONObject jsonObject = badgesJSONArray.getJSONObject(i);
            String badgeNameStr = jsonObject.getString("title");
            String badgeDetailStr = jsonObject.getString("detail");
            String badgeDateStr = jsonObject.getString("date");
            String imagePathStr = jsonObject.getString("imagepath");

            fragmentLayout = (LinearLayout)inflater.inflate(R.layout.my_achievements_badge_fragment,null);

            TextView badgeName = (TextView) fragmentLayout.findViewById(R.id.badgeName);
            badgeName.setText(badgeNameStr);

            TextView badgeDetails = (TextView) fragmentLayout.findViewById(R.id.badgeDetails);
            badgeDetails.setText(badgeDetailStr);

            TextView badgeDate = (TextView)fragmentLayout.findViewById(R.id.badgeDate);
            badgeDate.setText(badgeDateStr);

            ImageView badgeImageView = (ImageView)fragmentLayout.findViewById(R.id.badgeImage);
            loadImageCircleRequest(badgeImageView, StringConstants.WEB_URL  + imagePathStr);

            mainLinearLayout.addView(fragmentLayout);
        }
        //if(badgesJSONArray.length() == 0){
            TextView noBadgesTextView = new TextView(this);
            noBadgesTextView.setText("No Badges Found");
            noBadgesTextView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            noBadgesTextView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            noBadgesTextView.setTextAlignment(ViewGroup.TEXT_ALIGNMENT_CENTER);
            mainLinearLayout.addView(noBadgesTextView);
        //}
    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    private void loadImageCircleRequest(ImageView img, String url) {
        Glide.with(this)
                .load(url)
                .transform(new ImageViewCircleTransform(this))
                .into(img);
    }
}