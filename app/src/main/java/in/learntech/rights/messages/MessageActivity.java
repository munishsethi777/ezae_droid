package in.learntech.rights.messages;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.tubb.smrv.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.learntech.rights.DashboardActivity;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.MyTrainings;
import in.learntech.rights.R;
import in.learntech.rights.SendMessageActivity;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;

import java.text.MessageFormat;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,
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
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        intent = getIntent();
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
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        executeGetMessagesCall();
        Intent intent = getIntent();
        if(intent.hasExtra(StringConstants.DATA_STRING)){
            String dataJsonString = intent.getExtras().getString(StringConstants.DATA_STRING);
            try {
                JSONObject dataJson = new JSONObject(dataJsonString);
                openDirectMessage(dataJson);

            }catch (JSONException ex){}

        }
    }
    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        rowListItem = new ArrayList<>();
        messageAdapter = new MessageAdapter(this,rowListItem);
        rView.setAdapter(messageAdapter);
        messageAdapter.setClickListener(this);
        executeGetMessagesCall();
    }

        @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_sendMessage:
                Intent intent = new Intent(this,SendMessageActivity.class);
                startActivity(intent);
                break;  //optional
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

    private  void openDirectMessage(JSONObject dataJson) throws JSONException{
        Integer entitySeq = dataJson.getInt("entitySeq");
        String entityType = dataJson.getString("entityType");
        String fromUserName = dataJson.getString("fromUserName");
        MessageModel mm = new MessageModel();
        mm.setChattingUser(fromUserName);
        mm.setChattingUserSeq(entitySeq);
        mm.setChattingUserType(entityType);
        Intent messageChatActivity = new Intent(this,MessageChatActivity.class);
        messageChatActivity.putExtra("messageModel",mm);
        startActivity(messageChatActivity);
        overridePendingTransition(R.anim.firstactivity_enter, R.anim.firstactivity_exit);
    }

    public void executeGetMessagesCall(){
        Object[] args = {mUserMgr.getLoggedInUserSeq(),mUserMgr.getLoggedInUserCompanySeq()};
        String notificationUrl = MessageFormat.format(StringConstants.GET_MESSAGES,args);
        mAuthTask = new ServiceHandler(notificationUrl,this,GET_MESSAGES,this);
        mAuthTask.setShowProgress(!swipeLayout.isRefreshing());
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
                        String chattingUser = jsonObject.getString("name");
                        String imageUrl = StringConstants.WEB_URL + jsonObject.getString("userImage");
                        String chattingWithUserType = jsonObject.getString("userType");
                        int chattingWithUserSeq = jsonObject.getInt("userSeq");
                        MessageModel mm = new MessageModel(messageText,dated,chattingUser,imageUrl,
                                        chattingWithUserType,chattingWithUserSeq);
                        rowListItem.add(mm);
                    }
                    messageAdapter.notifyItemInserted(rowListItem.size()-1);
                    rView.smoothScrollToPosition(rowListItem.size()-1);
                    if(swipeLayout != null){
                        swipeLayout.setRefreshing(false);
                    }
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
