package in.learntech.rights.Chatroom;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.messages.MessageChatAdapter;
import in.learntech.rights.messages.MessageChatModel;
import in.learntech.rights.messages.MessageClickListener;
import in.learntech.rights.messages.MessageModel;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.ImageViewCircleTransform;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;

public class ChatRoomChatActivity extends AppCompatActivity implements View.OnClickListener,
                                    ChatRoomClickListener,IServiceHandler {

    private static String GET_CHAT_ROOM_DETAILS = "getChatRoomDetails";
    private static String SEND_MESSAGE_CHAT = "sendMessageChat";
    private ServiceHandler mAuthTask = null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private String mCallName;
    private UserMgr mUserMgr;
    private ChatRoomModel mMessageModel;
    ArrayList<ChatRoomChatModel> rowListItem;
    ChatRoomChatAdapter rcAdapter;
    RecyclerView rView;
    Thread refreshThread;

    @Override
    protected void onStop() {
        super.onStop();
        refreshThread.interrupt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshThread.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        mMessageModel = (ChatRoomModel)getIntent().getExtras().getSerializable("messageModel");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mMessageModel.getTitle());
        }
        TextView toolBarUserName = (TextView)toolbar.findViewById(R.id.name);
        toolBarUserName.setText(mMessageModel.getTitle());

        ImageView toolBarUserImage = (ImageView)toolbar.findViewById(R.id.userImage);
        Glide.with(getApplicationContext())
                .load(mMessageModel.getImageURL())
                .transform(new ImageViewCircleTransform(getApplicationContext()))
                .into(toolBarUserImage);

        mUserMgr = UserMgr.getInstance(this);
        rowListItem = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rView = (RecyclerView) findViewById(R.id.recyclerView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(layoutManager);
        rView.setNestedScrollingEnabled(true);

        rcAdapter = new ChatRoomChatAdapter(this, rowListItem);
        rView.setAdapter(rcAdapter);
        rcAdapter.setClickListener(this);
        executeGetChatRoomDetailCall();
        refreshChatUI();
    }

    private void refreshChatUI(){
        refreshThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(6000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                executeGetChatRoomDetailCall();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        refreshThread.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSend:
                executeSendMessageCall();
                break;
            default:
                break;
        }
    }

    public void executeGetChatRoomDetailCall(){
        int afterMessageSeq = 0;
        if(rowListItem.size()>0){
            ChatRoomChatModel lastMCM = rowListItem.get(rowListItem.size()-1);
            afterMessageSeq = lastMCM.getSeq();
        }

        Object[] args = {mUserMgr.getLoggedInUserSeq(),mUserMgr.getLoggedInUserCompanySeq(),
                mMessageModel.getSeq(),afterMessageSeq};
        String url = MessageFormat.format(StringConstants.GET_CHAT_ROOMS_DETAIL,args);
        mAuthTask = new ServiceHandler(url,this, GET_CHAT_ROOM_DETAILS,this);
        mAuthTask.setShowProgress(false);
        mAuthTask.execute();
    }

    public void executeSendMessageCall(){
        EditText composeMessageText = (EditText)findViewById(R.id.messageText);
        String messageStr = URLEncoder.encode(String.valueOf(composeMessageText.getText()));
        if(messageStr != "" && messageStr != null) {
            int afterMessageSeq = 0;
            if(rowListItem.size() > 0) {
                ChatRoomChatModel chatRoomChatModel = rowListItem.get(rowListItem.size() - 1);
                afterMessageSeq = chatRoomChatModel.getSeq();
            }
            String userType = "user";
            Object[] args = {mUserMgr.getLoggedInUserSeq(), mUserMgr.getLoggedInUserCompanySeq(),
                    mMessageModel.getSeq(), userType,mUserMgr.getLoggedInUserName(), afterMessageSeq,messageStr};
            String url = MessageFormat.format(StringConstants.SEND_CHAT_ROOM_CHAT, args);
            mAuthTask = new ServiceHandler(url, this, SEND_MESSAGE_CHAT, this);
            mAuthTask.setShowProgress(false);
            mAuthTask.execute();
        }
    }


    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(SUCCESS) == 1 ? true : false;
            message = response.getString(MESSAGE);
            if(success){
                if(mCallName.equals(GET_CHAT_ROOM_DETAILS)){
                    JSONArray chatJsonArr = response.getJSONArray("messages");
                    addMessagesChatModel(chatJsonArr);

                }else if(mCallName.equals(SEND_MESSAGE_CHAT)){
                    JSONArray chatJsonArr = response.getJSONArray("messages");
                    addMessagesChatModel(chatJsonArr);
                    EditText composeMessageText = (EditText)findViewById(R.id.messageText);
                    composeMessageText.setText("");
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            LayoutHelper.showToast(getApplicationContext(),message);
        }
    }

    private void addMessagesChatModel(JSONArray chatJsonArr){
        try {
            for (int i = 0; i < chatJsonArr.length(); i++){
                JSONObject jsonObject = chatJsonArr.getJSONObject(i);
                int chatSeq = jsonObject.getInt("post_id");
                String dated = jsonObject.getString("post_time");
                String fromUser = jsonObject.getString("post_user");
                String name = jsonObject.getString("uname");
                String type = jsonObject.getString("user_type");
                if(type.equals("admin")){
                    name = jsonObject.getString("aname");
                }else {
                    if(name == null || name.equals("") || name.equals("null")){
                        name =   fromUser;
                    }
                }
                String messagetext = jsonObject.getString("post_message");
                boolean isSent = false;
                if (mUserMgr.getLoggedInUserName().equals(fromUser)) {
                   isSent = true;
                }
                ChatRoomChatModel mcm = new ChatRoomChatModel(chatSeq, messagetext, dated, name,isSent);
                if(!rowListItem.contains(mcm)) {
                    rowListItem.add(mcm);
                }
            }
            ChatRoomChatModel lastMCM = rowListItem.get(rowListItem.size()-1);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView toolBarLastTime = (TextView)toolbar.findViewById(R.id.timeLastViews);
            if(lastMCM.getTime() != null && lastMCM.getTime() != "") {
                toolBarLastTime.setText("Last Sent:"+lastMCM.getTime());
            }

            rView.getRecycledViewPool().clear();
            rcAdapter.notifyDataSetChanged();
            rcAdapter.notifyItemInserted(rowListItem.size()-1);
            rView.smoothScrollToPosition(rowListItem.size()-1);
        }catch(Exception e) {

        }
    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.secondactivity_enter, R.anim.secondactivity_exit);
        return true;
    }

    @Override
    public void itemClicked(View view, int position) {
        //int pos = position + 1;
        //Toast.makeText(this, "Position " + pos + " clicked!", Toast.LENGTH_SHORT).show();
    }


}

