package com.example.munishsethi.myapplication.messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.example.munishsethi.myapplication.R;
import com.tubb.smrv.SwipeMenuRecyclerView;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener, MessageClickListener {

    ArrayList<MessageModel> rowListItem;
    private ItemTouchHelper mItemTouchHelper;
    private SwipeMenuRecyclerView rView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        rowListItem = getAllItemList();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rView = (SwipeMenuRecyclerView) findViewById(R.id.recyclerView);
        rView.setHasFixedSize(false);
        rView.setLayoutManager(layoutManager);
        rView.setNestedScrollingEnabled(false);

        MessageAdapter adapter = new MessageAdapter(this,rowListItem);
        rView.setAdapter(adapter);
        adapter.setClickListener(this);
    }





    private ArrayList<MessageModel> getAllItemList(){
        ArrayList<MessageModel> allItems = new ArrayList<>();
        MessageModel model;
        model = new MessageModel("Hello Brother 1","1 day ago","Christina","img/a1.jpg");
        allItems.add(model);
        model = new MessageModel("Hello Brother 2","2 day ago","Christina","img/a2.jpg");
        allItems.add(model);
        model = new MessageModel("Hello Brother 3","3 day ago","Christina","img/a3.jpg");
        allItems.add(model);
        return  allItems;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //case R.id.btnLoginSignupBack:
                //onBackPressed();
                //break;
            //case R.id.buttonEdit:
                //Toast.makeText(this, "button edit clicked!", Toast.LENGTH_SHORT).show();
                //break;
            default:
                break;
        }
    }

    @Override
    public void itemClicked(View view, int position) {
        int pos = position + 1;
        Toast.makeText(this, "Position " + pos + " clicked!", Toast.LENGTH_SHORT).show();

        Intent messageChatActivity = new Intent(this,MessageChatActivity.class);
        startActivity(messageChatActivity);
    }


}
