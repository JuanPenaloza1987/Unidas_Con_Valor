package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADMyFavs;
import com.creatio.imm.Objects.OMyCupons;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyFavs extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private ListView lvCupons;
    private ArrayList<OMyCupons> data_last = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favs);
        context = MyFavs.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Mis favoritos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mis favoritos");
        lvCupons = findViewById(R.id.lvCupons);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ReadFavsByUser();
    }

    public void ReadFavsByUser() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvCupons)
                .load(R.layout.item_skeleton)
                .show();
        data_last.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadFavsByUser")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
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
                            String name_branch = "";
                            String name_business = obj.optString("name_business");
                            String date_reserved = obj.optString("date_reserved");

                            data_last.add(new OMyCupons(ID, name, description, create_date, status, quantity, type, category, image, price, discount,name_branch,name_business,date_reserved, "", "", ""));

                        }
                        if (data_last.size() == 0){
                            lvCupons.setVisibility(View.GONE);
                        }
                        ADMyFavs adapter = new ADMyFavs(context, data_last);
                        lvCupons.setAdapter(adapter);
                        lvCupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(context,ItemsMenu.class);
                                intent.putExtra("ID_cupon",data_last.get(position).getID());
                                intent.putExtra("image",data_last.get(position).getImage());
                                startActivity(intent);
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
}
