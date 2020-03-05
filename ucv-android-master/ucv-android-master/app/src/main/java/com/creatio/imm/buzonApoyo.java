package com.creatio.imm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
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

public class buzonApoyo extends AppCompatActivity {
    private android.webkit.WebView webView;
    private String direccion;
    private String id;
    private String numTarjeta;
    private TextView txtRegrasar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzon);
        Intent intent = getIntent();
        id = intent.getStringExtra("ids");
        numTarjeta = intent.getStringExtra("numTarjeta");
        txtRegrasar = findViewById(R.id.txtRegresar2);
        txtRegrasar.setOnClickListener(onclickRegress);
        //load the page with cache
        urls();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            finish();
            Intent intent = new Intent(buzonApoyo.this, TarjetaMenuIniActivity.class);
            startActivity(intent);
            //super.onBackPressed();
        }
    }

    private View.OnClickListener onclickRegress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent i = new Intent(buzonApoyo.this,TarjetaMenuIniActivity.class);
            i.putExtra("ids",id);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }
    };

    public void urls(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.apiurl)+"GetUrls", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response", response);

                    JSONObject json = new JSONObject(response);
                    String url = json.getString("success");
                    String datos = json.getString("data");
                    JSONObject urls = new JSONObject(datos);
                    direccion = urls.getString("url");

                    webView= findViewById(R.id.notiBuzon);
                    webView.setWebViewClient(new WebViewClient());
                    direccion =direccion+"?id="+id;
                    Log.e("direccion",direccion);

                    webView.loadUrl(direccion);
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);

                } catch (JSONException e) {
                    Alerter.create(buzonApoyo.this)
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
                params.put("tipo", "buzon");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


}
