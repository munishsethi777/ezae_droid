package in.learntech.rights.BroadcastReceiver;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;
import in.learntech.rights.DashboardActivity;
import in.learntech.rights.LoginActivity;
import in.learntech.rights.Managers.UserMgr;
import in.learntech.rights.R;
import in.learntech.rights.UserTrainingActivity;
import in.learntech.rights.utils.PreferencesUtil;
import in.learntech.rights.utils.StringConstants;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mNotificationManager = (NotificationManager)
            this.getSystemService(Context.NOTIFICATION_SERVICE);
        String jsonString = intent.getExtras().getString("message");
        String title = "Right";
        String description = "";
        String action = "";
        Intent newIntent = new Intent(this,LoginActivity.class);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            title = jsonObject.getString("title");
            description = jsonObject.getString("description");
            Integer entitySeq = jsonObject.getInt("entitySeq");
            String entityType = jsonObject.getString("entityType");
            UserMgr mUserManager = UserMgr.getInstance(getApplicationContext());
            if(!mUserManager.isUserLoggedIn()){
                PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(getApplicationContext());
                preferencesUtil.setNotificationState(true);
                preferencesUtil.setNotificationData(entitySeq.toString(),entityType);
            }else{
                if(entityType.equals("module")){
                    newIntent = new Intent(this,UserTrainingActivity.class);
                    newIntent.putExtra(StringConstants.LP_SEQ,0);
                    newIntent.putExtra(StringConstants.MODULE_SEQ,entitySeq);
                }
            }
        }catch (Exception e){
            Log.e("GcmIntentService",e.getMessage());
        }

        newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    //.setColor(Color.parseColor("#ff5031"))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSound(uri);

        mBuilder.setContentIntent(contentIntent);
        Notification n = mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, n);

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}

