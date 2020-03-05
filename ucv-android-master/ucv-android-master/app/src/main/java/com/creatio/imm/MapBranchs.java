package com.creatio.imm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapBranchs extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private String ID_cupon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_branchs);
        context = MapBranchs.this;
        extras = getIntent().getExtras();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GetItemsMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void GetItemsMenu() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCuponByID")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID", extras.getString("ID_cupon", "0"))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("re",response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            final String ID_business = obj.optString("ID_business");
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
                            String isFavorite = obj.optString("isFavorite");
                            String isReserved = obj.optString("isReserved");

                            JSONArray arrBr = obj.optJSONArray("data_branchs");
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (int j = 0; j < arrBr.length(); j++) {
                                JSONObject objbr = arrBr.getJSONObject(j);
                                String latlng = objbr.optString("location");
                                String namebr = objbr.optString("name");
                                String name_business = objbr.optString("name_business");

                                String[] arrll = latlng.split(",");
                                LatLng sydney = new LatLng(Double.parseDouble(arrll[0]),Double.parseDouble(arrll[1]));
                                builder.include(sydney);
                                mMap.addMarker(new MarkerOptions().position(sydney).title(namebr).snippet(name_business));
                            }
                            int width = getResources().getDisplayMetrics().widthPixels;
                            int height = getResources().getDisplayMetrics().heightPixels;
                            int padding = (int) (width * 0.10);
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                            mMap.animateCamera(cu);



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
