package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupMenu;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADCitysAU;
import com.creatio.imm.Adapters.ADColsAU;
import com.creatio.imm.Objects.OCitys;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class Colonies extends AppCompatActivity {
    private ArrayList<OCitys> data_citys = new ArrayList<>();
    private ArrayList<OCitys> data_cols = new ArrayList<>();
    private AutoCompleteTextView edtCity,edtCol;
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private String id_city = "";
    private String id_colony = "";
    private Button btnCity;
    private CircularProgressButton btnContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colonies);
        context = Colonies.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        btnContinue = (CircularProgressButton) findViewById(R.id.btnContinue);
        edtCity = (AutoCompleteTextView) findViewById(R.id.edtCity);
        btnCity = findViewById(R.id.btnCity);
        edtCity.setThreshold(2);
        edtCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                for(OCitys d : data_citys){
                    String name = d.getCity() + ", " + d.getState();
                    if(d.getCity() != null && name.contains(item.toString())){
                        id_city = d.getID();
                    }
                    //something here
                }
            }
        });
        edtCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                for(OCitys d : data_citys){
                    String name = d.getCity() + ", " + d.getState();
                    if(d.getCity() != null && name.contains(item.toString())){
                        id_city = d.getID();
                    }
                    //sompoething here
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu pop = new PopupMenu(context, btnCity);
                for (int i = 0; i < data_citys.size(); i++) {
                    pop.getMenu().add(0, Integer.parseInt(data_citys.get(i).getID()), i, data_citys.get(i).getCity());
                }

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        id_city = data_citys.get(item.getOrder()).getID();
                        btnCity.setText(item.getTitle().toString());
                        return true;
                    }
                });
                pop.show();
            }
        });
        edtCol = (AutoCompleteTextView) findViewById(R.id.edtCol);
        edtCol.setThreshold(2);
        edtCol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OCitys fruit = (OCitys) parent.getItemAtPosition(position);
                edtCol.setText(fruit.getCity());
                id_colony = fruit.getID();
                //Toast.makeText(context, "id0 " + id_colony + "," + id_city, Toast.LENGTH_SHORT).show();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save
                btnContinue.startAnimation();
                AndroidNetworking.post(getResources().getString(R.string.apiurl) + "UpdateDataUserPremium")
                        .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                        .addBodyParameter("ID_user", prefs.getString("ID", "0"))

                        .addBodyParameter("id_city", id_city)
                        .addBodyParameter("id_colony", id_colony)

                        .setPriority(Priority.IMMEDIATE)
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("res", response.toString());
                            String success = response.getString("success");
                            if (success.equalsIgnoreCase("true")) {

                                Alerter.create(Colonies.this)
                                        .setTitle("Hecho ")
                                        .setText("Los datos enviados se han actualizado correctamente.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.green))
                                        .setOnHideListener(new OnHideAlertListener() {
                                            @Override
                                            public void onHide() {
                                                finish();
                                                Intent intent = new Intent(context,MainActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();

                            } else {
                                btnContinue.stopAnimation();
                                Alerter.create(Colonies.this)
                                        .setTitle("Error")
                                        .setText("Los datos enviados no son los correctos.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }
                        } catch (JSONException e) {

                            btnContinue.stopAnimation();
                            Alerter.create(Colonies.this)
                                    .setTitle("Error")
                                    .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        btnContinue.stopAnimation();
                        Alerter.create(Colonies.this)
                                .setTitle("Error")
                                .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                });

            }
        });
        GetCitys();
        GetCols();
    }

    public void GetCitys() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCitysUserP")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("info usetr", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("id");
                            String city = obj.optString("municipio");
                            String state = obj.optString("estado");
                            data_citys.add(new OCitys(ID, city, state));
                        }

                        final ADCitysAU adapter = new ADCitysAU(context, R.layout.item_au, data_citys);
                        edtCity.setAdapter(adapter);
                        edtCity.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                System.out.println("Text [" + s + "]");

                                adapter.getFilter().filter(s.toString());

                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count,
                                                          int after) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
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
    public void GetCols() {
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadColsUser")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
           // @Override
            @Override
            public void onResponse(JSONObject response) {
                Log.e("info usetr", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String city = obj.optString("name");
                            String state = obj.optString("name");
                            data_cols.add(new OCitys(ID, city, state));
                        }

                        final ADColsAU adapter = new ADColsAU(context, R.layout.item_au, data_cols);
                        edtCol.setAdapter(adapter);
                        edtCol.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                System.out.println("Text [" + s + "]");

                                adapter.getFilter().filter(s.toString());

                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count,
                                                          int after) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
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
}
