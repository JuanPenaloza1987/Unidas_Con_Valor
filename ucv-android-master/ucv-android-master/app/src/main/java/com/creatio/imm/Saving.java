package com.creatio.imm;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Saving extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving);
        context = Saving.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        extras = getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Tu ahorro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tu ahorro");
        final TextView txtSaving = findViewById(R.id.txtSaving);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, Float.valueOf(extras.getString("saving", "0")));
        valueAnimator.setDuration(1500);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                txtSaving.setText("$ " + valueAnimator.getAnimatedValue().toString());

            }
        });
        valueAnimator.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
