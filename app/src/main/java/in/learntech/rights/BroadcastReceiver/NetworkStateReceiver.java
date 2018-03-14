package in.learntech.rights.BroadcastReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import in.learntech.rights.Managers.ModuleMgr;
import in.learntech.rights.services.UploadPendingProgressService;

/**
 * Created by baljeetgaheer on 09/03/18.
 */

public class NetworkStateReceiver extends BroadcastReceiver{
    private static final String TAG = "NetworkStateReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(TAG, "Network connectivity change");

        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
            if (ni != null && ni.isConnectedOrConnecting()) {
                UploadPendingProgressService service = new UploadPendingProgressService(context);
                service.UploadPendingProgress();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Toast.makeText(context, "There's no network connectivity" ,Toast.LENGTH_LONG);
                Log.d(TAG, "There's no network connectivity");
            }
        }
    }
}
