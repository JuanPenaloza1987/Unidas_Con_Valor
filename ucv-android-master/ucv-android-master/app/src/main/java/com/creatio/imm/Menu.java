package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADMenu;
import com.creatio.imm.Objects.OCategorieMenu;
import com.creatio.imm.Objects.OMenu;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {
    private ExpandableListView lvMenu;
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private ArrayList<OCategorieMenu> data = new ArrayList<>();
    private TextView txtBadgeGifts;
    private TextView txtBadgeCupons;
    private TextView txtBadgemenu;
    private ImageButton btnClose;
    private RelativeLayout rlCar, rlCupons;
    private String countcar = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        lvMenu = findViewById(R.id.lvMenu);
        txtBadgemenu = findViewById(R.id.txtBadgemenu);
        txtBadgeCupons = findViewById(R.id.txtBadgeCupons);
        txtBadgeGifts = findViewById(R.id.txtBadgeGifts);
        btnClose = findViewById(R.id.btnClose);
        rlCupons = findViewById(R.id.rlCupons);
        rlCar = findViewById(R.id.rlCar);

        context = Menu.this;
        extras = getIntent().getExtras();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countcar.equalsIgnoreCase("0")) {
                    Intent intent = new Intent(context, OrderCar.class);
                    intent.putExtra("ID_business",extras.getString("ID_business", "0"));
                    startActivityForResult(intent,1);
                } else {
                    Toast.makeText(context, "Aún no tienes nada en tu platillo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rlCupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyCupons.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReadMenuByBusiness();
        GetBadgeCar();
        ReadCountCuponsUser();
    }

    public void ReadMenuByBusiness() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvMenu)
                .load(R.layout.item_skeleton)
                .show();

        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadMenuByBusiness")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID", extras.getString("ID_business", "0"))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("response", response.toString());
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String description = obj.optString("description");
                            String image = obj.optString("image");
                            String ID_business = obj.optString("ID_business");
                            JSONArray dataM = obj.optJSONArray("dataMenu");
                            ArrayList<OMenu> dataMenu = new ArrayList<>();

                            for (int j = 0; j < dataM.length(); j++) {
                                JSONObject objMenu = dataM.getJSONObject(j);
                                String IDm, namem, price, ID_categorie, ID_branch, imagem, descriptionm, is_offer;
                                IDm = objMenu.optString("ID");
                                namem = objMenu.optString("name");
                                price = objMenu.optString("price");
                                is_offer = objMenu.optString("is_offer");
                                if (price.equalsIgnoreCase("0")){
                                    is_offer = objMenu.optString("discount");
                                }

                                ID_categorie = objMenu.optString("ID_categorie");
                                ID_branch = objMenu.optString("ID_branch");
                                imagem = objMenu.optString("image");
                                descriptionm = objMenu.optString("description");

                                dataMenu.add(new OMenu(IDm, namem, price, ID_categorie, ID_branch, imagem, descriptionm, is_offer));
                            }
                            data.add(new OCategorieMenu(name, description, ID_business, image, dataMenu));


                        }
                        skeletonScreen.hide();
                        ADMenu adapter = new ADMenu(data, context);
                        lvMenu.setAdapter(adapter);
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

    public void GetBadgeCar() {
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "GetBadgeCar")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            countcar = obj.optString("count");
                            if (countcar.equalsIgnoreCase("0")) {
                                txtBadgemenu.setVisibility(View.INVISIBLE);
                            } else {
                                txtBadgemenu.setVisibility(View.VISIBLE);
                            }
                            txtBadgemenu.setText(countcar);
                        }
                    }

                } catch (JSONException e) {
                    Log.e("pedidos error", e.toString());
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
        if (resultCode == 199){
            finish();
        }
    }

    public void ReadCountCuponsUser() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCountCuponsUser")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("e", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String count_gifts = obj.optString("count_gift");
                            String count_cupons = obj.optString("count_cupons");

                            if (count_gifts.equalsIgnoreCase("0")) {
                                txtBadgeGifts.setVisibility(View.GONE);
                            } else {
                                txtBadgeGifts.setVisibility(View.VISIBLE);
                                txtBadgeGifts.setText(count_gifts);
                            }
                            if (count_cupons.equalsIgnoreCase("0")) {
                                txtBadgeCupons.setVisibility(View.GONE);
                            } else {
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
