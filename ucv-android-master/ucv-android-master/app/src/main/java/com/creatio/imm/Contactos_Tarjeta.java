package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Contactos_Tarjeta extends AppCompatActivity {
    private TextView txtRegrasar2;
    private Button btnContinuar3;
    private TextView txtNumTarjeta;
    private TextView txtContacto1;
    private TextView txtNumContac1;
    private TextView txtContacto2;
    private TextView txtNumContac2;
    private TextView txtContacto3;
    private TextView txtNumContac3;
    Bundle datos;
    private String numTarjeta;
    private String ids;
    private Context context;
    private SharedPreferences prefs;
    private String accionBotones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos__tarjeta);
        datos=getIntent().getExtras();

        context = Contactos_Tarjeta.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ids = prefs.getString("ID", "0");
        numTarjeta = prefs.getString("numTarjeta", "0");
        accionBotones = datos.getString("accionBotones","0");
        txtRegrasar2 = findViewById(R.id.txtRegresar2);
        txtContacto1 = findViewById(R.id.txtContacto1);
        txtNumContac1 = findViewById(R.id.txtNumContac1);
        txtContacto2 = findViewById(R.id.txtContacto2);
        txtNumContac2 = findViewById(R.id.txtNumContac2);
        txtContacto3 = findViewById(R.id.txtContacto3);
        txtNumContac3 = findViewById(R.id.txtNumContac3);
        txtRegrasar2 = findViewById(R.id.txtRegresar2);
        btnContinuar3 = findViewById(R.id.btnContinuar3);
        btnContinuar3.setOnClickListener(onclickContinua2);
        txtRegrasar2.setOnClickListener(onclickRegress2);
        buscarContactos();
    }

    private View.OnClickListener onclickContinua2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validarCampos()){
                StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.apitarupdurl) , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Mostrando el mensaje de la respuesta
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.e("response", response);
                            String success = json.getString("success");
                            if (success.equalsIgnoreCase("true")) {
                                if (accionBotones != null && accionBotones.equals("1")){
                                    final Intent i = new Intent(Contactos_Tarjeta.this,TarjetaMenuIniActivity.class);
                                    i.putExtra("ids",ids);
                                    i.putExtra("numTarjeta",numTarjeta);
                                    startActivity(i);
                                }else {
                                    final Intent i = new Intent(Contactos_Tarjeta.this,TarjetaParticipa.class);
                                    i.putExtra("ids",ids);
                                    i.putExtra("numTarjeta",numTarjeta);
                                    startActivity(i);
                                }

                            } else {
                                Alerter.create(Contactos_Tarjeta.this)
                                        .setTitle("Oh oh")
                                        .setText("No existe información con los datos proporcionados..")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }
                        } catch (JSONException e) {
                            Alerter.create(Contactos_Tarjeta.this)
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
                        //Descartar el diálogo de progreso
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
                        //Creación de parámetros
                        Map<String, String> params = new HashMap<String, String>();
                        //Agregando de parámetros
                        params.put("apikey", getResources().getString(R.string.apitarupdurl));
                        params.put("id", ids);
                        params.put("numTarjeta", numTarjeta);
                        JSONObject postData1 = new JSONObject();
                        try {
                            postData1.put("contacto", 1);
                            postData1.put("nombre", txtContacto1.getText().toString().trim());
                            postData1.put("celular", txtNumContac1.getText().toString().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject postData2 = new JSONObject();
                        try {
                            postData2.put("contacto", 2);
                            postData2.put("nombre", txtContacto2.getText().toString().trim());
                            postData2.put("celular", txtNumContac2.getText().toString().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject postData3 = new JSONObject();
                        try {
                            postData3.put("contacto", 3);
                            postData3.put("nombre", txtContacto3.getText().toString().trim());
                            postData3.put("celular", txtNumContac3.getText().toString().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONArray contaArray = new JSONArray();

                        contaArray.put(postData1);
                        contaArray.put(postData2);
                        contaArray.put(postData3);

                        JSONObject contactosCliente = new JSONObject();
                        try {
                            contactosCliente.put("contactos",contaArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params.put("contactos", contactosCliente.toString());
                        Log.e("data", String.valueOf(params));
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }else{
                Alerter.create(Contactos_Tarjeta.this)
                        .setText("Favor de ingresar los datos requeridos en los campos,\n"+
                                "El numero de celular tiene que ser de 10 digitos.")
                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                        .show();
            }
        }
    };

    private View.OnClickListener onclickRegress2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (accionBotones != null && accionBotones.equals("1")){
                final Intent i = new Intent(Contactos_Tarjeta.this,TarjetaMenuIniActivity.class);
                i.putExtra("ids",ids);
                i.putExtra("numTarjeta",numTarjeta);
                startActivity(i);
            }else {
                final Intent i = new Intent(Contactos_Tarjeta.this,EditPerfilTarjeta.class);
                i.putExtra("ids",ids);
                i.putExtra("numTarjeta",numTarjeta);
                startActivity(i);
            }
        }
    };

    private void buscarContactos(){
        if (!numTarjeta.trim().isEmpty()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.apitarurl), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.e("response", response);
                    String success = json.getString("success");
                    if (success.equalsIgnoreCase("true")) {
                        String datos = json.getString("contacts");
                        Log.e("data", datos);
                        JSONArray arr = json.getJSONArray("contacts");
                        Log.e("arr", String.valueOf(arr));
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("id");
                            if ( i == 0 ){
                                txtContacto1.setText(obj.optString("fullname"));
                                txtNumContac1.setText(obj.optString("cellphone"));
                            }else if (i == 1){
                                txtContacto2.setText(obj.optString("fullname"));
                                txtNumContac2.setText(obj.optString("cellphone"));
                            }else if (i == 2){
                                txtContacto3.setText(obj.optString("fullname"));
                                txtNumContac3.setText(obj.optString("cellphone"));
                            }
                        }
                    } else {
                        Alerter.create(Contactos_Tarjeta.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados..")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                } catch (JSONException e) {
                    Alerter.create(Contactos_Tarjeta.this)
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
                params.put("id", numTarjeta);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        }else {
            Alerter.create(Contactos_Tarjeta.this)
            .setText("No existe información con los datos proporcionados.")
            .setBackgroundColorInt(getResources().getColor(R.color.red))
            .show();
        }
    }

    private boolean validarCampos(){
        boolean validar = true;
        if (!txtContacto1.getText().toString().trim().isEmpty() ||
                !txtNumContac1.getText().toString().trim().isEmpty()) {
            Log.e("xtx1",txtContacto1.getText().toString().trim());
            Log.e("numtxt",txtNumContac1.getText().toString().trim());
            String r = txtNumContac1.getText().toString().trim();
            Log.e("tama", String.valueOf(r.length()));
            if (txtContacto1.getText().toString().trim().isEmpty()){
                validar = false;
            }else if (txtNumContac1.getText().toString().trim().isEmpty()){
                validar = false;
            }else if (txtNumContac1.getText().toString().trim().length() != 10){
                validar = false;
            }
        }

        if (!txtContacto2.getText().toString().trim().isEmpty() ||
                !txtNumContac2.getText().toString().trim().isEmpty()) {
            if (txtContacto2.getText().toString().trim().isEmpty()){
                validar = false;
            }else if (txtContacto2.getText().toString().trim().isEmpty()){
                validar = false;
            }else if (txtNumContac2.getText().toString().trim().length() != 10){
                validar = false;
            }
        }
        if (!txtContacto3.getText().toString().trim().isEmpty() ||
                !txtNumContac3.getText().toString().trim().isEmpty()) {
            if (txtContacto3.getText().toString().trim().isEmpty()){
                validar = false;
            }else if (txtContacto3.getText().toString().trim().isEmpty()){
                validar = false;
            }else if (txtNumContac3.getText().toString().trim().length() != 10){
                validar = false;
            }
        }

        return validar;
    }

    @Override
    public void onBackPressed() {
        if (accionBotones != null && accionBotones.equals("1")){
            final Intent i = new Intent(Contactos_Tarjeta.this,TarjetaMenuIniActivity.class);
            i.putExtra("ids",ids);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }else {
            final Intent i = new Intent(Contactos_Tarjeta.this,EditPerfilTarjeta.class);
            i.putExtra("ids",ids);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }
    }

}
