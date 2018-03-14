package in.learntech.rights.services;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;

import in.learntech.rights.BusinessObjects.PendingToUpload;
import in.learntech.rights.Managers.ModuleMgr;
import in.learntech.rights.Managers.QuestionProgressMgr;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.PreferencesUtil;
import in.learntech.rights.utils.StringConstants;

/**
 * Created by baljeetgaheer on 09/03/18.
 */

public class UploadPendingProgressService implements IServiceHandler {
    private Context mContext;
    private ServiceHandler mAuthTask;
    private QuestionProgressMgr questionProgressMgr;
    private String mCallName;
    private ModuleMgr moduleMgr;
    public UploadPendingProgressService(Context context){
        mContext = context;
    }


    public void UploadPendingProgress(){
        moduleMgr = ModuleMgr.getInstance(mContext);
        questionProgressMgr = QuestionProgressMgr.getInstance(mContext);
        List<PendingToUpload> pendingToUploads = moduleMgr.getModulesToSubmit();
        for (PendingToUpload pendingToUpload : pendingToUploads){
            JSONArray progress = questionProgressMgr.getProgressListByModule(
                    pendingToUpload.getModuleSeq(),pendingToUpload.getLearningPlanSeq());
            try {
                executeTrainingSubmitCall(progress,
                        pendingToUpload.getModuleSeq(),
                        pendingToUpload.getLearningPlanSeq());
            }catch (Exception e){}
        }
    }

    private void executeTrainingSubmitCall(JSONArray progressArr,int moduleSeq,int lpSeq)throws Exception{
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(mContext);
        int userSeq = preferencesUtil.getLoggedInUserSeq();
        int companySeq = preferencesUtil.getLoggedInUserCompanySeq();
        String jsonArrString = progressArr.toString();
        jsonArrString = URLEncoder.encode(jsonArrString, "UTF-8");
        Object[] args = {userSeq,companySeq,jsonArrString};
        String notificationUrl = MessageFormat.format(StringConstants.SUBMIT_QUIZ_PROGRESS,args);
        mCallName = moduleSeq + "_" + lpSeq;
        ServiceHandler mAuthTask = new ServiceHandler(notificationUrl,this,mCallName);
        mAuthTask.setShowProgress(false);
        mAuthTask.execute();
    }

    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        boolean success;
        String message;
        try {
            success = response.getInt(StringConstants.SUCCESS) == 1 ? true : false;
            message = response.getString(StringConstants.MESSAGE);
            if(success){
                //Delete module Progress
                String[] seqs = mCallName.split("_");
                moduleMgr.delete(Integer.parseInt(seqs[0]),Integer.parseInt(seqs[1]));
                questionProgressMgr.deleteByModule(Integer.parseInt(seqs[0]),Integer.parseInt(seqs[1]));
            }
        }catch (Exception e){
            message = e.getMessage();
        }

    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }
}
