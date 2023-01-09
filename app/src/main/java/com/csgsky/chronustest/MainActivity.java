package com.csgsky.chronustest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.csgsky.cscommon.MessageMonitor;
import com.csgsky.cscommon.MessageMonitorBuilder;
import com.csgsky.cscommon.MonitorCallback;
import com.csgsky.cscommon.MonitorConfig;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = findViewById(R.id.start);
        start.setOnClickListener(v -> {
            Log.d("MessageMonitorLog", "--------start----------------");
            Log.d("MessageMonitorLog", "--------start-----1-----------");
            Log.d("MessageMonitorLog", "--------start-----2-----------");
            Log.d("MessageMonitorLog", "--------start-----3-----------");
            Log.d("MessageMonitorLog", "--------start-----4-----------");
            Log.d("MessageMonitorLog", "--------start-----5-----------");
            MonitorConfig monitorConfig = MessageMonitorBuilder.startBuilder()
                    .setCatDuration(2000L)
                    .setSaveCount(500)
                    .setMinLagTime(100L)
                    .setMinMessageCost(1000L)
                    .setIdleClear(false).build();
            MessageMonitor.startMonitor(monitorConfig);
        });

        Button slow = findViewById(R.id.slow);
        slow.setOnClickListener(v -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "123", Toast.LENGTH_SHORT).show();
        });
        Button test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mainReportMessageData = MessageMonitor.INSTANCE.getMainReportMessageData();
                Log.d("MainData", mainReportMessageData);
            }
        });

        MessageMonitor.setMonitorCallBack(new MonitorCallback() {
            @Override
            public void reportLagMessages(@NotNull String errorType, @NotNull String messages) {
                Log.i(errorType, "reportLagMessages: " + messages);
            }

            @Override
            public void monitorError(@NotNull String errorType, @NotNull String errorLog) {
                Log.i("MessageMonitor", "errorType: " + errorType + " , errorLog: " + errorLog);
            }
        });

    }
}