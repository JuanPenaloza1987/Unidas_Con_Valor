package com.creatio.imm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADNotificacions;
import com.creatio.imm.Objects.ONotifications;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private ListView lvNotifications;
    private ArrayList<ONotifications> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        context = Notifications.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Mis notificaciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mis notificaciones");
        lvNotifications = findViewById(R.id.lvNotifications);
        ReadNotificacionsByUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();

    }
    public void ReadNotificacionsByUser() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvNotifications)
                .load(R.layout.item_skeleton)
                .show();
        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadNotificacionsByUser")
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
                            String image = obj.optString("image");
                            String ID_cupon = obj.optString("ID_cupon");

                            data.add(new ONotifications(ID,name,description,create_date,image,ID_cupon));


                        }
                        ADNotificacions adapter = new ADNotificacions(context, data);
                        lvNotifications.setAdapter(adapter);

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
