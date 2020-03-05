package com.creatio.imm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import org.json.JSONException;
import org.json.JSONObject;

public class QRLector extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private QRLector activity;
    private String ID_cupon;
    private String ID_business;
    private String ID_r;
    private String ID_branch;
    private String contact_validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlector);
        activity = QRLector.this;
        context = QRLector.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        extras = getIntent().getExtras();
        ID_cupon = extras.getString("ID_cupon", "0");
        ID_r = extras.getString("ID_r", "0");
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] arr = result.getText().split("-");

                        ID_business = arr[6];
                        ID_branch = arr[4];
                        contact_validate = arr[2];
                        ValidateCupon();


                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(300);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void ValidateCupon() {
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ValidateCupon")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_cupon", ID_cupon)
                .addBodyParameter("ID_r", ID_r)
                .addBodyParameter("ID_business", ID_business)
                .addBodyParameter("contact_validate", contact_validate)
                .addBodyParameter("ID_branch", ID_branch)
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("r", response.toString());
                try {
                    String success = response.getString("success");
                    String message = response.getString("message");
                    if (success.equalsIgnoreCase("true")) {


                        if (message.contains("New nivel")) {
                            Intent intent = new Intent(context, MainActivity.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder b = new NotificationCompat.Builder(context);

                            b.setAutoCancel(true)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.drawable.ic_stat_name)
                                    .setTicker("Nuevo nivel")
                                    .setContentTitle("Has subido de nivel")
                                    .setContentText("Has subido de nivel")
                                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                    .setContentIntent(contentIntent)
                                    .setContentInfo("Nivel nuevo");


                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(1, b.build());
                        }
                        Alerter.create(QRLector.this)
                                .setTitle("Felicidades")
                                .setText("Cupón vinculado corectamente")
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
                        if (message.contains("Branch invalid")) {
                            Alerter.create(QRLector.this)
                                    .setTitle("Sucursal inválida")
                                    .setText("El cupón no es aceptado por la sucursal donde estás.")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                        } else {
                            Alerter.create(QRLector.this)
                                    .setTitle("Error.")
                                    .setText("El cupón no es válido.")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                        }

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
}
