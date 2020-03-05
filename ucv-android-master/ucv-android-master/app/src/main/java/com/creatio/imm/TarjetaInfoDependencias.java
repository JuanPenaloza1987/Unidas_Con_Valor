package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import java.util.HashMap;
import java.util.Map;

public class TarjetaInfoDependencias extends AppCompatActivity {

    private WebView webView;
    private TextView txtRegrasar;
    private SharedPreferences prefs;
    private Context context;
    private String numTarjeta;
    private String ids;
    private String direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = TarjetaInfoDependencias.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        setContentView(R.layout.activity_tarjeta_info_dependencias);
        txtRegrasar = findViewById(R.id.txtRegresar2);
        txtRegrasar.setOnClickListener(onclickRegress);
        ids = prefs.getString("ID", "0");
        numTarjeta = prefs.getString("numTarjeta", "0");
        urls();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            finish();
            Intent intent = new Intent(TarjetaInfoDependencias.this, TarjetaMenuIniActivity.class);
            startActivity(intent);
        }
    }

    public void urls(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.apiurl)+"GetUrls", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.e("response", response);
                    String url = json.getString("success");
                    String datos = json.getString("data");
                    JSONObject urls = new JSONObject(datos);
                    direccion = urls.getString("url");
                    Log.e("dependencias", String.valueOf(direccion));

                    webView= findViewById(R.id.notiDependecias);
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(direccion);
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);

                } catch (JSONException e) {
                    Alerter.create(TarjetaInfoDependencias.this)
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
                params.put("tipo", "instancias");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private View.OnClickListener onclickRegress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent i = new Intent(TarjetaInfoDependencias.this,TarjetaMenuIniActivity.class);
            i.putExtra("ids",ids);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }
    };
}
