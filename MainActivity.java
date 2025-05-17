package com.example.callrecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sharedPreferences = getSharedPreferences("CallRecorderPrefs", MODE_PRIVATE);
        
        if (!sharedPreferences.contains("sender_email")) {
            startActivity(new Intent(this, EmailConfigActivity.class));
            finish();
            return;
        }
        
        checkAndRequestPermissions();
    }
    
    private void checkAndRequestPermissions() {
        List<String> requiredPermissions = new ArrayList<>();
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.RECORD_AUDIO);
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) 
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        
        if (!requiredPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions.toArray(new String[0]),
                REQUEST_PERMISSIONS
            );
        } else {
            startService(new Intent(this, CallRecordingService.class));
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "All permissions are required", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            startService(new Intent(this, CallRecordingService.class));
        }
    }
}
