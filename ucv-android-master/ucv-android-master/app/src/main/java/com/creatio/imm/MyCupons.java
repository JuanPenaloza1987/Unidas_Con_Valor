package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MyCupons extends AppCompatActivity{
    private FragmentPagerItemAdapter adapter;
    private SharedPreferences prefs;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cupons);
        context = MyCupons.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Tus cupones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tus cupones");

        FragmentPagerItems viewPagers = FragmentPagerItems.with(context).add(getResources().getString(R.string.mycupons), FRMyCupons.class).create();
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), viewPagers);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPagers.add(FragmentPagerItem.of(getResources().getString(R.string.especiales), FRSpecials.class));
        adapter.notifyDataSetChanged();
//        viewPagers.add(FragmentPagerItem.of(getResources().getString(R.string.myorders), FRMyOrders.class));
//        adapter.notifyDataSetChanged();
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        return super.onSupportNavigateUp();
    }
}
