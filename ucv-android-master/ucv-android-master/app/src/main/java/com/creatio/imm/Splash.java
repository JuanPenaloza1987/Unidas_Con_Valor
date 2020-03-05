package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

public class Splash extends AppCompatActivity {
    private Context context;
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = Splash.this;
        FirebaseApp.initializeApp(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        new CountDownTimer(3000, 1500) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                Log.e("r",refreshedToken);
                if (prefs.getBoolean("login", false)) {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        //here can get notification message
                        Intent intent = new Intent(context,MyCupons.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(context, TarjetaMenuIniActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Intent intent = new Intent(context, TarjetaUnidos.class);
                    startActivity(intent);
                    finish();
                }

            }
        }.start();

    }
}
