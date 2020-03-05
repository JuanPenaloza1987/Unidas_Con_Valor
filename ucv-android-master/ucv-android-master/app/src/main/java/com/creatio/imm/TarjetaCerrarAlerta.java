package com.creatio.imm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TarjetaCerrarAlerta extends AppCompatActivity {

    private TextView txtCerrar;
    Bundle datos;
    private String numTarjeta;
    private String ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta_cerrar_alerta);
        datos=getIntent().getExtras();
        ids = datos.getString("ids");
        numTarjeta = datos.getString("numTarjeta");

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
                    i = new Intent(TarjetaCerrarAlerta.this, TarjetaMenuIniActivity.class);
                    i.putExtra("ids",ids);
                    i.putExtra("numTarjeta",numTarjeta);
                    break;
            }
            startActivity(i);
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        final Intent intent = new Intent(TarjetaCerrarAlerta.this, TarjetaMenuIniActivity.class);
        intent.putExtra("ids",ids);
        intent.putExtra("numTarjeta",numTarjeta);
        startActivity(intent);
    }
}
