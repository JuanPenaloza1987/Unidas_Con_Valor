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
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADLineOrder;
import com.creatio.imm.Objects.OCar;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailOrder extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private ListView lvOrder;
    private ArrayList<OCar> data = new ArrayList<>();
    private ImageButton btnClose;
    private String ID_order = "";
    private String has_reservattion = "";
    private String reservation = "";
    private TextView txtTotal, txtPaymen, txtdataRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        context = DetailOrder.this;
        extras = getIntent().getExtras();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        btnClose = findViewById(R.id.btnClose);
        lvOrder = findViewById(R.id.lvOrder);
        txtTotal = findViewById(R.id.txtTotal);
        txtPaymen = findViewById(R.id.txtPayment);
        txtdataRes = findViewById(R.id.txtdataRes);
        has_reservattion = extras.getString("has_reservation", "0");
        reservation = extras.getString("reservation", "No data");
        if (has_reservattion.equalsIgnoreCase("1")){
            txtdataRes.setText(reservation);
        }else{
            txtdataRes.setText("Sin reservación");
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetCar();
    }

    public void GetCar() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvOrder)
                .load(R.layout.item_skeleton)
                .show();
        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "GetOrder")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .addBodyParameter("ID_order", extras.getString("ID_order", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("e", response.toString());
                try {
                    String success = response.getString("success");
                    String payment = response.getString("payment");
                    if (payment.equalsIgnoreCase("2")){
                        txtPaymen.setText("Pago en el lugar");
                    }else{
                        txtPaymen.setText("Pago con tarjeta");
                    }
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        double totalgr = 0.0;
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject object = arr.getJSONObject(i);
                            String ID = object.optString("ID_line");
                            String ID_menu = object.optString("ID_menu");
                            String ID_cupon = object.optString("ID_cupon");
                            String name_menu = object.optString("name");
                            String price_menu = object.optString("price");
                            String quantity = object.optString("quantity");
                            String total = object.optString("total");
                            ID_order = object.optString("ID_order");
                            String image = object.optString("image");
                            totalgr = totalgr + (Double.parseDouble(price_menu) * Double.parseDouble(quantity));
                            data.add(new OCar(ID, ID_menu, name_menu, price_menu, quantity, total, ID_order, image, ID_cupon));
                        }
                        skeletonScreen.hide();
                        txtTotal.setText("Total: " + HelperClass.formatDecimal(totalgr));
                        ADLineOrder adapter = new ADLineOrder(context, data);
                        lvOrder.setAdapter(adapter);

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
