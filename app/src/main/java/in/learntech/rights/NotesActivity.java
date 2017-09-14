package in.learntech.rights;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import in.learntech.rights.utils.sorting.ListFragment;

public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
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
        //SORTING WIDGET
        showFragment(ListFragment.newInstance());

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

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "fragment").commit();
    }

}
