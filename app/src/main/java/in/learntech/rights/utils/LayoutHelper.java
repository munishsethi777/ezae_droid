package in.learntech.rights.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public void jsonToModuleLayout(JSONArray modulesJsonArr, LinearLayout mParentLayout) throws Exception {
        int count = modulesJsonArr.length();
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
            String circleText = "Q";
            if (moduleType.equals("lesson")) {
                circleText = "L";
            } else if (moduleType.equals("survey")) {
                circleText = "S";
            }
            if (progress.equals("null")) {
                progress = "0";
            }
            int progressInt = Integer.parseInt(progress);
            int circleColorId = R.color.Red;
            if (progressInt > 0) {
                circleColorId = R.color.Orange;
            }
            if (progressInt == 100) {
                circleColorId = R.color.Green;
            }
            String score = jsonObject.getString("score");
            if (score.equals("null")) {
                score = "0";
            }
            String imageUrl = StringConstants.IMAGE_URL + "modules/" + moduleImage;
            LinearLayout moduleInternalLayout = (LinearLayout)
                     mInflater.inflate(R.layout.my_training_module_fragment, mContainer, false);

            ImageView imageView = (ImageView) moduleInternalLayout.findViewById(R.id.imageView_moduleImage);
            loadImageRequest(imageView, imageUrl);

            TextView textView_moduleName = (TextView) moduleInternalLayout.findViewById(R.id.textView_moduleName);
            textView_moduleName.setText(moduleTitle);

            TextView textView_moduleProg = (TextView) moduleInternalLayout.findViewById(R.id.textView_moduleProgress);
            textView_moduleProg.setText(progress + "%");

            TextView textView_score = (TextView) moduleInternalLayout.findViewById(R.id.textView_moduleScore);
            textView_score.setText(score);

            CardView cardView_circle = (CardView) moduleInternalLayout.findViewById(R.id.timeline_circle);
            cardView_circle.setCardBackgroundColor(ContextCompat.getColor(mActivity, circleColorId));

            TextView textView_circle = (TextView) moduleInternalLayout.findViewById(R.id.timeline_circleText);
            textView_circle.setText(circleText);

            Button button_launch = (Button)moduleInternalLayout.findViewById(R.id.button_moduleLaunch);
            button_launch.setTag(R.string.lp_seq,lpSeq);
            button_launch.setTag(R.string.module_seq,moduleSeq);
            mParentLayout.addView(moduleInternalLayout);
        }
    }

    private void loadImageRequest(ImageView bg, String url) {
        Glide.with(mActivity)
                .load(url)
                .thumbnail(0.01f)
                .centerCrop()
                .crossFade()
                .into(bg);

    }

    public static void showToast(Context context,String message){
        if(message != null && !message.equals("")){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}