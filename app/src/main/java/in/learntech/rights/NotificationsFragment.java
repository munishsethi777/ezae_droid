package in.learntech.rights;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;

import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.StringConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment implements IServiceHandler{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_SEQ = "userSeq";
    private static final String ARG_COMPANY_SEQ = "companySeq";
    public static final String UN_NOMINATED_EVENT = "unNominatedEvent";
    public static final String CURRENTLY_ACTIVE_EVENT = "currentlyActiveEvent";
    private ServiceHandler mAuthTask = null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private int mUserSeq;
    private int mCompanySeq;
    private LinearLayout mChildItemsLayout;
    private LinearLayout mFragmentLayout;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    private OnFragmentInteractionListener mListener;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userSeq Parameter 1.
     * @param companySeq Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */

    public static NotificationsFragment newInstance(int userSeq, int companySeq) {
        NotificationsFragment fragment = new NotificationsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mInflater = inflater;
        mContainer = container;
        mFragmentLayout =  (LinearLayout) inflater.inflate(R.layout.dashboard_notification_fragment, container, false);
        executeGetNotificationCall();
        return mFragmentLayout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void executeGetNotificationCall(){
        Object[] args = {mUserSeq,mCompanySeq};
        String notificationUrl = MessageFormat.format(StringConstants.GET_NOTIFICATIONS,args);
        mAuthTask = new ServiceHandler(notificationUrl,this,getActivity());
        mAuthTask.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                JSONArray notificationJsonArr = response.getJSONArray("notifications");
                for (int i=0; i < notificationJsonArr.length(); i++) {
                    JSONObject jsonObject = notificationJsonArr.getJSONObject(i);
                    String notificationTitle = jsonObject.getString("title");
                    String type = jsonObject.getString("type");
                    String eventType = jsonObject.getString("eventtype");
                    String buttonTitle = "Nominate";
                    if(type.equals(CURRENTLY_ACTIVE_EVENT)){
                        if(eventType.equals("chatroom")) {
                            buttonTitle = "Chatroom";
                        }else if(eventType.equals("classroom")){
                            buttonTitle = "Classroom";
                        }
                    }
                    LinearLayout childLayout = (LinearLayout) mInflater.inflate(R.layout.notifications_child_items, mContainer, false);
                    TextView textView = (TextView) childLayout.getChildAt(0);
                    textView.setText(notificationTitle);
                    Button button = (Button) childLayout.getChildAt(1);
                    button.setText(buttonTitle);
                    textView.setText(notificationTitle);
                    mFragmentLayout.addView(childLayout);
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
