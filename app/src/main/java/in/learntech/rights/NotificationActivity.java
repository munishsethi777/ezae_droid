package in.learntech.rights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;

import in.learntech.rights.services.Interface.IServiceHandler;

public class NotificationActivity extends AppCompatActivity
        implements IServiceHandler {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_notification_fragment);
    }

    private void populateNotifications(){

    }


    @Override
    public void processServiceResponse(JSONObject response) {

    }

    @Override
    public void setCallName(String call) {

    }
}
