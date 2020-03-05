package com.creatio.imm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.Objects.OCupons;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ItemsMenu extends AppCompatActivity implements OnMapReadyCallback {
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private String ID_cupon;
    private CollapsingToolbarLayout coll;
    private GoogleMap mMap;
    private TextView txtQuantity, txtNegInfo, txtRestrictions, txtDescription, txtName, txtDate, txtPrice;
    private ArrayList<OCupons> data = new ArrayList<>();
    private LinearLayout lyGral;
    private Button btnUse;
    private boolean isFav = false;
    private String rate = "0";
    private String iscuponcode = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        coll = findViewById(R.id.toolbar_layout);
        context = ItemsMenu.this;
        txtQuantity = findViewById(R.id.txtQuantity);
        txtNegInfo = findViewById(R.id.txtNegInfo);
        txtRestrictions = findViewById(R.id.txtRestrictions);
        txtDescription = findViewById(R.id.txtDescription);
        txtName = findViewById(R.id.txtName);
        txtDate = findViewById(R.id.txtDateNeg);
        txtPrice = findViewById(R.id.txtPrice);
        lyGral = findViewById(R.id.lyGral);
        btnUse = findViewById(R.id.btnUse);
        extras = getIntent().getExtras();
        ID_cupon = extras.getString("ID_cupon", "0");
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_VIEW)) {
            Uri uri = intent.getData();
            String scheme = uri.getScheme();

            ID_cupon = uri.getQueryParameter("ID_cupon");

        }
        LayoutInflater myinflater = getLayoutInflater();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getBoolean("login", false)) {

        } else {
            Intent intr = new Intent(context, Login.class);
            startActivity(intr);
            finish();
        }
        final ImageView bgImage = findViewById(R.id.bgImage);
        getSupportActionBar().setTitle("");
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(1000, 1000)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(extras.getString("image", ""))
                .apply(options)
                .into(bgImage);
        bgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SliderImage.class);
                intent.putExtra("image", extras.getString("image", ""));
                startActivity(intent);

            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GetItemsMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(301);
        finish();
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(301);
    }

    public void GetItemsMenu() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lyGral)
                .load(R.layout.item_skeleton)
                .show();
        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCuponByID")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID", ID_cupon)
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("response", response.toString());
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            final String ID_business = obj.optString("ID_business");
                            String name = obj.optString("name");
                            iscuponcode = obj.optString("is_cuponcode");
                            String description = obj.optString("description");
                            String create_date = obj.optString("create_date");
                            String status = obj.optString("status");
                            String type = obj.optString("type");
                            String category = obj.optString("category");
                            String quantity = obj.optString("quantity");
                            String image = obj.optString("image");
                            String price = obj.optString("price");
                            String discount = obj.optString("discount");
                            String date_finish = obj.optString("date_finish");
                            String isFavorite = obj.optString("isFavorite");
                            String isReserved = obj.optString("isReserved");
                            String s_vencido = obj.optString("s_vencido");
                            if (isFavorite.equalsIgnoreCase("1")) {
                                isFav = true;
                            } else {
                                isFav = false;
                            }
                            invalidateOptionsMenu();
                            final String restrictions = obj.optString("restrictions");
                            txtName.setText(name);
                            txtPrice.setText(HelperClass.formatDecimal(Double.parseDouble(price)));
                            if (price.equalsIgnoreCase("0")) {
                                txtPrice.setText(discount + " %");
                            } else {
                                txtPrice.setText(HelperClass.formatDecimal(Double.parseDouble(price)));
                            }
                            txtDescription.setText(description);
                            txtQuantity.setText("Quedan " + quantity);
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
                            txtRestrictions.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HelperClass.ShowAlert(context, "Restricciones", restrictions, 0);
                                }
                            });
                            if (isReserved.equalsIgnoreCase("1")) {
                                btnUse.setText("Canjeado");
                                btnUse.setBackgroundResource(R.drawable.btn_disabled);
                                btnUse.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Toast.makeText(context, "Ya esta canjeado.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                btnUse.setText("Canjear");
                                btnUse.setBackgroundResource(R.drawable.btn_primary);
                                btnUse.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ReservedCupon(ID_cupon);
                                    }
                                });
                            }

                            txtNegInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, InformationBusiness.class);
                                    intent.putExtra("ID_business", ID_business);
                                    intent.putExtra("rate", rate);
                                    startActivity(intent);
                                }
                            });
                            final ImageView bgImage = findViewById(R.id.bgImage);
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
                                    .into(bgImage);
                            data.add(new OCupons(ID, name, description, create_date, status, quantity, type, category, image, price, discount, isFavorite, isReserved, s_vencido));
                            JSONArray arrBr = obj.optJSONArray("data_branchs");
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (int j = 0; j < arrBr.length(); j++) {
                                JSONObject objbr = arrBr.getJSONObject(j);
                                String latlng = objbr.optString("location");
                                String namebr = objbr.optString("name");
                                String name_business = objbr.optString("name_business");

                                String[] arrll = latlng.split(",");
                                LatLng sydney = new LatLng(Double.parseDouble(arrll[0]), Double.parseDouble(arrll[1]));
                                builder.include(sydney);
                                mMap.addMarker(new MarkerOptions().position(sydney).title(namebr).snippet(name_business));
                            }
                            int width = getResources().getDisplayMetrics().widthPixels;
                            int height = getResources().getDisplayMetrics().heightPixels;
                            int padding = (int) (width * 0.10);
                            LatLngBounds bounds = builder.build();
                            if (arrBr.length() > 1) {
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                                mMap.animateCamera(cu);
                            } else {
                                String[] arrll = arrBr.getJSONObject(0).optString("location").split(",");
                                LatLng sydney = new LatLng(Double.parseDouble(arrll[0]), Double.parseDouble(arrll[1]));
                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(sydney, 15);

                                mMap.animateCamera(cu);
                            }


                        }

                        skeletonScreen.hide();
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
            setResult(300);
            finish();
        }
    }

    public void ReservedCupon(final String ID_cupon) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_alert_lottie_more);
        // set the custom dialog components - text, image and button
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
        txtTitle.setText("¿Estás seguro que deseas canjear el cupón?");
        txtMsj.setText("Recuerda que debes estar en el negocio.");


        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
        Button btnApartar = (Button) dialog.findViewById(R.id.btnApartar);
        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

        btnAceptar.setText("Si, canjear");
        btnCancelar.setText("Cancelar");
        btnApartar.setText("Aún no, apártamelo");
        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(ItemsMenu.this)
                        .withPermissions(
                                android.Manifest.permission.CAMERA,
                                android.Manifest.permission.READ_CONTACTS,
                                android.Manifest.permission.RECORD_AUDIO
                        ).withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                        if (iscuponcode.equalsIgnoreCase("1")){
                            AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReservedCupon")
                                    .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                                    .addBodyParameter("ID_cupon", ID_cupon)
                                    .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                                    .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String success = response.getString("success");
                                        String message = response.getString("message");
                                        JSONArray arr = response.getJSONArray("data");
                                        for (int i = 0; i < arr.length(); i++) {
                                            JSONObject obj = arr.getJSONObject(i);
                                            String code = obj.optString("code");
                                            Intent intent = new Intent(context,CodeBar.class);
                                            intent.putExtra("code",code);
                                            startActivity(intent);
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(context, "Error catch", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(context, "Error HTTP", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Intent intent = new Intent(context, QRLector.class);
                            intent.putExtra("ID_cupon", ID_cupon);
                            intent.putExtra("ID_r", "0");
                            startActivityForResult(intent, 300);
                        }


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
            }
        });
        btnApartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReservedCupon")
                        .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                        .addBodyParameter("ID_cupon", ID_cupon)
                        .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                        .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success = response.getString("success");
                            String message = response.getString("message");
                            if (success.equalsIgnoreCase("true")) {

                                HelperClass.ShowAlertCongrats(context, ItemsMenu.this, "¡Felicidades!", "Tu cupón ha sido procesado correctamente,  y esta listo para ser canjeado", "Te quedan 3 días para canjearlo");


                            } else {
                                if (message.contains("finished")) {
                                    Alerter.create(ItemsMenu.this)
                                            .setTitle("Casi lo tienes.")
                                            .setText("El cupón ya esta agotado.")
                                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                                            .show();
                                } else {
                                    Alerter.create(ItemsMenu.this)
                                            .setTitle("Oh oh")
                                            .setText("El cupón ya esta validado.")
                                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                                            .show();
                                }

                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error catch", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "Error HTTP", Toast.LENGTH_SHORT).show();
                    }
                });

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isFav) {
            menu.findItem(R.id.fav_menu).setIcon(R.drawable.ic_fav_white);
        } else {
            menu.findItem(R.id.fav_menu).setIcon(R.drawable.ic_fav_unfill);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favs_menu, menu);
        if (isFav) {
            menu.findItem(R.id.fav_menu).setIcon(R.drawable.ic_fav_white);
        } else {
            menu.findItem(R.id.fav_menu).setIcon(R.drawable.ic_fav_unfill);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(301);
            finish();

        }
        if (id == R.id.share_menu) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/pain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "eCupon");
            shareIntent.putExtra("ID_cupon", ID_cupon);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Encontre un cupón bien padre, abrelo. "+R.string.principalurl +"/data.php?ID_cupon=" + ID_cupon);
            startActivity(Intent.createChooser(shareIntent, "eCupon"));
        }
        if (id == R.id.fav_menu) {
            AndroidNetworking.post(getResources().getString(R.string.apiurl) + "InsertFav")
                    .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                    .addBodyParameter("ID_cupon", ID_cupon)
                    .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                    .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String success = response.getString("success");
                        String message = response.getString("message");
                        if (success.equalsIgnoreCase("true")) {
                            if (message.contains("Insert")) {
                                isFav = true;
                                invalidateOptionsMenu();
                                Alerter.create(ItemsMenu.this)
                                        .setTitle("Hecho")
                                        .setIcon(R.drawable.ic_fav_white)
                                        .setText("El cupón se ha guardado en la lista de favoritos")
                                        .setBackgroundColorInt(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            } else {
                                isFav = false;
                                invalidateOptionsMenu();
                                Alerter.create(ItemsMenu.this)
                                        .setTitle("Hecho")
                                        .setIcon(R.drawable.ic_fav_white)
                                        .setText("El cupón se ha eliminado en la lista de favoritos")
                                        .setBackgroundColorInt(getResources().getColor(R.color.colorPrimary))
                                        .show();
                            }

                        } else {
                            if (message.contains("finished")) {
                                Alerter.create(ItemsMenu.this)
                                        .setTitle("Casi lo tienes.")
                                        .setText("El cupón ya esta agotado.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            } else {
                                Alerter.create(ItemsMenu.this)
                                        .setTitle("Oh oh")
                                        .setText("El cupón ya esta validado.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }

                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error catch", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(context, "Error HTTP", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return true;
    }
}
