package in.learntech.rights.Chatroom;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tubb.smrv.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.StringConstants;

public class ChatRoomActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,View.OnClickListener, ChatRoomClickListener ,IServiceHandler{
    private UserMgr mUserMgr;
    private SwipeMenuRecyclerView rView;
    private ChatRoomAdapter chatRoomAdapter;
    ArrayList<ChatRoomModel> rowListItem;
    private ServiceHandler mAuthTask = null;
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Chat Rooms");
        }
        mUserMgr = UserMgr.getInstance(this);
        rowListItem = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rView = (SwipeMenuRecyclerView) findViewById(R.id.recyclerView);
        rView.setHasFixedSize(false);
        rView.setLayoutManager(layoutManager);
        rView.setNestedScrollingEnabled(false);

        chatRoomAdapter = new ChatRoomAdapter(this,rowListItem);
        rView.setAdapter(chatRoomAdapter);
        chatRoomAdapter.setClickListener(this);
        executeGetChatRoomsCall();
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }

    public void executeGetChatRoomsCall(){
        Object[] args = {mUserMgr.getLoggedInUserSeq(),mUserMgr.getLoggedInUserCompanySeq()};
        String getChatRoomUrl = MessageFormat.format(StringConstants.GET_CHAT_ROOMS,args);
        mAuthTask = new ServiceHandler(getChatRoomUrl,this,this);
        if(swipeLayout != null) {
            mAuthTask.setShowProgress(!swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {}

    @Override
    public void itemClicked(View view, int position) {
        int pos = position + 1;
        //Toast.makeText(this, "Position " + pos + " clicked!", Toast.LENGTH_SHORT).show();

        ChatRoomModel mm = rowListItem.get(position);
        Intent messageChatActivity = new Intent(this,ChatRoomChatActivity.class);
        messageChatActivity.putExtra("messageModel",mm);
        startActivity(messageChatActivity);
        overridePendingTransition(R.anim.firstactivity_enter, R.anim.firstactivity_exit);
    }

    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(StringConstants.SUCCESS) == 1 ? true : false;
            message = response.getString(StringConstants.MESSAGE);
            if(success){
                JSONArray notesJsonArr = response.getJSONArray("chatrooms");
                for (int i=0; i < notesJsonArr.length(); i++) {
                    JSONObject jsonObject = notesJsonArr.getJSONObject(i);
                    int seq = jsonObject.getInt("seq");
                    String title = jsonObject.getString("title");
                    String imageUrl = StringConstants.WEB_URL + jsonObject.getString("imagepath");
                    ChatRoomModel mm = new ChatRoomModel(seq,title,imageUrl);
                    rowListItem.add(mm);
                }
                chatRoomAdapter.notifyItemInserted(rowListItem.size()-1);
                rView.smoothScrollToPosition(rowListItem.size()-1);
                if(swipeLayout != null) {
                    swipeLayout.setRefreshing(false);
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

    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        rowListItem = new ArrayList<>();
        chatRoomAdapter = new ChatRoomAdapter(this,rowListItem);
        rView.setAdapter(chatRoomAdapter);
        chatRoomAdapter.setClickListener(this);
        executeGetChatRoomsCall();
    }
}
