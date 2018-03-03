package in.learntech.rights;

import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tubb.smrv.SwipeHorizontalMenuLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;

import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.StringConstants;

/**
 * Created by munishsethi on 15/09/17.
 */

public class NotesFragment extends Fragment implements IServiceHandler {
    private static final String GET_ALL_NOTES = "getAllNotes";
    private static final String DELETE_NOTE = "deleteNote";

    private static final String ARG_USER_SEQ = "userSeq";
    private static final String ARG_COMPANY_SEQ = "companySeq";
    private ServiceHandler mAuthTask = null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private int mUserSeq;
    private int mCompanySeq;
    private LinearLayout mNotesLayout;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    private String mCallName;
    private int mNoteSeq;
    private NotesActivity parentActivity;
    public NotesFragment() {
        // Required empty public constructor
    }
    public static NotesFragment newInstance(UserMgr mUserMgr,NotesActivity parent) {
        NotesFragment fragment = new NotesFragment();
        int mLoggedInUserSeq = mUserMgr.getLoggedInUserSeq();
        int mLoggedInCompanySeq = mUserMgr.getLoggedInUserCompanySeq();

        Bundle args = new Bundle();
        args.putInt(ARG_USER_SEQ, mLoggedInUserSeq);
        args.putInt(ARG_COMPANY_SEQ, mLoggedInCompanySeq);
        fragment.setArguments(args);
        fragment.parentActivity = parent;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserSeq = getArguments().getInt(ARG_USER_SEQ);
            mCompanySeq = getArguments().getInt(ARG_COMPANY_SEQ);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mInflater = inflater;
        mContainer = container;
        ConstraintLayout mFragmentLayout =  (ConstraintLayout) inflater.inflate(R.layout.content_notes_child, container, false);
        mNotesLayout = (LinearLayout) mFragmentLayout.findViewById(R.id.notesLayout);
        executeGetNotesCall();
        return mFragmentLayout;
    }

    public void executeGetNotesCall(){
        Object[] args = {mUserSeq,mCompanySeq};
        String notificationUrl = MessageFormat.format(StringConstants.GET_ALL_NOTES,args);
        mAuthTask = new ServiceHandler(notificationUrl,this,GET_ALL_NOTES,getActivity());
        if(parentActivity.swipeLayout != null) {
            mAuthTask.setShowProgress(!parentActivity.swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(SUCCESS) == 1 ? true : false;
            message = response.getString(MESSAGE);
            if(success) {
                if (mCallName.equals(GET_ALL_NOTES)) {
                    JSONArray notesJsonArr = response.getJSONArray("notes");
                    for (int i = 0; i < notesJsonArr.length(); i++) {
                        JSONObject jsonObject = notesJsonArr.getJSONObject(i);
                        int noteSeq = jsonObject.getInt("seq");
                        String noteDetails = jsonObject.getString("details");
                        String noteCreatedOn = jsonObject.getString("createdon");

                        SwipeHorizontalMenuLayout swipeLayout = (SwipeHorizontalMenuLayout) mInflater.inflate(
                                R.layout.notes_fragment, mContainer, false);
                        swipeLayout.setId(noteSeq);
                        TextView notesHeader = (TextView) swipeLayout.findViewById(R.id.notesHeader);
                        notesHeader.setText(noteDetails);

                        TextView notesCreatedOn = (TextView) swipeLayout.findViewById(R.id.notesCreatedOn);
                        notesCreatedOn.setText(noteCreatedOn);

                        RelativeLayout contentRL = (RelativeLayout) swipeLayout.findViewById(R.id.smContentView);
                        contentRL.setTag(R.string.noteSeq, noteSeq);

                        RelativeLayout deleteRL = (RelativeLayout) swipeLayout.findViewById(R.id.smMenuViewRight);
                        deleteRL.setTag(R.string.noteSeq, noteSeq);

                        mNotesLayout.addView(swipeLayout);
                    }
                } else if (mCallName.equals(DELETE_NOTE)) {
                    int noteSeq = response.getInt("noteSeq");
                    SwipeHorizontalMenuLayout layout = (SwipeHorizontalMenuLayout) getActivity().findViewById(noteSeq);
                    mNotesLayout.removeView(layout);
                    LayoutHelper.showToast(getActivity(), "Deleted Successfully");
                }
                if(parentActivity.swipeLayout != null) {
                    parentActivity.swipeLayout.setRefreshing(false);
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
        mCallName = call;
    }

    public void deleteNote(final int noteSeq) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Note");
        builder.setMessage("Do you really want to delete the Note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteAction(noteSeq);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void deleteNoteAction(int noteSeq){
        Object[] args = {mUserSeq,mCompanySeq,noteSeq};
        String deleteNoteURL = MessageFormat.format(StringConstants.DELETE_NOTE,args);
        mAuthTask = new ServiceHandler(deleteNoteURL,this,DELETE_NOTE,getActivity());
        mAuthTask.execute();
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
