package com.creatio.imm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TarjetaAlerta extends AppCompatActivity implements LocationListener{

    protected LocationManager locationManager;
    private TextView txtCerrar;
    private Button btnCancelar;
    private CountDownTimer countDownTimer;
    private TextView textTimer;
    ProgressBar progressBarToday;
    int porcen = 1;
    Bundle datos;
    private String numTarjeta;
    private String ids;
    private SharedPreferences prefs;
    private Context context;
    private double latitud;
    private double longitud;
    private String direccion;
    private String tipo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta_alerta);
        datos=getIntent().getExtras();
        context = TarjetaAlerta.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ids = prefs.getString("ID", "0");
        numTarjeta = prefs.getString("numTarjeta", "0");
        Intent intent = getIntent();
        tipo = intent.getStringExtra("tipo");
        btnCancelar = findViewById(R.id.btnCancelar);
        txtCerrar = findViewById(R.id.txtCerrar);
        textTimer = findViewById(R.id.textTimer);
        progressBarToday = findViewById(R.id.progressBarToday);

        btnCancelar.setOnClickListener(onClikCerrar);
        txtCerrar.setOnClickListener(onClikCerrar);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this);

        }
        startTimer(5);
    }

    private View.OnClickListener onClikCerrar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCancelar:
                    countDownTimer.cancel();
                    countDownTimer.onFinish();
                    finish();
                    final Intent iCancelar = new Intent(TarjetaAlerta.this, TarjetaApoyo.class);
                    iCancelar.putExtra("ids",ids);
                    iCancelar.putExtra("numTarjeta",numTarjeta);
                    startActivity(iCancelar);
                    break;
                case R.id.txtCerrar:
                    countDownTimer.cancel();
                    countDownTimer.onFinish();
                    finish();
                    final Intent i = new Intent(TarjetaAlerta.this, TarjetaApoyo.class);
                    i.putExtra("ids",ids);
                    i.putExtra("numTarjeta",numTarjeta);
                    startActivity(i);
                    break;
            }
        }
    };

    private void startTimer(final int minuti) {
        countDownTimer = new CountDownTimer( minuti * 1000, 1000) {
            //countDownTimer = new CountDownTimer(60 * minuti * 1000, 500) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                int progresPorcen = 20;
                //progressBarToday.setProgress((int)seconds);
                progressBarToday.setProgress(progresPorcen*porcen);
                porcen++;
               // textTimer.setText(String.format("%02d", seconds/60) + ":" + String.format("%02d", seconds%60));
                textTimer.setText(String.format("%02d", seconds%60));
                // format the textview to show the easily readable format
            }
            @Override
            public void onFinish() {
                if(textTimer.getText().equals("00") || textTimer.getText().equals("01")){
                    textTimer.setText("00");
                    progressBarToday.setProgress(minuti *100);
                    locationStart();
                    envioAlerta();
                }
                else{
                    Alerter.create(TarjetaAlerta.this)
                            .setText("Algo salio mal, favor de volver a intentarlo.")
                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                            .show();
                }
            }
        }.start();

    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this);
    }

/*
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }*/

    private void envioAlerta(){
        //Mostrar el diálogo de progresotipo
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.apitarpointsurl) , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.e("responseEditProfile", response);
                    String success = json.getString("success");
                    if (success.equalsIgnoreCase("true")) {
                        finish();
                        final Intent i = new Intent(TarjetaAlerta.this, TarjetaAlertaEnviar.class);
                        i.putExtra("ids",ids);
                        i.putExtra("numTarjeta",numTarjeta);
                        startActivity(i);
                    } else {
                        Alerter.create(TarjetaAlerta.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados..")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                } catch (JSONException e) {
                    Alerter.create(TarjetaAlerta.this)
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
                //Creación de parámetros
                Map<String, String> params = new HashMap<String, String>();
                //Agregando de parámetros
                params.put("apikey", getResources().getString(R.string.apikey));
                params.put("id", ids);
                params.put("numTarjeta", numTarjeta);
                params.put("type", tipo);
                params.put("msg", "ayudame siguiendome");
                params.put("latitude", String.valueOf(latitud));
                params.put("longitud", String.valueOf(longitud));
                params.put("location", direccion);
                Log.e("longitud", String.valueOf(params));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    /**
     * Called when the location has changed.
     *
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
        // debido a la deteccion de un cambio de ubicacion
        location.getLatitude();
        location.getLongitude();
        String Text = "Mi ubicacion actual es: " + "\n Lat = "
                + location.getLatitude() + "\n Long = " + location.getLongitude();
        Log.e("latLong" ,Text );

        latitud = location.getLatitude();
        longitud = location.getLongitude();

        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion = String.valueOf(DirCalle.getAddressLine(0));
                    Log.e("direccion","Mi direccion es: \n"
                            + DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {@link LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {@link LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {@link LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {
        // Este metodo se ejecuta cuando el GPS es activado
    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {
        // Este metodo se ejecuta cuando el GPS es desactivado
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        countDownTimer.cancel();
        countDownTimer.onFinish();
        finish();
        final Intent iCancelar = new Intent(TarjetaAlerta.this, TarjetaApoyo.class);
        iCancelar.putExtra("ids",ids);
        iCancelar.putExtra("numTarjeta",numTarjeta);
        startActivity(iCancelar);
    }
}
