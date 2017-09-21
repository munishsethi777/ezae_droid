package in.learntech.rights.messages;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.ImageViewCircleTransform;
import in.learntech.rights.utils.StringConstants;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MessageChatActivity extends AppCompatActivity implements View.OnClickListener,
                                    MessageClickListener,IServiceHandler {

    private static String GET_MESSAGE_DETAILS = "getMessageDetails";
    private static String SEND_MESSAGE_CHAT = "sendMessageChat";
    private ServiceHandler mAuthTask = null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private String mCallName;
    private UserMgr mUserMgr;
    private MessageModel mMessageModel;
    ArrayList<MessageChatModel> rowListItem;
    MessageChatAdapter rcAdapter;
    RecyclerView rView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        mMessageModel = (MessageModel)getIntent().getExtras().getSerializable("messageModel");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mMessageModel.getChattingUser());
        }
        TextView toolBarUserName = (TextView)toolbar.findViewById(R.id.name);
        toolBarUserName.setText(mMessageModel.getChattingUser());

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

        rcAdapter = new MessageChatAdapter(this, rowListItem);
        rView.setAdapter(rcAdapter);
        rcAdapter.setClickListener(this);
        executeGetMessageDetailsCall();
        refreshChatUI();
    }

    private void refreshChatUI(){
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(6000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                executeGetMessageDetailsCall();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
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

    public void executeGetMessageDetailsCall(){
        int afterMessageSeq = 0;
        if(rowListItem.size()>0){
            MessageChatModel lastMCM = rowListItem.get(rowListItem.size()-1);
            afterMessageSeq = lastMCM.getSeq();
        }

        Object[] args = {mUserMgr.getLoggedInUserSeq(),mUserMgr.getLoggedInUserCompanySeq(),
                mMessageModel.getChattingUserSeq(),mMessageModel.getChattingUserType(),afterMessageSeq};
        String url = MessageFormat.format(StringConstants.GET_MESSAGE_DETAILS,args);
        mAuthTask = new ServiceHandler(url,this,GET_MESSAGE_DETAILS,this);
        mAuthTask.setShowProgress(false);
        mAuthTask.execute();
    }

    public void executeSendMessageCall(){
        EditText composeMessageText = (EditText)findViewById(R.id.messageText);
        String messageStr = URLEncoder.encode(String.valueOf(composeMessageText.getText()));
        if(messageStr != "" && messageStr != null) {
            MessageChatModel lastMCM = rowListItem.get(rowListItem.size() - 1);
            Object[] args = {mUserMgr.getLoggedInUserSeq(), mUserMgr.getLoggedInUserCompanySeq(),
                    mMessageModel.getChattingUserSeq(), mMessageModel.getChattingUserType(), messageStr, lastMCM.getSeq()};
            String url = MessageFormat.format(StringConstants.SEND_MESSAGE_CHAT, args);
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
                if(mCallName.equals(GET_MESSAGE_DETAILS)){
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
    }

    private void addMessagesChatModel(JSONArray chatJsonArr){
        try {
            for (int i = 0; i < chatJsonArr.length(); i++) {
                JSONObject jsonObject = chatJsonArr.getJSONObject(i);
                int chatSeq = jsonObject.getInt("seq");
                String dated = jsonObject.getString("dated");
                String messagetext = jsonObject.getString("messagetext");
                int fromUserSeq = 0;
                if (jsonObject.getString("fromuserseq") != null) {
                    fromUserSeq = jsonObject.getInt("fromuserseq");
                }
                boolean isSent = false;
                if (mUserMgr.getLoggedInUserSeq() == fromUserSeq) {
                    isSent = true;
                }
                MessageChatModel mcm = new MessageChatModel(chatSeq, messagetext, dated, isSent);
                rowListItem.add(mcm);

            }
            MessageChatModel lastMCM = rowListItem.get(rowListItem.size()-1);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView toolBarLastTime = (TextView)toolbar.findViewById(R.id.timeLastViews);
            toolBarLastTime.setText(lastMCM.getTime());

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

