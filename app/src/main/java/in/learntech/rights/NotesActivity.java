package in.learntech.rights;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.utils.StringConstants;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener{
    android.app.Fragment mFragment;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setTag(R.string.noteSeq,0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNotedEditorActivity(view);
            }
        });

        UserMgr mUserMgr = UserMgr.getInstance(this);
        mFragment = NotesFragment.newInstance(mUserMgr);
        getFragmentManager().beginTransaction().replace(R.id.notesLayout,mFragment).commit();

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_cancel).setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNoteDetails:
                gotoNotedEditorActivity(view);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                mToolbar.getMenu().findItem(R.id.action_edit).setVisible(false);
                mToolbar.getMenu().findItem(R.id.action_cancel).setVisible(true);
                showHideDeleteButtons(true);
                break;
            case R.id.action_cancel:
                mToolbar.getMenu().findItem(R.id.action_edit).setVisible(true);
                mToolbar.getMenu().findItem(R.id.action_cancel).setVisible(false);
                showHideDeleteButtons(false);
                break;
            default:
                onBackPressed();
                break;
        }
        return true;
    }

    public void showHideDeleteButtons(boolean isShow) {
        LinearLayout mainParentLayout = (LinearLayout)findViewById(R.id.notesLayout);
        LinearLayout notesContainer = (LinearLayout)mainParentLayout.getChildAt(0);
        for(int i=0;i<notesContainer.getChildCount();i++){
            LinearLayout noteslayout = (LinearLayout)notesContainer.getChildAt(i);
            Button delBtn = (Button)noteslayout.findViewById(R.id.btnNoteDelete);
            Button detailBtn = (Button)noteslayout.findViewById(R.id.btnNoteDetails);
            if(isShow) {
                //detailBtn.animate().alpha(0).translationX(50).setDuration(100);
                delBtn.setVisibility(View.VISIBLE);
                delBtn.animate().alpha(1).translationX(-50).setDuration(100);
            }else{
                //detailBtn.animate().alpha(1).translationX(-50).setDuration(100);
                delBtn.animate().alpha(0).translationX(50).setDuration(100);
                delBtn.setVisibility(View.INVISIBLE);
            }
        }

    }

}
