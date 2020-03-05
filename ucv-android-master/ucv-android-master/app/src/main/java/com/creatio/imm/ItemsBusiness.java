package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.Adapters.ADDetailCRC;
import com.creatio.imm.Adapters.ADGallery;
import com.creatio.imm.Objects.OCupons;
import com.creatio.imm.Objects.OGallery;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ItemsBusiness extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private String ID_business;
    private CollapsingToolbarLayout coll;
    private ArrayList<OCupons> data = new ArrayList<>();
    private ArrayList<OGallery> data_gall = new ArrayList<>();
    private RecyclerView rcCupons;
    private Button btnInformation;
    private RatingBar ratingBar;
    private String rate = "0";
    private LinearLayout lyMenu;
    private ADDetailCRC adapter;
    private TextView txtCal;
    ViewPager viewPager;
    CircleIndicator indicator;
    private Button btnMenu;
    private String can_reserve = "0";
    private LinearLayout lyNoHay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        coll = findViewById(R.id.toolbar_layout);
        context = ItemsBusiness.this;
        rcCupons = findViewById(R.id.rcCupons);
        lyMenu = findViewById(R.id.lyMenu);
        lyNoHay = findViewById(R.id.lyNoHay);
        btnMenu = findViewById(R.id.btnMenu);
        viewPager = findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rcCupons.setLayoutManager(llm);
        rcCupons.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        rcCupons.addItemDecoration(dividerItemDecoration);




        data = new ArrayList<>();
        adapter = new ADDetailCRC(context, data);
        rcCupons.setAdapter(adapter);
        LayoutInflater myinflater = getLayoutInflater();
//        ViewGroup myHeader = (ViewGroup) myinflater.inflate(R.layout.header_items_business, lvCupons, false);
        //lvCupons.addHeaderView(myHeader, null, false);
         btnInformation = findViewById(R.id.btnInformation);
        ratingBar = findViewById(R.id.ratingBar);
        txtCal = findViewById(R.id.txtCal);
        extras = getIntent().getExtras();
        ID_business = extras.getString("ID_business", "0");
        can_reserve = extras.getString("can_reserve", "0");
        if (can_reserve.equalsIgnoreCase("0")){
            lyMenu.setVisibility(View.GONE);
        }else{
            lyMenu.setVisibility(View.GONE);
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        final ImageView bgImage = findViewById(R.id.bgImage);
        getSupportActionBar().setTitle("");
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(1000, 1000)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(extras.getString("image", ""))
                .apply(options)
                .into(bgImage);

        GetCuponsBusiness();
        getSupportActionBar().setTitle("");
        btnInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InformationBusiness.class);
                intent.putExtra("ID_business", ID_business);
                intent.putExtra("rate", rate);
                startActivity(intent);
            }
        });
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(extras.getString("name", ""));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, com.creatio.imm.Menu.class);
                intent.putExtra("ID_business", ID_business);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        setResult(301);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(301);
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(301);
    }

    public void GetCuponsBusiness() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(rcCupons)
                .load(R.layout.item_skeleton)
                .adapter(adapter)
                .show();
        data.clear();
        data_gall.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCuponsBusiness")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID", extras.getString("ID_business", "0"))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("resp", response.toString());
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        JSONArray arrGal = response.getJSONArray("datai");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String description = obj.optString("description");
                            String create_date = obj.optString("create_date");
                            String status = obj.optString("status");
                            String type = obj.optString("type");
                            String category = obj.optString("category");
                            String quantity = obj.optString("quantity");
                            String image = obj.optString("image");
                            String price = obj.optString("price");
                            String discount = obj.optString("discount");
                            String date_finish = obj.optString("date_finish");
                            final String restrictions = obj.optString("restrictions");
                            String isFavorite = obj.optString("isFavorite");
                            String isReserved = obj.optString("isReserved");
                            String s_vencido = obj.optString("s_vencido");
                            rate = obj.optString("rate");
                            data.add(new OCupons(ID, name, description, create_date, status, quantity, type, category, image, price, discount, isFavorite, isReserved,s_vencido));


                        }
                        //data_gall.add(new OGallery("0","portada",extras.getString("image", ""),"0"));
                        for (int i = 0; i < arrGal.length(); i++) {
                            JSONObject obj = arrGal.getJSONObject(i);
                            String ID = obj.optString("ID_business");
                            String name = obj.optString("name");
                            String name_server = obj.optString("name_server");
                            String orden = obj.optString("orden");

                            data_gall.add(new OGallery(ID,name,name_server,orden));
                        }
                            if (!rate.contains("null")) {
                            txtCal.setText(String.format("%.2f", Float.parseFloat(rate)));
                            ratingBar.setRating(Float.parseFloat(rate));
                        }
                        viewPager.setAdapter(new ADGallery(context,data_gall));
                        indicator.setViewPager(viewPager);
//                        adapter = new ADDetailCRC(context, data);
//                        rcCupons.setAdapter(adapter);
                        if (data.size() == 0){

                            rcCupons.setVisibility(View.GONE);
                            lyNoHay.setVisibility(View.VISIBLE);
                        }else{
                            rcCupons.setVisibility(View.VISIBLE);
                            lyNoHay.setVisibility(View.GONE);
                        }
                        skeletonScreen.hide();
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            setResult(300);
            finish();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.business_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, MyCupons.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
