package com.creatio.imm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;


/**
 * Created by gerardo on 1/06/18.
 */

public class Intro extends AppCompatActivity {
    Button btnTag;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_main_layout);
        btnTag = new Button(this);
        btnTag.setBackgroundResource(R.drawable.btn_blue);
        btnTag.setTextColor(getResources().getColor(R.color.white));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = 16;
        params.rightMargin = 16;
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btnTag.setLayoutParams(params);
        btnTag.setText("Omitir");
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        //add button to the layout
       /* RelativeLayout ly = findViewById(R.layout.intro_layout2);
        ly.addView(btnTag);
        img = new ImageView(this);
        RelativeLayout.LayoutParams paramsmg = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        paramsmg.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        paramsmg.topMargin = 16;
        paramsmg.rightMargin = 16;
        paramsmg.leftMargin = 16;
        paramsmg.bottomMargin = 32;
        paramsmg.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        img.setLayoutParams(paramsmg);
        img.setImageResource(R.drawable.c1);
        ly.addView(img);*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Intro.this);
        SharedPreferences.Editor edt = prefs.edit();
        edt.putBoolean("primeravez", false);
        edt.apply();

        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int oldElementIndex, int newElementIndex) {
                //Toast.makeText(getApplicationContext(), "Swiped from " + oldElementIndex + " to " + newElementIndex, Toast.LENGTH_SHORT).show();//
                if (newElementIndex == 3){
                    btnTag.setText("Omitir");
                }
                switch (newElementIndex){
                    case 0:
                        img.setImageResource(R.drawable.c1);
                        break;
                    case 1:
                        img.setImageResource(R.drawable.c2);
                        break;
                    case 2:
                        img.setImageResource(R.drawable.c3);
                        break;
                    case 3:
                        img.setImageResource(R.drawable.c4);
                        break;
                    case 4:
                        img.setImageResource(R.drawable.c5);

                        break;
                }
            }
        });

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                // Probably here will be your exit action
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Just example data for Onboarding
    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr0 = new PaperOnboardingPage(null, null,
                Color.parseColor("#FFFFFF"), R.drawable.ic_branch, R.drawable.ic_zero);

        PaperOnboardingPage scr1 = new PaperOnboardingPage(null, null,
                Color.parseColor("#FFFFFF"), R.drawable.ic_branch, R.drawable.ic_one);
        PaperOnboardingPage scr2 = new PaperOnboardingPage(null, null,
                Color.parseColor("#FFFFFF"), R.drawable.ic_branch, R.drawable.ic_two);
        PaperOnboardingPage scr3 = new PaperOnboardingPage(null, null,
                Color.parseColor("#FFFFFF"), R.drawable.ic_branch, R.drawable.ic_three);
        PaperOnboardingPage scr4 = new PaperOnboardingPage(null, null,
                Color.parseColor("#FFFFFF"), R.drawable.ic_branch, R.drawable.ic_four);



        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr0);
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr4);

        return elements;
    }


}
