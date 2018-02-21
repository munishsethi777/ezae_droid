package in.learntech.rights;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import java.util.Date;

import in.learntech.rights.Chatroom.ChatRoomChatActivity;
import in.learntech.rights.Chatroom.ChatRoomModel;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.DateUtil;
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
    private static final String NOMINATE_TRAINING = "nominateTraining";
    private ServiceHandler mAuthTask = null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private int mUserSeq;
    private int mCompanySeq;
    private LinearLayout mChildItemsLayout;
    private LinearLayout mNotesLayout;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    private OnFragmentInteractionListener mListener;
    private String mCallName;
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
        //mFragmentLayout =  (LinearLayout) inflater.inflate(R.layout.dashboard_notification_fragment, container, false);
        ConstraintLayout mFragmentLayout =  (ConstraintLayout) inflater.inflate(R.layout.content_notes, container, false);
        mNotesLayout = (LinearLayout) mFragmentLayout.findViewById(R.id.notesLayout);
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
                if (mCallName != null && mCallName.equals(NOMINATE_TRAINING)) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    executeGetNotificationCall();
                }else {
                    JSONArray notificationJsonArr = response.getJSONArray("notifications");
                    for (int i = 0; i < notificationJsonArr.length(); i++) {
                        JSONObject jsonObject = notificationJsonArr.getJSONObject(i);
                        int seq = jsonObject.getInt("seq");
                        String notificationTitle = jsonObject.getString("title");
                        String type = jsonObject.getString("type");
                        String status = jsonObject.getString("status");
                        String eventType = jsonObject.getString("eventtype");
                        String from = jsonObject.getString("from");
                        Date fromDate = DateUtil.stringToDate(from);
                        from = DateUtil.dateToFormat(fromDate,DateUtil.format);
                        notificationTitle  += "\n on " + from;
                        String buttonTitle = "Nominate";
                        if (type.equals(CURRENTLY_ACTIVE_EVENT)) {
                            if (eventType.equals("chatroom")) {
                                buttonTitle = "Chatroom";
                            } else if (eventType.equals("classroom")) {
                                buttonTitle = "Classroom";
                            }
                        }else{
                            if(status.equals("unapproved")){
                                buttonTitle = "Nominated" ;
                            }
                        }
                        LinearLayout childLayout = (LinearLayout) mInflater.inflate(R.layout.notifications_child_items, mContainer, false);
                        TextView textView = (TextView) childLayout.findViewById(R.id.notification_title);
                        textView.setText(notificationTitle);
                        Button button = (Button) childLayout.findViewById(R.id.notification_button);
                        button.setText(buttonTitle);
                        if(!buttonTitle.equals("Nominated")){
                            button.setOnClickListener(new startChat(seq, notificationTitle, null, buttonTitle,fromDate));
                        }
                        textView.setText(notificationTitle);
                        mNotesLayout.addView(childLayout);
                    }
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }



    class startChat implements View.OnClickListener{
        ChatRoomModel  model;
        String notificationType;
        Date eventFromDate;
        startChat(int seq,String title,String imageUrl,String notType,Date fromDate){
            model = new ChatRoomModel(seq,title,imageUrl);
            notificationType = notType;
            eventFromDate = fromDate;
        }
        @Override
        public void onClick(View view) {
            if(notificationType == "Chatroom") {
                Intent messageChatActivity = new Intent(getActivity(), ChatRoomChatActivity.class);
                messageChatActivity.putExtra("messageModel", model);
                startActivity(messageChatActivity);
                getActivity().overridePendingTransition(R.anim.firstactivity_enter, R.anim.firstactivity_exit);
            }else if(notificationType == "Classroom"){
                Intent intent = new Intent(getActivity(), in.learntech.rights.Events.MainActivity.class);
                intent.putExtra(StringConstants.EVENT_DATE,eventFromDate);
                startActivity(intent);
            }else {
                executeNominateTrainingCall(model.getSeq());
            }
        }
    }


    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    private void executeNominateTrainingCall(int trainingSeq){
        Object[] args = {mUserSeq,mCompanySeq,trainingSeq,0};
        String notificationUrl = MessageFormat.format(StringConstants.NOMINATE_TRAINING,args);
        mAuthTask = new ServiceHandler(notificationUrl,this,NOMINATE_TRAINING,getActivity());
        mAuthTask.execute();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
