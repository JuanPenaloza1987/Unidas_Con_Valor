package com.creatio.imm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADBanner;
import com.creatio.imm.Objects.OBanner;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

public class TarjetaMenuIniActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageButton btnPanico;
    private ImageButton btnApoyo;
    private ImageButton btnFelizDiaMujer;
    private ImageButton btnDescuentos;
    private ImageButton btnInfoDependencias;
    private ImageButton btnTipsMujeres;
    private ImageButton btnConoceApp;
    private ImageButton btnMenu;
    private Button btnComenta;
    private SharedPreferences prefs;
    private Context context;
    Bundle datos;
    private String numTarjeta;
    private String ids;
    DrawerLayout drawer;
    ViewPager viewPager;
    private ArrayList<OBanner> data_last = new ArrayList<>();
    CircleIndicator indicator;
    ADBanner adapterBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = TarjetaMenuIniActivity.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ids = prefs.getString("ID", "2");
        numTarjeta = prefs.getString("numTarjeta", "987654321");
        setContentView(R.layout.activity_tarjeta_menu_ini);
        datos=getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        AndroidNetworking.initialize(getApplicationContext());

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
        navigationView.setNavigationItemSelectedListener(this);

        btnPanico = findViewById(R.id.btnPanico);
        btnApoyo = findViewById(R.id.btnApoyo);
        //btnFelizDiaMujer = findViewById(R.id.btnFelizDiaMujer);
        btnDescuentos = findViewById(R.id.btnDescuentos);
        btnInfoDependencias = findViewById(R.id.btnInfoDependencias);
        btnTipsMujeres = findViewById(R.id.btnTipsMujeres);
        btnConoceApp = findViewById(R.id.btnConoceApp);
        btnComenta = findViewById(R.id.btnComenta);
        btnMenu = findViewById(R.id.btnMenu);

        btnPanico.setOnClickListener(onclickAll);
        btnApoyo.setOnClickListener(onclickAll);
        //btnFelizDiaMujer.setOnClickListener(onclickAll);
        btnDescuentos.setOnClickListener(onclickAll);
        btnInfoDependencias.setOnClickListener(onclickAll);
        btnTipsMujeres.setOnClickListener(onclickAll);
        btnConoceApp.setOnClickListener(onclickAll);
        btnComenta.setOnClickListener(onclickAll);
        //btnMenu.setOnClickListener(onclickShowMenu);


        //LayoutInflater layoutInflater = LayoutInflater.from(context);
        //View headerView = layoutInflater.inflate(R.layout.app_bar_tarjeta_menu_ini, null);
        viewPager = findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tarjeta_menu_ini, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReadLastCupon();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void OpenMenuLeft() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
        } else {
            drawer.openDrawer(Gravity.RIGHT);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private View.OnClickListener onclickAll = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            Intent i = new Intent();
            switch (v.getId()) {
                case R.id.btnPanico:
                    i = new Intent(TarjetaMenuIniActivity.this, TarjetaCerrar.class);
                    break;
                case R.id.btnApoyo:
                    i = new Intent(TarjetaMenuIniActivity.this, TarjetaApoyo.class);
                    break;
                    /*
                case R.id.btnFelizDiaMujer:
                    i = new Intent(TarjetaMenuIniActivity.this, TarjetaInfoMujeres.class);
                    break;
                    */
                case R.id.btnDescuentos:
                    i = new Intent(TarjetaMenuIniActivity.this, MainActivity.class);
                    break;
                case R.id.btnInfoDependencias:
                    i = new Intent(TarjetaMenuIniActivity.this, TarjetaInfoDependencias.class);
                    break;
                case R.id.btnTipsMujeres:
                    i = new Intent(TarjetaMenuIniActivity.this, TarjetaTipsMujeres.class);
                    break;
                case R.id.btnConoceApp:
                    i = new Intent(TarjetaMenuIniActivity.this, TarjetaCerrar.class);
                    break;
                case R.id.btnComenta:
                    i = new Intent(TarjetaMenuIniActivity.this, activityComentarios.class);
                    break;
            }
            i.putExtra("ids",ids);
            i.putExtra("numTarjeta",numTarjeta);
            startActivity(i);
        }
    };

    public void ReadLastCupon() {
        data_last.clear();
        Log.e("line error", "read");
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadBannerTop")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("line error", response.toString());
                try {
                    Log.e("line error", response.toString());
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {
                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject object = arr.getJSONObject(i);
                            String ID = object.optString("ID");
                            String name = object.optString("name");
                            String image = object.optString("image");
                            String icon = object.optString("image_action");
                            String action = object.optString("action");
                            final String params = object.optString("params");
                            String type = object.optString("type");
                            String description = object.optString("description");
                            String button_text = object.optString("button_text");
                            String show_button = object.optString("show_button");
                            data_last.add(new OBanner(ID, name, image,icon,action,params,type,description,button_text,show_button));
                        }

                        adapterBanner = new ADBanner(context,data_last);
                        viewPager.setAdapter(adapterBanner);
                        adapterBanner.notifyDataSetChanged();
                        indicator.setViewPager(viewPager);

                    }
                } catch (JSONException e) {
                    Log.e("line error", e.getMessage() );
                    Toast.makeText(context, "Error catch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                viewPager.setVisibility(View.GONE);
            }
        });
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_ini:
                /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                } else {
                    drawer.openDrawer(Gravity.RIGHT);
                }*/
                drawer.closeDrawers();
                break;
            case R.id.nav_favs:
                Alerter.create(TarjetaMenuIniActivity.this)
                        .setTitle("Entro SubMenu")
                        .setText("Paso 2")
                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                        .show();
                Intent intent = new Intent(context, EditPerfilTarjeta2.class);
                intent.putExtra("ids", ids);
                intent.putExtra("numTarjeta", numTarjeta);
                intent.putExtra("init", "1");
                startActivity(intent);
                break;
            case R.id.nav_not:
                Intent intentCont = new Intent(context, Contactos_Tarjeta.class);
                intentCont.putExtra("ids", ids);
                intentCont.putExtra("numTarjeta", numTarjeta);
                intentCont.putExtra("accionBotones", "1");
                startActivity(intentCont);
                break;

            case R.id.nav_buzon:
                Intent intentbuzon = new Intent(context, buzonApoyo.class);
                intentbuzon.putExtra("ids", ids);
                intentbuzon.putExtra("numTarjeta", numTarjeta);
                startActivity(intentbuzon);
                break;
            case R.id.nav_trend:
                Intent intentPart = new Intent(context, TarjetaParticipa.class);
                intentPart.putExtra("ids", ids);
                intentPart.putExtra("numTarjeta", numTarjeta);
                intentPart.putExtra("accionBotones", "1");
                startActivity(intentPart);
                break;
            case R.id.nav_politica:
                //Toast.makeText(context, "Open po", Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(R.string.principalurl +"politica/"));
                startActivity(browserIntent);
                break;
            case R.id.nav_close:
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.layout_alert_lottie);
                // set the custom dialog components - text, image and button
                TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                txtTitle.setText("¿Estas seguro de cerrar sesión?");
                txtMsj.setText("Los datos no se perderán");

                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

                btnAceptar.setText("Si");
                btnCancelar.setText("No");
                // if button is clicked, close the custom dialog
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Map<String, ?> allEntries = prefs.getAll();
                        SharedPreferences.Editor edit = prefs.edit();
                        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                            edit.remove(entry.getKey());
                            edit.putBoolean("login", false);
                        }
                        edit.apply();
                        Intent i = new Intent(context, TarjetaUnidos.class);
                        startActivity(i);
                        finish();
                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            case R.id.nav_help:
                final Dialog dialog2 = new Dialog(context);
                dialog2.setContentView(R.layout.layout_alert_lottie_help);
                // set the custom dialog components - text, image and button
                TextView txtTitle2 = (TextView) dialog2.findViewById(R.id.txtTitle);
                TextView txtMsj2 = (TextView) dialog2.findViewById(R.id.txtMsj);
                TextView txtCall = (TextView) dialog2.findViewById(R.id.txtCall);
                final EditText edtCommit = dialog2.findViewById(R.id.edtCommit);

                txtTitle2.setText("Hola");
                txtMsj2.setText("Necesitas ayuda o quieres comentar, hazlo aqui o marcanos: ");

                txtCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = "+526144559224";
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                });
                Button btnAceptar2 = (Button) dialog2.findViewById(R.id.btnAceptar);
                btnAceptar2.setText("Comentar");
                Button btnCancelar2 = (Button) dialog2.findViewById(R.id.btnCancelar);

                // if button is clicked, close the custom dialog
                btnAceptar2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AndroidNetworking.post(context.getResources().getString(R.string.apiurl) + "InsertCommitApp")
                                .addBodyParameter("apikey", context.getResources().getString(R.string.apikey))
                                .addBodyParameter("commit", edtCommit.getText().toString())
                                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                                .build().getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String success = response.getString("success");
                                    if (success.equalsIgnoreCase("true")) {
                                        Alerter.create(TarjetaMenuIniActivity.this)
                                                .setTitle("¡Gracias!")
                                                .setText("Hemos recibido tus comentarios con satisfacción.")
                                                .setBackgroundColorInt(context.getResources().getColor(R.color.green))
                                                .setIcon(R.drawable.ic_done_white_48dp)
                                                .enableVibration(true)
                                                .enableSwipeToDismiss()
                                                .show();
                                        dialog2.dismiss();
                                    } else {
                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
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
                        dialog2.dismiss();
                    }
                });
                btnCancelar2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                dialog2.show();
                break;

        }
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        //drawer.closeDrawer(1);
        //drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawers();*/
        return true;
    }
}
