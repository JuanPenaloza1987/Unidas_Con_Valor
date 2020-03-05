package com.creatio.imm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

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

public class TarjetaInfoMujeres extends AppCompatActivity {
    private android.webkit.WebView webView;
    private String direccion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta_info_mujeres);
        //load the page with cache
        urls();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            finish();
            Intent intent = new Intent(TarjetaInfoMujeres.this, TarjetaMenuIniActivity.class);
            startActivity(intent);
            //super.onBackPressed();
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
                    Alerter.create(TarjetaInfoMujeres.this)
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
                params.put("tipo", "tips");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
