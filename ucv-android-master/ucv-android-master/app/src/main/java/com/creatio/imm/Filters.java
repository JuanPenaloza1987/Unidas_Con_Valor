package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADCategoriesRight;
import com.creatio.imm.Adapters.ADDetailC;
import com.creatio.imm.Adapters.ADUbications;
import com.creatio.imm.Objects.OCategoryCuponRight;
import com.creatio.imm.Objects.OCupons;
import com.creatio.imm.Objects.OUbications;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class Filters extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences prefs;
    private Context context;
    private ListView lvDetail;
    private ArrayList<OCupons> data =  new ArrayList<>();
    private Bundle extras;
    private ListView lvUbications;
    private ArrayList<OUbications> data_ubications = new ArrayList<>();
    private GridViewWithHeaderAndFooter grid;
    private ArrayList<OCategoryCuponRight> dataRight = new ArrayList<>();
    private String ID_category = "0";
    private String city_selected = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        context = Filters.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvDetail = findViewById(R.id.lvDetails);
        extras = getIntent().getExtras();
        ID_category = extras.getString("category", "0");
        city_selected = extras.getString("city", "0");
        ReadCuponsCategory();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filtros");
        NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
        lvUbications = findViewById(R.id.lvUbications);
        grid = findViewById(R.id.grid);
        Button btnGo = findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadCuponsCategory();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
            }
        });

        ReadUbications();
        ReadCategorysCupon();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void ReadCuponsCategory(){
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvDetail)
                .load(R.layout.item_skeleton)
                .show();
        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCuponsFilters")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("category",ID_category)
                .addBodyParameter("city",city_selected)
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
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
                            String isFavorite = obj.optString("isFavorite");
                            String isReserved = obj.optString("isReserved");
                            String s_vencido = obj.optString("s_vencido");
                            data.add(new OCupons(ID,name,description,create_date,status,quantity,type,category,image,price,discount,isFavorite,isReserved,s_vencido));

                        }
                        if (data.size() == 0){
                            lvDetail.setVisibility(View.GONE);
                        }else{
                            lvDetail.setVisibility(View.VISIBLE);
                        }
                        ADDetailC adapter = new ADDetailC(context, data);
                        lvDetail.setAdapter(adapter);
                        skeletonScreen.hide();

                    } else {

                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Error catch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(context, "Error HTTP", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent =  new Intent(context,MyCupons.class);
            startActivity(intent);

            return true;
        }
        if (id == R.id.action_filters) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.openDrawer(Gravity.RIGHT);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favs) {
            Intent intent = new Intent(context, MyFavs.class);
            startActivity(intent);
        }  else if (id == R.id.nav_not) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void ReadUbications() {
        data_ubications.clear();
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvUbications)
                .load(R.layout.item_skeleton)
                .show();

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllCitys")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String city = obj.optString("city");

                            data_ubications.add(new OUbications("" + i, city,false));

                        }
                        final ADUbications adapter = new ADUbications(context, data_ubications);
                        lvUbications.setAdapter(adapter);
                        lvUbications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                city_selected = data_ubications.get(position).getName();
                                for (int i = 0; i < data_ubications.size(); i++) {
                                    data_ubications.get(i).setSelected(false);
                                }
                                data_ubications.get(position).setSelected(true);
                                adapter.notifyDataSetChanged();
                            }
                        });
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

    public void ReadCategorysCupon() {

        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllCategoryCupon")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String description = obj.optString("description");
                            String create_date = obj.optString("create_date");
                            String status = obj.optString("status");
                            String image = obj.optString("image");
                            dataRight.add(new OCategoryCuponRight(ID, name, description, create_date, status, image, false));

                        }
                        final ADCategoriesRight adapter = new ADCategoriesRight(context, dataRight);
                        grid.setAdapter(adapter);
                        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ID_category = dataRight.get(position).getID();
                                for (int i = 0; i < dataRight.size(); i++) {
                                    dataRight.get(i).setSelected(false);
                                }
                                dataRight.get(position).setSelected(true);
                                adapter.notifyDataSetChanged();
                            }
                        });
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

}
