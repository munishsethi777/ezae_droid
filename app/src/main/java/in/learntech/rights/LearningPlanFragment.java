package in.learntech.rights;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.progresviews.ProgressWheel;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;

import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.ImageViewCircleTransform;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;

/**
 * Created by munishsethi on 04/09/17.
 */
@SuppressLint("ValidFragment")
public class LearningPlanFragment extends Fragment implements IServiceHandler{
    private static final String ARG_USER_SEQ = "userSeq";
    private static final String ARG_COMPANY_SEQ = "companySeq";
    private ServiceHandler mAuthTask = null;
    private int mUserSeq;
    private int mCompanySeq;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    private LinearLayout mPrentLayout;
    private LayoutHelper mLayoutHelper;
    public static LearningPlanFragment newInstance(int userSeq, int companySeq) {
        LearningPlanFragment fragment = new LearningPlanFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_SEQ, userSeq);
        args.putInt(ARG_COMPANY_SEQ, companySeq);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserSeq = getArguments().getInt(ARG_USER_SEQ);
            mCompanySeq = getArguments().getInt(ARG_COMPANY_SEQ);
        }
    }

    private void executeGetLPDetail(){
        Object[] args = {mUserSeq,mCompanySeq};
        String getLPDetailUrl = MessageFormat.format(StringConstants.GET_LEARNING_PLAN_DETAIL,args);
        mAuthTask = new ServiceHandler(getLPDetailUrl,this,getActivity());
        mAuthTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View drawerLayout = inflater.inflate(R.layout.my_training_learningplans_fragment, container, false);
        mPrentLayout = (LinearLayout) drawerLayout.findViewById(R.id.layout_lp);
        mContainer = container;
        mInflater = inflater;
        mLayoutHelper = new LayoutHelper(getActivity(),mInflater,mContainer);
        executeGetLPDetail();
        return drawerLayout;
    }

    private void loadImageCircleRequest(ImageView img, String url){
        Glide.with(this)
                .load(url)
                .transform(new ImageViewCircleTransform(getActivity()))
                .into(img);
    }

    private void loadImageRequest(ImageView bg, String url) {
        Glide.with(this)
                .load(url)
                .thumbnail(0.01f)
                .centerCrop()
                .crossFade()
                .into(bg);
    }

    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        //showProgress(false);
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(StringConstants.SUCCESS) == 1 ? true : false;
            message = response.getString(StringConstants.MESSAGE);
            if(success){
                JSONArray lpJsonArr = response.getJSONArray("learningPlanDetails");
                int count = lpJsonArr.length();
                for (int i=0; i < count; i++) {
                    JSONObject lpJson = lpJsonArr.getJSONObject(i);
                    int progress = lpJson.getInt("percentCompleted");
                    String description = lpJson.getString("description");
                    JSONArray jsonArray = lpJson.getJSONArray("modules");
                    JSONObject moduleJson = jsonArray.getJSONObject(0);
                    String imageName = moduleJson.getString("imagepath");
                    Integer points = moduleJson.getInt("points");
                    Integer score = moduleJson.getInt("score");
                    if(imageName != null && !imageName.equals("null") && !imageName.equals("")){
                    }else{
                        imageName = "dummy.jpg";
                    }
                    String imageUrl = StringConstants.IMAGE_URL +"modules/"+imageName;
                    String title = lpJson.getString("title");
                    LinearLayout lpFragment = (LinearLayout)
                            mInflater.inflate(R.layout.learning_plan_list_fragment, mContainer, false);
                    ImageView lpImage = (ImageView)lpFragment.findViewById(R.id.imageView_lpImage);
                    loadImageCircleRequest(lpImage,imageUrl);

                    TextView textView_title = (TextView) lpFragment.findViewById(R.id.textview_lpTitle);
                    textView_title.setText(title);

                    TextView textView_description = (TextView) lpFragment.findViewById(R.id.textView_lpDescription);
                    textView_description.setText(description);

                    TextView textView_score = (TextView) lpFragment.findViewById(R.id.textView_score);
                    textView_score.setText(score.toString());

                    TextView textView_points = (TextView) lpFragment.findViewById(R.id.textView_points);
                    textView_points.setText(points.toString());


                    mPrentLayout.addView(lpFragment);
                }
            }
        }catch (Exception e){

            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setCallName(String call) {

    }
}
