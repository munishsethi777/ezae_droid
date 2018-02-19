package in.learntech.rights.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import in.learntech.rights.R;

/**
 * Created by baljeetgaheer on 07/09/17.
 */

public class LayoutHelper {
    private Activity mActivity;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    public LayoutHelper(Activity activity, LayoutInflater inflater, ViewGroup container){
        mActivity = activity;
        mInflater = inflater;
        mContainer = container;

    }
    public void jsonToModuleLayout(JSONArray modulesJsonArr, boolean isLockSequence,LinearLayout mParentLayout) throws Exception {
        int moduleCardMargins = 10;
        int count = modulesJsonArr.length();
        String badgeImages[] = new String[3];
        badgeImages[0] = "AdminImages/BadgeImages/Default/5.png";
        badgeImages[1] = "AdminImages/BadgeImages/Default/4.png";
        badgeImages[2] = "AdminImages/BadgeImages/Default/7.png";
        int lasProgress = 0;
        for (int i = 0; i < count; i++) {
            JSONObject jsonObject = modulesJsonArr.getJSONObject(i);
            int lpSeq = 0;
            if(jsonObject.has("learningPlanSeq")) {
                lpSeq = jsonObject.getInt("learningPlanSeq");
            }
            int moduleSeq = jsonObject.getInt("seq");
            String moduleTitle = jsonObject.getString("title");
            String progress = jsonObject.getString("progress");
            String isCompleted = jsonObject.getString("iscompleted");
            String moduleType = jsonObject.getString("moduletype");
            String moduleImage = jsonObject.getString("imagepath");
            Integer timeAllowed = jsonObject.getInt("timeallowed");
            Integer rank = 0;
            String rankStr = jsonObject.getString("leaderboard");
            if(rankStr != null && !rankStr.equals("null") && !rankStr.equals("")){
                rank = jsonObject.getInt("leaderboard");
            }

            String points = jsonObject.getString("points");
            if (points.equals("null")){
                points = "0";
            }
            String circleText = "Q";
            if(moduleImage != null && moduleImage != "null" && !moduleImage.isEmpty()){
            }else{
                moduleImage = "dummy.jpg";
            }
            String imageUrl = StringConstants.IMAGE_URL + "modules/" + moduleImage;
            LinearLayout moduleInternalLayout = (LinearLayout)
                    mInflater.inflate(R.layout.my_training_module_fragment, mContainer, false);

            ImageView imageView = (ImageView) moduleInternalLayout.findViewById(R.id.imageView_moduleImage);
            loadImageRequest(imageView, imageUrl, true);
            Button button_launch = (Button)moduleInternalLayout.findViewById(R.id.button_moduleLaunch);
            if (progress.equals("null")) {
                progress = "0";
            }
            int progressInt = Integer.parseInt(progress);
            if(isLockSequence){
                if(i > 0 && lasProgress < 100){
                    button_launch.setEnabled(false);
                    button_launch.setTextColor(Color.GRAY);
                }
            }
            button_launch.setTag(R.string.lp_seq,lpSeq);
            button_launch.setTag(R.string.module_seq,moduleSeq);
            if (moduleType.equals("lesson")) {
                circleText = "L";
            } else if (moduleType.equals("survey")) {
                circleText = "S";
            }


            int circleColorId = R.color.Red;
            if (progressInt > 0 && progressInt < 100) {
                circleColorId = R.color.Orange;
                LinearLayout timeAllowedLayout = (LinearLayout) moduleInternalLayout.findViewById(R.id.inProgressLayout);
                timeAllowedLayout.setVisibility(View.VISIBLE);
                TextView textView_progress = (TextView) moduleInternalLayout.findViewById(R.id.textView_progress);
                textView_progress.setText(progress + "%");
                button_launch.setText("In Progress");
            }else{
                if(timeAllowed > 0 && progressInt != 100) {
                    LinearLayout inProgressLayout = (LinearLayout) moduleInternalLayout.findViewById(R.id.timeAllotedLayout);
                    inProgressLayout.setVisibility(View.VISIBLE);
                    TextView textView_timeAllowed = (TextView) moduleInternalLayout.findViewById(R.id.textView_moduleTime);
                    textView_timeAllowed.setText(timeAllowed + " Mins");
                }
            }
            if (progressInt == 100) {
                circleColorId = R.color.Green;
                if(moduleType.equals("quiz")) {
                    LinearLayout completedLayout = (LinearLayout) moduleInternalLayout.findViewById(R.id.completedLayout);
                    completedLayout.setVisibility(View.VISIBLE);
                    TextView textView_rank = (TextView) moduleInternalLayout.findViewById(R.id.textView_rank);
                    textView_rank.setText(rank.toString());
                    button_launch.setText("Review");
                }

            }
            //Allotted Badges
            JSONArray earnedBadgesArr = jsonObject.getJSONArray("badges");
            if(earnedBadgesArr.length() > 0) {
                LinearLayout badgeImageLayout = (LinearLayout) moduleInternalLayout.findViewById(R.id.layout_badgeImages);
                TextView textView_lbl_badgeImage = (TextView) moduleInternalLayout.findViewById(R.id.textView_lbl_badges);
                textView_lbl_badgeImage.setVisibility(View.VISIBLE);
                for (int j = 0; j < earnedBadgesArr.length(); j++) {
                    JSONObject badgeJson = earnedBadgesArr.getJSONObject(j);
                    String badgeImageUrl =  badgeJson.getString("imagepath");
                    LinearLayout image_view_Layout = (LinearLayout)
                            mInflater.inflate(R.layout.child_image_view, mContainer, false);
                    String url = StringConstants.WEB_URL + badgeImageUrl;
                    ImageView badgeImageView = (ImageView) image_view_Layout.getChildAt(0);
                    loadImageRequest(badgeImageView, url, true);
                    badgeImageLayout.addView(image_view_Layout);
                }
            }
            String score = jsonObject.getString("score");
            if (score.equals("null")) {
                score = "0";
            }


            TextView textView_moduleName = (TextView) moduleInternalLayout.findViewById(R.id.textView_moduleName);
            textView_moduleName.setText(moduleTitle);


            TextView textView_score = (TextView) moduleInternalLayout.findViewById(R.id.textView_moduleScore);
            textView_score.setText(score);

            TextView textView_point = (TextView) moduleInternalLayout.findViewById(R.id.textView_modulePoint);
            textView_point.setText(points);

//            CardView cardView_circle = (CardView) moduleInternalLayout.findViewById(R.id.timeline_circle);
//            cardView_circle.setCardBackgroundColor(ContextCompat.getColor(mActivity, circleColorId));

//            TextView textView_circle = (TextView) moduleInternalLayout.findViewById(R.id.timeline_circleText);
//            textView_circle.setText(circleText);



//            CardView moduleCardView = (CardView) moduleInternalLayout.findViewById(R.id.moduleCardView);
//            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)moduleCardView.getLayoutParams();
//            marginLayoutParams.setMargins(moduleCardMargins,moduleCardMargins,moduleCardMargins,moduleCardMargins);

            //CardView moduleStatusView = (CardView) moduleInternalLayout.findViewById(R.id.timeline_circle);
           // ViewGroup.MarginLayoutParams moduleStatusViewParams = (ViewGroup.MarginLayoutParams)moduleStatusView.getLayoutParams();
           // moduleStatusViewParams.setMargins(moduleCardMargins,moduleCardMargins,moduleCardMargins,moduleCardMargins);


//            if(count == 1){
//                marginLayoutParams.setMargins(moduleCardMargins,moduleCardMargins*2,moduleCardMargins,moduleCardMargins*2);
//                moduleStatusViewParams.setMargins(moduleCardMargins,moduleCardMargins*2,moduleCardMargins,moduleCardMargins);
//            }else{
//                if(i==0){
//                    marginLayoutParams.setMargins(moduleCardMargins,moduleCardMargins*2,moduleCardMargins,moduleCardMargins);
//                    moduleStatusViewParams.setMargins(moduleCardMargins,moduleCardMargins*2,moduleCardMargins,moduleCardMargins);
//                }
//                if(i==count-1) {//last one
//                    marginLayoutParams.setMargins(moduleCardMargins, moduleCardMargins, moduleCardMargins, moduleCardMargins * 2);
//                }
//            }
           // moduleCardView.requestLayout();
            Animation animation = AnimationUtils.loadAnimation(mActivity,R.anim.fade_in);
           // moduleCardView.startAnimation(animation);
            mParentLayout.addView(moduleInternalLayout);
            lasProgress = progressInt;
        }

    }

    public void jsonToLpLayout(JSONArray lpJsonArr,LinearLayout mParentLayout) throws Exception {
        int moduleCardMargins = 10;
        int count = lpJsonArr.length();
        for (int i = 0; i < count; i++) {

        }

    }

    public void loadImageRequest(ImageView bg, String url, boolean isCircle) {
        if(isCircle) {
            Animation animation = AnimationUtils.loadAnimation(mActivity,R.anim.fade_in);
            Glide.with(mActivity)
                    .load(url)
                    .thumbnail(0.1f)
                    .centerCrop()
                    .crossFade()
                    .animate(animation)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .transform(new ImageViewCircleTransform(mActivity))
                    .into(bg);
        }else{
            Glide.with(mActivity)
                    .load(url)
                    .thumbnail(0.01f)
                    .crossFade()
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(bg);
        }
    }

    public static void showToast(Context context,String message){
        if(message != null && !message.equals("")){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static JSONArray mergeTwoJsonArray(JSONArray jsonArray1,JSONArray jsonArray2)throws  Exception{
        JSONArray mergedJsonArr = new JSONArray();
        for(int i = 0;i<jsonArray1.length();i++){
            mergedJsonArr.put(jsonArray1.getJSONObject(i));
        }
        for(int i = 0;i<jsonArray2.length();i++){
            mergedJsonArr.put(jsonArray2.getJSONObject(i));
        }
        return  mergedJsonArr;
    }

    public LinearLayout getViewByType(String viewType,String value){
        LinearLayout textTypeLayout = (LinearLayout) mInflater.inflate(R.layout.edit_text, null);
        final EditText editText = (EditText) textTypeLayout.getChildAt(1);
        final Calendar myCalendar = Calendar.getInstance();
        if(viewType.toLowerCase().equals("date")){
            if(value != null && !value.isEmpty()){
                int spacePos = value.indexOf(" ");
                if (spacePos > 0) {
                    value= value.substring(0, spacePos);
                }
                String[] date = value.split("/");
                if(value != "null") {
                    myCalendar.set(Calendar.YEAR, Integer.valueOf(date[2]));
                    myCalendar.set(Calendar.MONTH, Integer.valueOf(date[0]) - 1);
                    myCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[1]));
                }
            }
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String myFormat = "MM/dd/yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                    editText.setText(sdf.format(myCalendar.getTime()));
                }
            };

            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(mActivity, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setText(value);
        }else if(viewType.toLowerCase().equals("numeric")){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        return textTypeLayout;
    }

}