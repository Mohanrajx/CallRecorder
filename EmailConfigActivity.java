package com.example.callrecorder;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EmailConfigActivity extends AppCompatActivity {
    private EditText etSenderEmail, etAppPassword, etReceiverEmail;
    private Button btnSave;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_config);

        etSenderEmail = findViewById(R.id.etSenderEmail);
        etAppPassword = findViewById(R.id.etAppPassword);
        etReceiverEmail = findViewById(R.id.etReceiverEmail);
        btnSave = findViewById(R.id.btnSave);
        
        sharedPreferences = getSharedPreferences("CallRecorderPrefs", MODE_PRIVATE);

        btnSave.setOnClickListener(v -> {
            String senderEmail = etSenderEmail.getText().toString().trim();
            String appPassword = etAppPassword.getText().toString().trim();
            String receiverEmail = etReceiverEmail.getText().toString().trim();

            if (senderEmail.isEmpty() || appPassword.isEmpty() || receiverEmail.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("sender_email", senderEmail);
            editor.putString("app_password", appPassword);
            editor.putString("receiver_email", receiverEmail);
            editor.apply();

            String deviceName = getDeviceName();
            String subject = "Call Recorder successfully installed on " + deviceName;
            String body = "This is a confirmation that Call Recorder app has been successfully installed and configured on " + deviceName;
            
            EmailSender.sendEmail(this, senderEmail, appPassword, receiverEmail, subject, body, null);

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
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
}
