package in.learntech.rights;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.MessageFormat;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.StringConstants;

public class NotesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

    NotesFragment mFragment;
    Toolbar mToolbar;
    int mDeletingNoteSeq;
    UserMgr mUserMgr;
    protected SwipeRefreshLayout swipeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("My Notes");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setTag(R.string.noteSeq,0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNotedEditorActivity(view);
            }
        });

        mUserMgr = UserMgr.getInstance(this);
        mFragment = NotesFragment.newInstance(mUserMgr,this);
        getFragmentManager().beginTransaction().replace(R.id.notesLayout,mFragment).commit();
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        //SORTING WIDGET
        //showFragment(ListFragment.newInstance());

        //PDF VIEWER WIDGET
        //String url = "http://docs.google.com/gview?url=http://www.ezae.in/docs/moduledocs/Book1.xlsx&embedded=true";
        //WebView webView = (WebView)findViewById(R.id.pdfWebView);
        //webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl(url);

        //SLIDER SEEKBAR WIDGET
//        ArrayMap<Integer,String> itemsMap = new ArrayMap<>();
//        itemsMap.put(1,"Ludhiana");
//        itemsMap.put(2,"Jalandhar");
//        itemsMap.put(3,"Amritsar");
//        itemsMap.put(4,"Chandigarh");
//        LinearLayout mSeekLin = (LinearLayout) findViewById(R.id.lin1);
//        CustomSeekBar customSeekBar = new CustomSeekBar(this,itemsMap, Color.DKGRAY);
//        customSeekBar.addSeekBar(mSeekLin);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.smContentView:
                gotoNotedEditorActivity(view);
                break;
            case R.id.smMenuViewRight:
                mDeletingNoteSeq= (int) view.getTag(R.string.noteSeq);
                mFragment.deleteNote(mDeletingNoteSeq);
                break;
            default:
                break;
        }
    }

    private void gotoNotedEditorActivity(View view){
        int noteSeq = (int)view.getTag(R.string.noteSeq);
        Intent intent = new Intent(this,NotesEditor.class);
        intent.putExtra(StringConstants.NOTE_SEQ,noteSeq);
        startActivity(intent);
        overridePendingTransition(R.anim.firstactivity_enter, R.anim.firstactivity_exit);
        finish();
    }


    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        mFragment = NotesFragment.newInstance(mUserMgr,this);
        getFragmentManager().beginTransaction().replace(R.id.notesLayout,mFragment).commit();
    }
}
