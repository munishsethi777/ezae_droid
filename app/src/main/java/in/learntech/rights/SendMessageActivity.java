package in.learntech.rights;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tubb.smrv.SwipeMenuRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import in.learntech.rights.BusinessObjects.CompanyUser;
import in.learntech.rights.Managers.CompanyUserManager;
import in.learntech.rights.messages.MessageAdapter;
import in.learntech.rights.messages.MessageChatActivity;
import in.learntech.rights.messages.MessageClickListener;
import in.learntech.rights.messages.MessageModel;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.utils.DateUtil;
import in.learntech.rights.utils.StringConstants;

public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener,
        MessageClickListener, IServiceHandler,SearchView.OnQueryTextListener {
    private SwipeMenuRecyclerView rView;
    ArrayList<CompanyUser> rowListItem;
    ArrayList<CompanyUser> filteredListItem;
    private CompanyUserAdapter companyUserAdapter;
    private CompanyUserManager mCompanyUserMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rView = (SwipeMenuRecyclerView) findViewById(R.id.recyclerView_users);
        rView.setHasFixedSize(false);
        rView.setLayoutManager(layoutManager);
        rView.setNestedScrollingEnabled(false);
        mCompanyUserMgr = CompanyUserManager.getInstance(this);
        rowListItem = mCompanyUserMgr.getCompanyUsersForLoggedInUser();
        this.sortList();
        filteredListItem = rowListItem;
        companyUserAdapter = new CompanyUserAdapter(this,filteredListItem);
        rView.setAdapter(companyUserAdapter);
        companyUserAdapter.setClickListener(this);
    }

    private void sortList(){
        Collections.sort(rowListItem, new Comparator<CompanyUser>(){
            public int compare(CompanyUser obj1, CompanyUser obj2) {
                // ## Ascending order
                return obj1.getType().compareToIgnoreCase(obj2.getType()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void itemClicked(View view, int position) {
        int pos = position + 1;
        //Toast.makeText(this, "Position " + pos + " clicked!", Toast.LENGTH_SHORT).show();
        CompanyUser companyUser = filteredListItem.get(position);
        Intent messageChatActivity = new Intent(this,MessageChatActivity.class);
        String dated = DateUtil.dateToString(new Date());
        String imageDirName = "UserImages/";
        String type = "User";
        if(!companyUser.getType().equals("user")){
            imageDirName = "AdminImages/";
            type = "Admin";
        }

        String imageName = companyUser.getImageName();
        String imageUrl = null;
        if(imageName != null && !imageName.equals("null") && !imageName.equals("")) {
            imageUrl = StringConstants.IMAGE_URL + imageDirName + imageName;
        }
        MessageModel mm = new MessageModel(null,dated,
                type + ": " + companyUser.getUserName(),
                imageUrl,
                companyUser.getType(),
                companyUser.getSeq());
        messageChatActivity.putExtra("messageModel",mm);
        startActivity(messageChatActivity);
        overridePendingTransition(R.anim.firstactivity_enter, R.anim.firstactivity_exit);
    }

    @Override
    public void processServiceResponse(JSONObject response) {

    }

    @Override
    public void setCallName(String call) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param query the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String query) {
        filteredListItem = filter(rowListItem, query);
        companyUserAdapter = new CompanyUserAdapter(this,filteredListItem);
        rView.setAdapter(companyUserAdapter);
        companyUserAdapter.setClickListener(this);
        rView.invalidate();
        //rView.scrollToPosition(0);
        return true;
    }

    private static ArrayList<CompanyUser> filter(ArrayList<CompanyUser> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final ArrayList<CompanyUser> filteredModelList = new ArrayList<>();
        for (CompanyUser model : models) {
            final String text = model.getUserName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
