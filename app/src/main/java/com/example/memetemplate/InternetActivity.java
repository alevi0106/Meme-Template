package com.example.memetemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class InternetActivity extends AppCompatActivity {
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);
        Button reload_button = findViewById(R.id.reload_btn);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("01A52AC1C81DB7346FE6FCABE563A6B0").build();
        mAdView.loadAd(adRequest);
        reload_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ( ConnectionUtils.isConnected(InternetActivity.this)){
                    Intent intent = new Intent(InternetActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(InternetActivity.this, "No Internet Connection.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
