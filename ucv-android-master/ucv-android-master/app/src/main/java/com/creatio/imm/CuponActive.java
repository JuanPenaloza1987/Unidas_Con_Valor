package com.creatio.imm;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.Objects.OCupons;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CuponActive extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private ImageView img;
    private String ID_cupon;
    private String ID_r,codebar;
    private TextView txtNegInfo, txtDate, txtName, txtPrice, txtCode;
    private ArrayList<OCupons> data = new ArrayList<>();
    private Button btnDetail, btnVincular, btnRate, btnMap, btnSeeCode;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupon_active);
        context = CuponActive.this;
        dialog = new ProgressDialog(context);
        dialog.setMessage("Cargando");
        dialog.show();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cupón activo");
        txtNegInfo = findViewById(R.id.txtBusiness);
        img = findViewById(R.id.img);
        txtName = findViewById(R.id.txtName);
        txtDate = findViewById(R.id.txtDate);
        txtPrice = findViewById(R.id.txtPrice);
        btnRate = findViewById(R.id.btnRate);
        txtCode = findViewById(R.id.txtCode);
        btnVincular = findViewById(R.id.btnVincular);
        btnDetail = findViewById(R.id.btnDetails);
        btnSeeCode = findViewById(R.id.btnSeeCode);
        btnMap = findViewById(R.id.btnMap);


        extras = getIntent().getExtras();
        ID_cupon = extras.getString("ID_cupon", "0");
        ID_r = extras.getString("ID_r", "0");
        txtNegInfo.setText(extras.getString("name", "No data"));
        GetItemsMenu();
        btnSeeCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CodeBar.class);
                intent.putExtra("code",codebar);
                startActivity(intent);
            }
        });
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.layout_alert_lottie_commits);
                // set the custom dialog components - text, image and button
                TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                final EditText edtCommit = dialog.findViewById(R.id.edtCommit);
                final RatingBar rtBar = dialog.findViewById(R.id.rtBar);
                rtBar.setRating(5);
                rtBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        // Toast.makeText(CuponActive.this, "Si", Toast.LENGTH_SHORT).show();
                    }
                });
                txtTitle.setText("Comentar");
                txtMsj.setText("Escribe debajo los comentarios que tienes para el negocio " + txtNegInfo.getText().toString());


                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

                btnCancelar.setVisibility(View.INVISIBLE);

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


                                        Alerter.create(CuponActive.this)
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
                                        Alerter.create(CuponActive.this)
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
                        dialog.dismiss();
                    }

                });
                btnCancelar.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapBranchs.class);
                intent.putExtra("ID_cupon", ID_cupon);
                startActivity(intent);
            }
        });
        if (!extras.getBoolean("is_special")){
            txtDate.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void GetItemsMenu() {

        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCuponByID")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID", extras.getString("ID_cupon", "0"))
                .addBodyParameter("ID_r", ID_r)
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    dialog.dismiss();
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            final String ID_business = obj.optString("ID_business");
                            String name = obj.optString("name");
                            String description = obj.optString("description");
                            String create_date = obj.optString("create_date");
                            String status = obj.optString("status");
                            String status_c = obj.optString("status_c");
                            String type = obj.optString("type");
                            String category = obj.optString("category");
                            String quantity = obj.optString("quantity");
                            final String image = obj.optString("image");
                            String price = obj.optString("price");
                            String discount = obj.optString("discount");
                            String date_finish = obj.optString("date_finish");
                            String isFavorite = obj.optString("isFavorite");
                            String isReserved = obj.optString("isReserved");
                            String s_vencido = obj.optString("s_vencido");
                            String is_cuponcode = obj.optString("is_cuponcode", "0");
                            String code = obj.optString("code", "0");
                            final String restrictions = obj.optString("restrictions");
                            if (status_c.equalsIgnoreCase("1")) {
                                btnVincular.setText("Canjeado");
                                btnVincular.setEnabled(false);
                                btnRate.setVisibility(View.VISIBLE);
                                btnVincular.setBackgroundResource(R.drawable.btn_disabled);
                            }
                            if (status_c.equalsIgnoreCase("0")) {
                                btnRate.setVisibility(View.GONE);
                            }
                            if (is_cuponcode.equalsIgnoreCase("1")) {
                                txtCode.setVisibility(View.VISIBLE);
                                btnVincular.setVisibility(View.GONE);
                                btnSeeCode.setVisibility(View.VISIBLE);
                            } else {
                                txtCode.setVisibility(View.GONE);
                                btnVincular.setVisibility(View.VISIBLE);
                                btnSeeCode.setVisibility(View.GONE);

                            }
                            RequestOptions options = new RequestOptions()
                                    .placeholder(R.drawable.no_image)
                                    .error(R.drawable.no_image)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .override(1000, 1000)
                                    .priority(Priority.HIGH);
                            Glide.with(context)
                                    .load(image)
                                    .apply(options)
                                    .into(img);
                            txtCode.setText("Código: " + code);
                            codebar = code;
                            if (status.equalsIgnoreCase("2")) {
                                btnVincular.setText("Cancelado");
                                btnVincular.setBackgroundResource(R.drawable.btn_red);
                            }
                            btnDetail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, ItemsMenu.class);
                                    intent.putExtra("ID_cupon", ID_cupon);
                                    intent.putExtra("image", image);
                                    context.startActivity(intent);
                                }
                            });
                            btnVincular.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.layout_alert_lottie);
                                    // set the custom dialog components - text, image and button
                                    TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                                    TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                                    txtTitle.setText("Atención");
                                    txtMsj.setText("Para canjear tu cupón debes estar en el establecimiento del negocio.");


                                    Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                                    Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

                                    btnCancelar.setVisibility(View.INVISIBLE);

                                    // if button is clicked, close the custom dialog
                                    btnAceptar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Dexter.withActivity(CuponActive.this)
                                                    .withPermissions(
                                                            Manifest.permission.CAMERA,
                                                            Manifest.permission.READ_CONTACTS,
                                                            Manifest.permission.RECORD_AUDIO
                                                    ).withListener(new MultiplePermissionsListener() {

                                                @Override
                                                public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                                                    Intent intent = new Intent(context, QRLector.class);
                                                    intent.putExtra("ID_cupon", ID_cupon);
                                                    intent.putExtra("ID_r", ID_r);
                                                    startActivityForResult(intent, 300);
                                                }

                                                @Override
                                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                                            }).check();
                                            dialog.dismiss();
                                        }
                                    });
                                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();


                                }
                            });
                            txtName.setText(name);
                            txtPrice.setText(HelperClass.formatDecimal(Double.parseDouble(price)));
                            if (price.equalsIgnoreCase("0")) {
                                txtPrice.setText(discount + " %");
                            } else {
                                txtPrice.setText(HelperClass.formatDecimal(Double.parseDouble(price)));
                            }
                            try {
                                String myFormat = "EEE dd MMM"; //In which you need put here
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.setTime(sdf.parse(date_finish));
                                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat, new Locale("es", "MX"));
                                txtDate.setText("Válido hasta " + sdf2.format(c.getTime()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                txtDate.setText("");
                            }


                            txtNegInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, InformationBusiness.class);
                                    intent.putExtra("ID_business", ID_business);
                                    startActivity(intent);
                                }
                            });

                            data.add(new OCupons(ID, name, description, create_date, status, quantity, type, category, image, price, discount, isFavorite, isReserved, s_vencido));


                        }

                    } else {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            btnRate.callOnClick();
            GetItemsMenu();
        }
    }
}
