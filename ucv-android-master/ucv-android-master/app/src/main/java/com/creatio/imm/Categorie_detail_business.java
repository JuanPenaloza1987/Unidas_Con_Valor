package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADBusiness;
import com.creatio.imm.Adapters.ADDetailC;
import com.creatio.imm.Objects.OBusiness;
import com.creatio.imm.Objects.OCupons;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Categorie_detail_business extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private ListView lvDetail;
    private ArrayList<OCupons> data =  new ArrayList<>();
    private ArrayList<OBusiness> data_business =  new ArrayList<>();
    private Bundle extras;
    private TextView  txtBadgeGifts, txtBadgeCupons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie_detail);
        context = Categorie_detail_business.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvDetail = findViewById(R.id.lvDetails);
        extras = getIntent().getExtras();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(extras.getString("name_category",""));
        if (extras.getString("name_category").equalsIgnoreCase("Busqueda de negocios")){
            ReadAllBusiness();

        }else{
            ReadCuponsCategory();
        }
        ReadCountCuponsUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    public void ReadAllBusiness() {
        final ADBusiness adapter = new ADBusiness(context, data_business);
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvDetail)
                .load(R.layout.item_skeleton)
                .show();
        data_business.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllBusinessFind")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("words",extras.getString("words", "0"))
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
                            String image = obj.optString("image");
                            String can_reserve = obj.optString("can_reserve");
                            data_business.add(new OBusiness(ID, name, description, create_date, image,can_reserve));

                        }
                        if (data_business.size() == 0){
                            lvDetail.setVisibility(View.GONE);
                        }else{
                            lvDetail.setVisibility(View.VISIBLE);
                        }

                        lvDetail.setAdapter(adapter);

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
    public void ReadCuponsCategory(){
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvDetail)
                .load(R.layout.item_skeleton)
                .show();
        data.clear();
        String url = "ReadCuponsCategoryByBusiness";


        AndroidNetworking.post(getResources().getString(R.string.apiurl) + url)
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("category",extras.getString("category", "0"))
                .addBodyParameter("words",extras.getString("words", "0"))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .addBodyParameter("ID_business", extras.getString("ID_business", "0"))
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.action_settings);
        FrameLayout gral = (FrameLayout)  alertMenuItem.getActionView();
        txtBadgeCupons = gral.findViewById(R.id.txtBadgeCupons);
        txtBadgeGifts = gral.findViewById(R.id.txtBadgeGifts);
        txtBadgeGifts.setVisibility(View.GONE);
        txtBadgeCupons.setVisibility(View.GONE);
        gral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_only, menu);
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
            Intent intent = new Intent(context, MyCupons.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    public void ReadCountCuponsUser() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCountCuponsUser")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("e",response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String count_gifts = obj.optString("count_gift");
                            String count_cupons = obj.optString("count_cupons");

                            if (count_gifts.equalsIgnoreCase("0")){
                                txtBadgeGifts.setVisibility(View.GONE);
                            }else{
                                txtBadgeGifts.setVisibility(View.VISIBLE);
                                txtBadgeGifts.setText(count_gifts);
                            }
                            if (count_cupons.equalsIgnoreCase("0")){
                                txtBadgeCupons.setVisibility(View.GONE);
                            }else{
                                txtBadgeCupons.setVisibility(View.VISIBLE);
                                txtBadgeCupons.setText(count_cupons);
                            }
                        }

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
}
