package com.creatio.imm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TarjetaApoyo extends AppCompatActivity {

    private TextView txtCerrar;
    private Button btnCancelar;
    private Button btnVial;
    private Button btnLlamar;
    private Button btnMedico;
    private Button btnSigueme;
    Bundle datos;
    private String numTarjeta;
    private String ids;
    private SharedPreferences prefs;
    private Context context;
    private String hero []= {"Ironman","Capitan America","Hulk","Thor",
            "Black Widow","Ant man","Spider man","Goku","Gohan","Bulma","Bidel",
            "Picoro","maestro Rochi","Satan","Cell","Majin boo","Truns","Kayosama",
            "Bruja","Crillin","Gottens","Patrulla Roja","Asesinos","Carros","Casas"};

    private ArrayList aContactos = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta_apoyo);
        datos=getIntent().getExtras();
        context = TarjetaApoyo.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ids = prefs.getString("ID", "2");
        numTarjeta = prefs.getString("numTarjeta", "987654321");

        txtCerrar = findViewById(R.id.txtCerrar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnVial = findViewById(R.id.btnVial);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnMedico = findViewById(R.id.btnMedico);
        btnSigueme = findViewById(R.id.btnSigueme);

        txtCerrar.setOnClickListener(onClikCerrar);
        btnCancelar.setOnClickListener(onClikCerrar);
        btnVial.setOnClickListener(onClikLlamar);
        btnLlamar.setOnClickListener(onClikLlamar);
        btnMedico.setOnClickListener(onClikLlamar);
        btnSigueme.setOnClickListener(onClikCerrar);
    }

    private View.OnClickListener onClikCerrar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            Intent i = new Intent();
            switch (v.getId()) {
                case R.id.txtCerrar:
                    i = new Intent(TarjetaApoyo.this, TarjetaMenuIniActivity.class);
                    break;
                case R.id.btnCancelar:
                    i = new Intent(TarjetaApoyo.this, TarjetaMenuIniActivity.class);
                    break;
                case R.id.btnSigueme:
                    i = new Intent(TarjetaApoyo.this, TarjetaAlerta.class);
                    i.putExtra("tipo","sigueme");
                    break;
            }
            i.putExtra("ids",ids);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }
    };

    private View.OnClickListener onClikLlamar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           // Intent i = new Intent(Intent.ACTION_DIAL);
            switch (v.getId()) {
                case R.id.btnVial:
                    Log.e("numTarjeta",numTarjeta);
                    Log.e("aCon1",String.valueOf(aContactos));
                    aContactos.clear();
                    Log.e("aCon2",String.valueOf(aContactos));
                    //mostrar();
                     buscarContactos();
                    Log.e("aCon3",String.valueOf(aContactos));
                   final Dialog dialog = new Dialog(TarjetaApoyo.this);
                    dialog.setContentView(R.layout.elemento_modal);
                    // set the custom dialog components - text, image and button
                    TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                    txtTitle.setText("Selecciona al menos un contacto.");
                    ListView milista = findViewById(R.id.listContactos);
                    for (int y=0; y<11; y++ ){
                        aContactos.add("#" +y +" Nombre");
                    }
                    Log.e("aCon3",String.valueOf(aContactos));
                    ArrayAdapter adapter = new ArrayAdapter(TarjetaApoyo.this,
                            R.layout.layout_ele_contactos,aContactos);
                    milista.setAdapter(adapter);
                    Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                    Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

                    btnAceptar.setText("Aceptar");
                    btnCancelar.setText("Cancelar");
                    // if button is clicked, close the custom dialog
                    btnAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, ?> allEntries = prefs.getAll();
                            SharedPreferences.Editor edit = prefs.edit();
                            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                                edit.remove(entry.getKey());
                                edit.putBoolean("login", false);
                            }
                            edit.apply();
                            Intent i = new Intent(context, TarjetaUnidos.class);
                            startActivity(i);
                            finish();
                        }
                    });
                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    /*i = new Intent(TarjetaApoyo.this, TarjetaAlerta.class);
                    i.putExtra("ids",ids);
                    i.putExtra("numTarjeta",numTarjeta);
                    i.putExtra("tipo","vial");
                    startActivity(i);*/
                    break;
                case R.id.btnLlamar:
                  /*  i = new Intent(TarjetaApoyo.this, TarjetaAlerta.class);
                    i.putExtra("ids",ids);
                    i.putExtra("numTarjeta",numTarjeta);
                    i.putExtra("tipo","llamar");
                    startActivity(i);*/
                    break;
                case R.id.btnMedico:
                   /* i = new Intent(TarjetaApoyo.this, TarjetaAlerta.class);
                    i.putExtra("ids",ids);
                    i.putExtra("numTarjeta",numTarjeta);
                    i.putExtra("tipo","medico");
                    startActivity(i);*/
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }
           // startActivity(i);
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        final Intent i = new Intent(TarjetaApoyo.this, TarjetaMenuIniActivity.class);
        i.putExtra("ids",ids);
        i.putExtra("numTarjeta",numTarjeta);
        startActivity(i);
        //super.onBackPressed();
    }
    private void buscarContactos(){
        numTarjeta = "987654321";
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
                                aContactos .add(obj.optString("fullname") + " "+ obj.optString("cellphone"));
                            }
                        } else {
                            Alerter.create(TarjetaApoyo.this)
                                    .setTitle("Oh oh")
                                    .setText("No existe información con los datos proporcionados..")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                        }
                    } catch (JSONException e) {
                        Alerter.create(TarjetaApoyo.this)
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
            Alerter.create(TarjetaApoyo.this)
                    .setText("No existe información con los datos proporcionados.")
                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                    .show();
        }
    }

    public void mostrar(){
        for (int y=0; y<11; y++ ){
            aContactos.add("#" +y +" Nombre");
        }
        ArrayList a = aContactos;
        Log.e("aCon3",String.valueOf(aContactos));
        final ArrayList selectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(TarjetaApoyo.this);
        // Set the dialog title
        builder.setTitle("Seleciona un Contacto.")
                .setCancelable(false)
                // Specify the list array, the items to be selected by default (null for none),
                //R.array.listado and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.listado, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(which);
                                } else if (selectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                   }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //return builder.create();
    }
}
