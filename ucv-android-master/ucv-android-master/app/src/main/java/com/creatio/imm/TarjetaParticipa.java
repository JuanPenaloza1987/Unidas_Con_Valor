package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TarjetaParticipa extends AppCompatActivity {
    private TextView txtRegrasar3;
    private SharedPreferences prefs;
    private Button btnContinuar4;
    Bundle datos;
    private String numTarjeta;
    private String ids;
    private Button btnParticipaOn;
    private Button btnParticipaOff;
    private Integer participaONOFF = -1;
    private Context context;
    private String accionBotones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = TarjetaParticipa.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        setContentView(R.layout.activity_tarjeta_participa);
        datos=getIntent().getExtras();
        ids = prefs.getString("ID", "0");
        numTarjeta = prefs.getString("numTarjeta", "0");
        accionBotones = datos.getString("accionBotones","0");
        txtRegrasar3 = findViewById(R.id.txtRegresa3);
        btnContinuar4 = findViewById(R.id.btnContinuar4);
        btnParticipaOn = findViewById(R.id.button7);
        btnParticipaOff = findViewById(R.id.button8);
        btnContinuar4.setOnClickListener(onclickContinua4);
        btnParticipaOn.setOnClickListener(OnclickParticipa);
        btnParticipaOff.setOnClickListener(onclicNoParticipa);
        txtRegrasar3.setOnClickListener(onclickRegress3);
    }

    private View.OnClickListener OnclickParticipa =  new View.OnClickListener(){
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            btnParticipaOff.setTextColor(Color.parseColor("#FFFFFF"));
            btnParticipaOff.setBackgroundResource( R.drawable.button_rounded_sf);
            btnParticipaOn.setTextColor(Color.parseColor("#000000"));
            btnParticipaOn.setBackgroundResource( R.drawable.button_rounded_full_white);
            //btnParticipaOn.setBackgroundColor(Color.WHITE);

            Alerter.create(TarjetaParticipa.this)
                    .setDuration(3000)
                    .setText("¡Recomendaciones! Participando en este programa te recomendamos "+
                            "no arriesgar tu integridad física, te sugerimos llamar al 911 "+
                            " para dar seguimiento y apoyo.")
                    .setBackgroundColorInt(getResources().getColor(R.color.yellow))
                    .show();
            participaONOFF = 1;
        }
    };

    private View.OnClickListener onclicNoParticipa =  new View.OnClickListener(){
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            participaONOFF = 0;
            btnParticipaOn.setTextColor(Color.parseColor("#FFFFFF"));
            btnParticipaOn.setBackgroundResource( R.drawable.button_rounded_sf);
            btnParticipaOff.setTextColor(Color.parseColor("#000000"));
            btnParticipaOff.setBackgroundResource( R.drawable.button_rounded_full_white);
            //btnParticipaOff.setBackgroundColor(Color.WHITE);
        }
    };

    @Override
    public void onBackPressed() {
        if (accionBotones != null && accionBotones.equals("1")){
            final Intent i = new Intent(TarjetaParticipa.this,TarjetaMenuIniActivity.class);
            i.putExtra("ids",ids);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }else {
            final Intent i = new Intent(TarjetaParticipa.this,Contactos_Tarjeta.class);
            i.putExtra("ids",ids);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }
    }

    private View.OnClickListener onclickContinua4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (participaONOFF == 0 || participaONOFF ==1){
                StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.apitarupdparturl), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.e("response", response);
                            String success = json.getString("success");
                            if (success.equalsIgnoreCase("true")) {
                                finish();
                                Intent intent = new Intent(TarjetaParticipa.this, TarjetaMenuIniActivity.class);
                                intent.putExtra("ids",ids);
                                intent.putExtra("numTarjeta",numTarjeta);

                                startActivity(intent);
                                Alerter.create(TarjetaParticipa.this)
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
                                Alerter.create(TarjetaParticipa.this)
                                        .setTitle("Oh oh")
                                        .setText("No existe información con los datos proporcionados..")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }
                        } catch (JSONException e) {
                            Alerter.create(TarjetaParticipa.this)
                                    .setTitle("Oh oh")
                                    .setText("No existe información con los datos proporcionados...")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Anything you want
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("apikey", getResources().getString(R.string.apikey));
                        params.put("id", ids);
                        params.put("numTarjeta", numTarjeta);
                        params.put("participa", participaONOFF.toString().trim());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
           }else {
                Alerter.create(TarjetaParticipa.this)
                        .setText("Seleccione si desea participar o no antes de continuar.")
                        .setBackgroundColorInt(getResources().getColor(R.color.yellow))
                        .show();
            }
        }
    };

    private View.OnClickListener onclickRegress3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (accionBotones != null && accionBotones.equals("1")){
                final Intent i = new Intent(TarjetaParticipa.this,TarjetaMenuIniActivity.class);
                i.putExtra("ids",ids);
                i.putExtra("numTarjeta",numTarjeta);
                startActivity(i);
            }else {
                final Intent i = new Intent(  TarjetaParticipa.this,Contactos_Tarjeta.class);
                i.putExtra("ids",ids);
                i.putExtra("numTarjeta",numTarjeta);
                startActivity(i);
            }
        }
    };
}
