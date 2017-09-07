package in.learntech.rights;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.test.suitebuilder.TestMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class MyTrainings_MyModulesFragment extends Fragment implements IServiceHandler{
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    private static final String ARG_USER_SEQ = "userSeq";
    private static final String ARG_COMPANY_SEQ = "companySeq";
    private ServiceHandler mAuthTask = null;
    private int mUserSeq;
    private int mCompanySeq;
    private View mDrawerLayout;
    private LinearLayout mParentLayout;
    private LayoutHelper mLayoutHelper;
    public static MyTrainings_MyModulesFragment newInstance(int userSeq, int companySeq) {
        MyTrainings_MyModulesFragment fragment = new MyTrainings_MyModulesFragment();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerLayout = inflater.inflate(R.layout.my_training_mymodules_fragment, container, false);
        mParentLayout = (LinearLayout) mDrawerLayout.findViewById(R.id.layout_module);
        mContainer = container;
        mInflater = inflater;
        mLayoutHelper = new LayoutHelper(getActivity(),mInflater,mContainer);
        executeGetModule();
        return mDrawerLayout;
    }

    private void executeGetModule(){
        Object[] args = {mUserSeq,mCompanySeq};
        String getModulesUrl = MessageFormat.format(StringConstants.GET_MODULES,args);
        mAuthTask = new ServiceHandler(getModulesUrl,this);
        mAuthTask.execute();
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
                JSONArray modulesJsonArr = response.getJSONArray("modules");
                int count = modulesJsonArr.length();
                String moduleTabHeader = "No Module";
                if(count > 0){
                    moduleTabHeader = "My Modules" + System.lineSeparator()+  String.valueOf(count) + " Modules";
                }
                TextView textView_moduleHeader = (TextView) mParentLayout.findViewById(R.id.moduleHeader);
                textView_moduleHeader.setText(moduleTabHeader);
                mLayoutHelper.jsonToModuleLayout(modulesJsonArr,mParentLayout);
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
