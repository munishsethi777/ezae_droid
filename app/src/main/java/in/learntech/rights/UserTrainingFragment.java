package in.learntech.rights;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import org.json.JSONArray;
import org.json.JSONObject;

import in.learntech.rights.BuildConfig;

/**
 * Created by wahyu on 15/11/16.
 */

@SuppressLint("ValidFragment")
public class UserTrainingFragment extends Fragment {
    public static final String SINGLE = "single";
    public static final String MULTI = "multi";
    public static final String LONG_QUESTION = "longQuestion";
    public static final String YES_NO = "yesNo";
    int wizard_page_position;
    private JSONArray mQuestions;
    private Activity mActivity;
    private JSONArray mQuizProgress;
    private JSONArray mAnswers;
    private String mQuestionType;
    private ConstraintLayout mParentLayout;
    private LinearLayout mOptionsLayout;
    private boolean isQuizProgressExists;
    public UserTrainingFragment(int position,JSONArray questions) {
        this.wizard_page_position = position;
        this.mQuestions = questions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout_id = R.layout.user_training_fragment;
        mActivity = getActivity();
        mParentLayout= (ConstraintLayout)inflater.inflate(layout_id, container, false);
        TextView textView_question = (TextView)mParentLayout.findViewById(R.id.textView_question);
        mOptionsLayout = (LinearLayout)mParentLayout.findViewById(R.id.optionsLayout);
        try{
            JSONObject question = mQuestions.getJSONObject(wizard_page_position);
            mQuestionType = question.getString("type");
            mAnswers = question.getJSONArray("answers");
            mQuizProgress = question.getJSONArray("progress");
            isQuizProgressExists = mQuizProgress.length() > 0;
            if(mQuestionType.equals(SINGLE) || mQuestionType.equals(MULTI)){
                addSingleMultiOptionsViews();
            }else if(mQuestionType.equals(LONG_QUESTION)){
                addLongQuestionView();
            }else if(mQuestionType.equals(YES_NO)){
                addYesNoViews();
            }
            textView_question.setText(wizard_page_position + 1 + ". " +question.getString("title") + " ?");
            addButton();
        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return mParentLayout;
    }

    private void loadImageRequest(ImageView img, String url) {
        Glide.with(getActivity())
                .load(url)
                .thumbnail(0.01f)
                .centerCrop()
                .into(img);
    }


    private void addYesNoViews()throws Exception{
        String selectedAnsTitle = "";
        if(isQuizProgressExists){
            JSONObject progress = mQuizProgress.getJSONObject(0);
            int ansSeq = progress.getInt("answerSeq");
            JSONObject ans = getAnswerBySeqFromArr(ansSeq);
            if(ans != null)
            selectedAnsTitle = ans.getString("title");
        }
        Switch switchYesNo = new Switch(mActivity);
        switchYesNo.setChecked(selectedAnsTitle.equals("yes"));
        mOptionsLayout.addView(switchYesNo);
    }

    private void addLongQuestionView()throws Exception{
        String selectedAnsText = "";
        if(isQuizProgressExists){
            JSONObject progress = mQuizProgress.getJSONObject(0);
            selectedAnsText = progress.getString("answerText");
        }
        EditText texInput = new EditText(mActivity);
        texInput.setSingleLine(false);
        texInput.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        texInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        texInput.setLines(10);
        texInput.setMaxLines(15);
        texInput.setVerticalScrollBarEnabled(true);
        texInput.setMovementMethod(ScrollingMovementMethod.getInstance());
        texInput.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        texInput.setText(selectedAnsText);
        mOptionsLayout.addView(texInput);
    }
    private void addSingleMultiOptionsViews()throws Exception{
        RadioGroup radioGroup = new RadioGroup(mActivity);
        boolean checked = false;
        for (int i=0; i < mAnswers.length(); i++) {
            JSONObject answer = mAnswers.getJSONObject(i);
            int seq = answer.getInt(("seq"));
            if(isQuizProgressExists){
                checked = isAnswerExistsInProgressArr(seq);
            }

            String title = answer.getString("title");

            String feedback = answer.getString("feedback");
            int marks = answer.getInt("marks");
            int negativeMarks = answer.getInt("negativeMarks");
            if(mQuestionType.equals(MULTI)){
                CheckBox checkBox = new CheckBox(mActivity);
                checkBox.setText(title);
                checkBox.setTag(seq);
                checkBox.setChecked(checked);
                mOptionsLayout.addView(checkBox);
            }else{
                RadioButton radioButton = new RadioButton(mActivity);
                radioButton.setText(title);
                radioButton.setTag(seq);
                radioButton.setChecked(checked);
                radioGroup.addView(radioButton);
            }

        }
        if(mQuestionType.equals(SINGLE)){
            mOptionsLayout.addView(radioGroup);
        }
    }


    private void addButton(){
        Button button = new Button(mActivity);
        button.setText("Submit");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        button.setLayoutParams(params);
        //mParentLayout.addView(button);
    }


    private JSONObject getAnswerBySeqFromArr(int seq)throws Exception{
        for (int i=0; i < mAnswers.length(); i++) {
            JSONObject ansJson = mAnswers.getJSONObject(i);
            int ansSeq = ansJson.getInt("seq");
            if(ansSeq == seq){
                return ansJson;
            }
        }
        return null;
    }

    private boolean isAnswerExistsInProgressArr(int seq)throws Exception{
        boolean flag = false;
        for (int i=0; i < mQuizProgress.length(); i++) {
            JSONObject ansJson = mQuizProgress.getJSONObject(i);
            int ansSeq = ansJson.getInt("answerSeq");
            if(ansSeq == seq){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
