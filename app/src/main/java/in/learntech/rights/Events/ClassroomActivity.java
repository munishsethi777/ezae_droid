package in.learntech.rights.Events;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.learntech.rights.Chatroom.ChatRoomModel;
import in.learntech.rights.Events.domain.Event;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.utils.ImageViewCircleTransform;

public class ClassroomActivity extends AppCompatActivity {
    private Event mMessageModel;
    private UserMgr mUserMgr;
    private TextView mTitle;
    private TextView mDescription;
    private TextView mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        mMessageModel = (Event)getIntent().getExtras().getSerializable("event");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mMessageModel.getTitle());
        }
        ImageView toolBarUserImage = (ImageView)findViewById(R.id.imageView_classroom);
        mTitle = (TextView)findViewById(R.id.textView_classroom);
        mDescription = (TextView)findViewById(R.id.textView_des);
        mDate = (TextView)findViewById(R.id.textView_Dates);
        mDescription.setText(mMessageModel.getData().toString() + "\n"+ mMessageModel.getDetail());
        Glide.with(getApplicationContext())
                .load(mMessageModel.getImageUrl())
                .into(toolBarUserImage);

        mUserMgr = UserMgr.getInstance(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
