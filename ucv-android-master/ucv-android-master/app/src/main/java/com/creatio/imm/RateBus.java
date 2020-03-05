package com.creatio.imm;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RateBus extends AppCompatActivity {
    String ID_cupon = "";
    String name_bus = "";
    String ID_r = "";
    SharedPreferences prefs;
    Context context;
    Bundle extras;

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_bus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Califica</font>"));
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_close);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        context = RateBus.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        extras = getIntent().getExtras();
        ID_cupon = extras.getString("ID_cupon", "");
        ID_r = extras.getString("ID_r", "");
        name_bus = extras.getString("name_bus", "");

        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtMsj = (TextView) findViewById(R.id.txtMsj);
        final EditText edtCommit = findViewById(R.id.edtCommit);
        final RatingBar rtBar = findViewById(R.id.rtBar);
        rtBar.setRating(5);
        rtBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                // Toast.makeText(CuponActive.this, "Si", Toast.LENGTH_SHORT).show();
            }
        });
        txtTitle.setText("Comentar");
        txtMsj.setText("Califica cómo fue tu última visita a " + name_bus);


        Button btnAceptar = (Button) findViewById(R.id.btnAceptar);
        Button btnCancelar = (Button) findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidNetworking.post(getResources().getString(R.string.apiurl) + "InsertCommit")
                        .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                        .addBodyParameter("ID_cupon", ID_cupon)
                        .addBodyParameter("ID_r", ID_r)
                        .addBodyParameter("rate", String.valueOf(rtBar.getRating()))
                        .addBodyParameter("commit", edtCommit.getText().toString())
                        .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                        .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("r", response.toString());
                        try {
                            String success = response.getString("success");
                            if (success.equalsIgnoreCase("true")) {


                                Alerter.create(RateBus.this)
                                        .setTitle("Felicidades")
                                        .setText("Calificación guardada, ¡gracias!")
                                        .setBackgroundColorInt(getResources().getColor(R.color.green))
                                        .setOnHideListener(new OnHideAlertListener() {
                                            @Override
                                            public void onHide() {
                                                setResult(300);
                                                finish();
                                            }
                                        })
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                setResult(300);
                                                finish();
                                            }
                                        })
                                        .show();

                            } else {
                                Alerter.create(RateBus.this)
                                        .setTitle("Error.")
                                        .setText("No se ha guardado correctamente")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
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

        });
    }
}
