package br.com.golsat.testyello;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import rocks.alce.idlibrary.YelloAPI;
import rocks.alce.idlibrary.YelloMonitorService;
import rocks.alce.idlibrary.listeners.YelloApiDriverListener;
import rocks.alce.idlibrary.listeners.YelloApiVehicleListener;
import rocks.alce.idlibrary.models.YelloDriver;
import rocks.alce.idlibrary.models.YelloDriverData;
import rocks.alce.idlibrary.models.YelloVehicle;
import rocks.alce.idlibrary.models.YelloVehicleData;
import rocks.alce.idlibrary.receivers.YelloMonitorReceiver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static final String API_KEY = "21d3d4a2-6b5e-4c71-85f3-71b1822f121d";
    static final String CLIENT_UID = "599c2cef-a546-4d93-9e92-14d14a29985b";

    private YelloAPI yelloAPI;
    private Button startStopButton;

    private String mDriverUUID;
    private String mVehicleUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopButton = findViewById(R.id.bt_start_stop);
        startStopButton.setOnClickListener(this);

        yelloAPI = new YelloAPI(this, API_KEY, CLIENT_UID);

        YelloDriver driver = new YelloDriver("José Oliveira", "45877986254", new Date(), YelloDriver.MALE,
                YelloDriver.DIVORCED);

        yelloAPI.setDriver(driver, new YelloApiDriverListener() {

            @Override
            public void setDriverFinished(boolean success, YelloDriver driver, String errorMessage) {
                Log.d("YELLO", "setDriverFinished: " + driver.uuid);

                mDriverUUID = driver.uuid;
            }

            @Override
            public void getDriverFinished(boolean success, YelloDriverData data, String errorMessage) {}
        });

        YelloVehicle vehicle = new YelloVehicle("KJI-9856", "Onix preto", YelloVehicle.CAR, "", "", "2017", "");
        yelloAPI.setVehicle(vehicle, new YelloApiVehicleListener() {

            @Override
            public void setVehicleFinished(boolean success, YelloVehicle vehicle, String
                    errorMessage) {
                Log.d("YELLO", "setVehicleFinished: " + vehicle.uuid);

                mVehicleUUID = vehicle.uuid;
            }

            @Override
            public void getVehiclesFinished(boolean success, ArrayList<YelloVehicle> list,
                                            String errorMessage) {}
            @Override
            public void getVehicleDataFinished(boolean success, YelloVehicleData data,
                                               String errorMessage) {}
        });

    }

    @Override
    public void onClick(View v) {
        if ( !YelloMonitorService.isServiceRunning) {
            start();
        } else {
            stop();
        }
    }

    private void stop() {
        YelloMonitorService.stopServiceMonitoring(
                this, new MonitoringReceiver(this), API_KEY, CLIENT_UID, mDriverUUID,
                mVehicleUUID);
    }

    private void start() {
        YelloMonitorService.startServiceMonitoring(this, new MonitoringReceiver(this), API_KEY, CLIENT_UID);
    }


    private static class MonitoringReceiver implements YelloMonitorReceiver.ResultReceiverCallback {

        private WeakReference<MainActivity> activityRef;
        private MonitoringReceiver(MainActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void monitoringStatusChanged(boolean isMonitoring, String message) {
            /*if (isMonitoring) {
                activityRef.get().startStopButton.setText("Stop");
            }
            else {
                activityRef.get().startStopButton.setText("Start");
            }*/

            Toast.makeText(activityRef.get().getBaseContext(), message, Toast.LENGTH_SHORT).show();

            Log.d("Yello", "monitoringStatusChanged: " + message);
        }
        @Override
        public void fileStorageFinished(boolean success, String message) {
            Toast toast = Toast.makeText(activityRef.get().getBaseContext(), message, Toast.LENGTH_SHORT);
            toast.show();
            Log.d("Yello", "fileStorageFinished: " + message);
        }
        @Override
        public void syncFinished(boolean success, String message) {
            Toast toast = Toast.makeText(activityRef.get().getBaseContext(), message, Toast.LENGTH_SHORT);

            toast.show();
            Log.d("Yello", "syncFinished: " + message);
        }
    }

}
