package com.example.munishsethi.myapplication.messages;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.example.munishsethi.myapplication.R;

import java.util.ArrayList;

public class MessageChatActivity extends AppCompatActivity implements View.OnClickListener,MessageClickListener {
    ArrayList<MessageChatModel> rowListItem;
    MessageChatAdapter rcAdapter;
    RecyclerView rView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Narender Sharma");
        }
        rowListItem = getAllItemList();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rView = (RecyclerView) findViewById(R.id.recyclerView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(layoutManager);
        rView.setNestedScrollingEnabled(true);

        rcAdapter = new MessageChatAdapter(this, rowListItem);
        rView.setAdapter(rcAdapter);
        rcAdapter.setClickListener(this);
        rView.smoothScrollToPosition(rowListItem.size()+1);
    }


    private ArrayList<MessageChatModel> getAllItemList() {
        ArrayList<MessageChatModel> allItems = new ArrayList<>();
        MessageChatModel dt;

        dt = new MessageChatModel(1,"Hello Munish how are you today","2:40 mins",false);
        allItems.add(dt);

        dt = new MessageChatModel(1,"I am good Remi. Hope you too doing good.","2:35 mins",true);
        allItems.add(dt);

        dt = new MessageChatModel(1,"Did you got a chance to make plans for visiting the hills" +
                " this weekend. It would be fun if we can go together","2 mins",false);
        allItems.add(dt);

        dt = new MessageChatModel(1,"Yes i have to first confirm with my appointments.","2 mins",true);
        allItems.add(dt);

        dt = new MessageChatModel(1,"Oh. i know you are too busy to go, but lets try to get hold of it","2 mins",false);
        allItems.add(dt);

        dt = new MessageChatModel(1,"Even i am too eager to visit the place, its been a since i got an off from my" +
                "hectic schedule. Pls wait for a couple of days and i will let you kno.","2 mins",true);
        allItems.add(dt);

        dt = new MessageChatModel(1,"Ok Bye, we will talk soon","2 mins",true);
        allItems.add(dt);

        dt = new MessageChatModel(1,"Bye for now. Salaam","2 mins",false);
        allItems.add(dt);

        return allItems;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSend:
                EditText composeMessageText = (EditText)findViewById(R.id.composeMessage);
                MessageChatModel mcm = new MessageChatModel(0,composeMessageText.getText().toString(),"just now",true);
                rowListItem.add(mcm);
                rcAdapter.notifyItemInserted(rowListItem.size()-1);
                rView.smoothScrollToPosition(rowListItem.size()-1);
                break;
            default:
                break;
        }
    }

    @Override
    public void itemClicked(View view, int position) {
        int pos = position + 1;
        Toast.makeText(this, "Position " + pos + " clicked!", Toast.LENGTH_SHORT).show();
    }
}
