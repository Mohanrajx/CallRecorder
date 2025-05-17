package com.example.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        CallRecordingService service = new CallRecordingService();
        service.onCreate(); // Initialize the service
        
        if (state != null) {
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                // Call started
                service.startRecording();
                Log.d("CallRecorder", "Call started - recording");
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // Call ended
                service.stopRecordingAndSendEmail();
                Log.d("CallRecorder", "Call ended - stopping recording");
            }
        }
    }
}
