package in.learntech.rights.messages;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tubb.smrv.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;

import java.text.MessageFormat;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener,
                                        MessageClickListener, IServiceHandler {
    private static String GET_MESSAGES = "getMessages";
    private ServiceHandler mAuthTask = null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private String mCallName;
    private UserMgr mUserMgr;

    ArrayList<MessageModel> rowListItem;
    private ItemTouchHelper mItemTouchHelper;
    private SwipeMenuRecyclerView rView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("My Messages");
        }

        mUserMgr = UserMgr.getInstance(this);
        rowListItem = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rView = (SwipeMenuRecyclerView) findViewById(R.id.recyclerView);
        rView.setHasFixedSize(false);
        rView.setLayoutManager(layoutManager);
        rView.setNestedScrollingEnabled(false);

        messageAdapter = new MessageAdapter(this,rowListItem);
        rView.setAdapter(messageAdapter);
        messageAdapter.setClickListener(this);

        executeGetMessagesCall();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
        }
    }

    @Override
    public void itemClicked(View view, int position) {
        int pos = position + 1;
        //Toast.makeText(this, "Position " + pos + " clicked!", Toast.LENGTH_SHORT).show();

        MessageModel mm = rowListItem.get(position);
        Intent messageChatActivity = new Intent(this,MessageChatActivity.class);
        messageChatActivity.putExtra("messageModel",mm);
        startActivity(messageChatActivity);
        overridePendingTransition(R.anim.firstactivity_enter, R.anim.firstactivity_exit);
    }

    public void executeGetMessagesCall(){
        Object[] args = {mUserMgr.getLoggedInUserSeq(),mUserMgr.getLoggedInUserCompanySeq()};
        String notificationUrl = MessageFormat.format(StringConstants.GET_MESSAGES,args);
        mAuthTask = new ServiceHandler(notificationUrl,this,GET_MESSAGES,this);
        mAuthTask.execute();
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
                if(mCallName.equals(GET_MESSAGES)){
                    JSONArray notesJsonArr = response.getJSONArray("messages");
                    for (int i=0; i < notesJsonArr.length(); i++) {
                        JSONObject jsonObject = notesJsonArr.getJSONObject(i);
                        String messageText = jsonObject.getString("messageText");
                        String dated = jsonObject.getString("dated");
                        String chattingUser = jsonObject.getString("userName");
                        String imageUrl = StringConstants.WEB_URL + jsonObject.getString("userImage");
                        String chattingWithUserType = jsonObject.getString("userType");
                        int chattingWithUserSeq = jsonObject.getInt("userSeq");
                        MessageModel mm = new MessageModel(messageText,dated,chattingUser,imageUrl,
                                        chattingWithUserType,chattingWithUserSeq);
                        rowListItem.add(mm);
                    }
                    messageAdapter.notifyItemInserted(rowListItem.size()-1);
                    rView.smoothScrollToPosition(rowListItem.size()-1);
                }
            }
        }catch (Exception e){

            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
