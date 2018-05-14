package in.learntech.rights;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
public class MyTrainings_LearningPlansFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IServiceHandler{
    private static final String ARG_USER_SEQ = "userSeq";
    private static final String ARG_COMPANY_SEQ = "companySeq";
    private ServiceHandler mAuthTask = null;
    private int mUserSeq;
    private int mCompanySeq;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    private LinearLayout mPrentLayout;
    private LayoutHelper mLayoutHelper;
    private SwipeRefreshLayout swipeLayout;
    public static MyTrainings_LearningPlansFragment newInstance(int userSeq, int companySeq) {
        MyTrainings_LearningPlansFragment fragment = new MyTrainings_LearningPlansFragment();
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
        if(swipeLayout != null){
            mAuthTask.setShowProgress(!swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View drawerLayout = inflater.inflate(R.layout.my_training_learningplans_fragment, container, false);
        mPrentLayout = (LinearLayout) drawerLayout.findViewById(R.id.layout_lp);
        swipeLayout = (SwipeRefreshLayout) drawerLayout.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
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
                    String title = lpJson.getString("title");
                    boolean isLockSequence = lpJson.getBoolean("lockSequence");
                    LinearLayout lpInternalLayout = (LinearLayout)
                            mInflater.inflate(R.layout.my_training_learningplan_header, mContainer, false);
                    ProgressWheel progressWheel = (ProgressWheel)lpInternalLayout.findViewById(R.id.progressBar_lp);
                    progressWheel.setStepCountText(String.valueOf(progress)+"%");
                    progressWheel.setPercentage(progress*4);

                    TextView  textView_lpName = (TextView)lpInternalLayout.findViewById(R.id.textView_learningPlanName);
                    textView_lpName.setText(title);

                    JSONArray modulesJsonArr = lpJson.getJSONArray("modules");
                    int moduleCount = modulesJsonArr.length();
                    TextView  textView_totalModules = (TextView)lpInternalLayout.findViewById(R.id.textView_lpTotalModules);
                    textView_totalModules.setText(String.valueOf(moduleCount) + " Modules");

                    mPrentLayout.addView(lpInternalLayout);
                    mLayoutHelper.jsonToModuleLayout(modulesJsonArr,isLockSequence,mPrentLayout);
                }
                if(swipeLayout != null){
                    swipeLayout.setRefreshing(false);
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

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        mPrentLayout.removeAllViews();
        executeGetLPDetail();
    }
}
