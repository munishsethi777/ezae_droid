package in.learntech.rights.Leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.StringConstants;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LeaderBoardFragment extends Fragment implements IServiceHandler {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String SELECTED_ITEM_ID = "selectedItemID";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private String mSelectedId;
    private OnListFragmentInteractionListener mListener;
    private ServiceHandler mAuthTask = null;
    private int mUserSeq;
    private int mCompanySeq;
    private RecyclerView mRecyclerView;
    private static SwipeRefreshLayout swipeLayout;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LeaderBoardFragment() {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LeaderBoardFragment newInstance(String selectedItemId,SwipeRefreshLayout swipeLayout) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();
        Bundle args = new Bundle();
        args.putString(SELECTED_ITEM_ID, selectedItemId);
        LeaderBoardFragment.swipeLayout = swipeLayout;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSelectedId = getArguments().getString(SELECTED_ITEM_ID);
        }
        UserMgr userMgr = UserMgr.getInstance(getActivity());
        mUserSeq = userMgr.getLoggedInUserSeq();
        mCompanySeq = userMgr.getLoggedInUserCompanySeq();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            executeLeaderboardDataCall();
        }
        return view;
    }

    private void executeLeaderboardDataCall(){
        String actionUrl = StringConstants.GET_LEADERBOARD_BY_PROFILE;
        String selectedIdArr[] = mSelectedId.split("_");
        String prefix = selectedIdArr[0];
        String id = selectedIdArr[1];
        if(prefix.equals("module")){
            actionUrl = StringConstants.GET_LEADERBOARD_BY_MODULE;
        }else if(prefix.equals("lp")){
            actionUrl = StringConstants.GET_LEADERBOARD_BY_LEARNINGPLAN;
        }
        Object args[] = {mUserSeq,mCompanySeq,id};
        String url = MessageFormat.format(actionUrl,args);
        mAuthTask = new ServiceHandler(url,this,getActivity());
        if(LeaderBoardFragment.swipeLayout != null){
            mAuthTask.setShowProgress(!swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        //showProgress(false);
        boolean success = false;
        String message = null;
        try {
            success = response.getInt(StringConstants.SUCCESS) == 1 ? true : false;
            message = response.getString(StringConstants.MESSAGE);
            if (success) {
                JSONArray leaderBoardDataArr = response.getJSONArray("leaderboarddata");
                List<LeaderboardModel> models = new ArrayList<>();
                 if(leaderBoardDataArr.length() > 0) {
                     for (int i = 0; i < leaderBoardDataArr.length(); i++) {
                         JSONObject leaderboardData = leaderBoardDataArr.getJSONObject(i);
                         LeaderboardModel model = new LeaderboardModel();
                         String userName = leaderboardData.getString("uname");
                         if (userName != null && !userName.equals("") && !userName.equals("null")) {
                         } else {
                             userName = leaderboardData.getString("username");
                         }
                         model.setUserName(userName);
                         model.setDateDiff(leaderboardData.getString("dateofplaytilldiff"));
                         model.setScore(leaderboardData.getString("totalscore"));
                         String imagePath = StringConstants.WEB_URL + leaderboardData.getString("imagepath");
                         model.setUserImage(imagePath);
                         models.add(model);
                     }
                 }else{
                     LeaderboardModel model = new LeaderboardModel();
                     model.setUserName("      No data found for selected option");
                     models.add(model);

                 }
                mRecyclerView.setAdapter(new LeaderboardRecyclerViewAdapter(getActivity().getApplicationContext(),models));
                if(swipeLayout != null){
                    swipeLayout.setRefreshing(false);
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void setCallName(String call) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(LeaderboardModel item);
    }
}
