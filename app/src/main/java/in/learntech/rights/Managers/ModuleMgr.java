package in.learntech.rights.Managers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import in.learntech.rights.BusinessObjects.PendingToUpload;
import in.learntech.rights.BusinessObjects.QuestionProgress;
import in.learntech.rights.DataStoreMgr.DBUtil;
import in.learntech.rights.DataStoreMgr.ModuleDataStore;
import in.learntech.rights.services.Interface.IServiceHandler;
import in.learntech.rights.services.ServiceHandler;
import in.learntech.rights.utils.AppStatus;
import in.learntech.rights.utils.LayoutHelper;
import in.learntech.rights.utils.PreferencesUtil;
import in.learntech.rights.utils.StringConstants;

/**
 * Created by baljeetgaheer on 09/03/18.
 */

public class ModuleMgr {
    private static ModuleMgr sInstance;
    private static ModuleDataStore moduleDataStore;
    private static PreferencesUtil mPreferencesUtil;
    private static QuestionProgressMgr questionProgressMgr;



    public static synchronized ModuleMgr getInstance(Context context) {
        if (sInstance == null){
            sInstance = new ModuleMgr();
            moduleDataStore = new ModuleDataStore(context);
            mPreferencesUtil = PreferencesUtil.getInstance(context);
            questionProgressMgr = QuestionProgressMgr.getInstance(context);
        }
        return sInstance;
    }

    public List<PendingToUpload> getModulesToSubmit(){
        int userSeq = mPreferencesUtil.getLoggedInUserSeq();
        List<PendingToUpload> pendingModules =  moduleDataStore.
                getPendingModules(userSeq);
        return pendingModules;
    }

    public boolean delete(int moduleSeq,int learningPlanSeq){
        int userSeq = mPreferencesUtil.getLoggedInUserSeq();
        return  moduleDataStore.deletePendingModules(userSeq,moduleSeq,learningPlanSeq);
    }

    public void savePendingModules(int moduleSeq,int lpSeq)throws Exception{
        PendingToUpload module = new PendingToUpload();
        module.setModuleSeq(moduleSeq);
        int userSeq = mPreferencesUtil.getLoggedInUserSeq();
        module.setUserSeq(userSeq);
        module.setLearningPlanSeq(lpSeq);
        module.setDated(new Date());
        moduleDataStore.save(module);
    }



}
