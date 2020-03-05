package com.creatio.imm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADInfo;
import com.creatio.imm.Objects.OCommit;
import com.creatio.imm.Objects.OInfo;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InformationBusiness extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private String ID_business;
    private ListView lvInfo;
    private ArrayList<OInfo> data = new ArrayList<>();
    private ArrayList<OCommit> data_commit = new ArrayList<>();
    private TextView txtTitle;
    private RatingBar ratingBar;
    private TextView txtCal;
    private ImageButton btnClose;
    float rr = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_business);
        context = InformationBusiness.this;
        lvInfo = findViewById(R.id.lvInfo);
        btnClose = findViewById(R.id.btnClose);
        txtCal = findViewById(R.id.txtCal);
        extras = getIntent().getExtras();
        ID_business = extras.getString("ID_business", "0");
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        txtTitle = findViewById(R.id.txtTitle);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ratingBar = findViewById(R.id.ratingBar);
        if (!extras.getString("rate", "0").contains("null")) {
            ratingBar.setRating(Float.parseFloat(extras.getString("rate", "0")));
            txtCal.setText(String.format("%.2f",Float.parseFloat(extras.getString("rate", "0"))));
        }

        ReadBusinessByID();


    }

    public void ReadBusinessByID() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvInfo)
                .load(R.layout.item_skeleton)
                .show();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadBusinessByID")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID", ID_business)
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

                            txtTitle.setText(name);
                        }

                        JSONArray arrb = response.getJSONArray("datab");
                        for (int i = 0; i < arrb.length(); i++) {
                            JSONObject obj = arrb.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String address = obj.optString("address");
                            String create_date = obj.optString("create_date");
                            String image = obj.optString("image");
                            String phone = obj.optString("tel");
                            String horario = obj.optString("horario");

                            data.add(new OInfo(name, address, R.drawable.ic_branch, phone, horario));
                        }

                        ReadCommits();

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

    public void ReadCommits() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCommits")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID", ID_business)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String commit = obj.optString("commit");
                            String rate = obj.optString("rate");
                            String ID_user = obj.optString("ID_user");
                            String name_user = obj.optString("name_user");
                            String image_user = obj.optString("image_user");
                            String create_date = obj.optString("create_date");
                            String ID_menu = obj.optString("ID_menu");

                            rr = rr + Float.parseFloat(rate);

                            data_commit.add(new OCommit(ID, commit, rate, name_user, image_user, create_date, ID_menu));

                        }
                        rr = rr / arr.length();
                        if (!Double.isNaN(rr)) {
                            ratingBar.setRating(rr);
                            txtCal.setText(String.format("%.2f", rr));
                        }
                        ADInfo adapter = new ADInfo(context, data, data_commit);
                        lvInfo.setAdapter(adapter);

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
