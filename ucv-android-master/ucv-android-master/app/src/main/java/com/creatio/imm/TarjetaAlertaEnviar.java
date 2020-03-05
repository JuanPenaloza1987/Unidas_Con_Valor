package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TarjetaAlertaEnviar extends AppCompatActivity {

    private TextView txtCerrar;
    Bundle datos;
    private String numTarjeta;
    private String ids;
    private SharedPreferences prefs;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta_alerta_enviar);
        datos=getIntent().getExtras();
        context = TarjetaAlertaEnviar.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ids = prefs.getString("ID", "0");
        numTarjeta = prefs.getString("numTarjeta", "0");
        txtCerrar = findViewById(R.id.txtCerrar);

        txtCerrar.setOnClickListener(onClikCerrar);
    }

    private View.OnClickListener onClikCerrar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            Intent i = new Intent();
            switch (v.getId()) {
                case R.id.txtCerrar:
                    i = new Intent(TarjetaAlertaEnviar.this, TarjetaMenuIniActivity.class);
                    break;
            }
            i.putExtra("ids",ids);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        final Intent intent = new Intent(TarjetaAlertaEnviar.this, TarjetaMenuIniActivity.class);
        intent.putExtra("ids",ids);
        intent.putExtra("numTarjeta",numTarjeta);
        startActivity(intent);
    }
}
