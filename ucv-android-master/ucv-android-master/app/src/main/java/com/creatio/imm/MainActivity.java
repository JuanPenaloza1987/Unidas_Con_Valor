package com.creatio.imm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADCategoriesRight;
import com.creatio.imm.Adapters.ADUbications;
import com.creatio.imm.Objects.OCategoryCuponRight;
import com.creatio.imm.Objects.OUbications;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class MainActivity extends AppCompatActivity {
    private FragmentPagerItemAdapter adapter;
    private SharedPreferences prefs;
    private Context context;
    private ListView lvUbications;
    private ArrayList<OUbications> data_ubications = new ArrayList<>();
    private GridViewWithHeaderAndFooter grid;
    private ArrayList<OCategoryCuponRight> data = new ArrayList<>();
    private String ID_category = "0";
    private String city_selected = "0";
    private String token_fb = "0";
    private TextView txtRegrasar2;
    Bundle datos;
    private String ids;
    private String numTarjeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        datos=getIntent().getExtras();
        ids = datos.getString("ids");
        numTarjeta = datos.getString("numTarjeta");
        token_fb = FirebaseInstanceId.getInstance().getToken();
        txtRegrasar2 = findViewById(R.id.txtRegresar2);
        txtRegrasar2.setOnClickListener(onclickRegress2);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        AndroidNetworking.initialize(getApplicationContext());

        ImageButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(MainActivity.this)
                        .withPermissions(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ).withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */
                        Intent intent = new Intent(context, MapsActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();

            }
        });
        //fab.setVisibility(View.GONE);

        lvUbications = findViewById(R.id.lvUbications);
        grid = findViewById(R.id.grid);
        Button btnGo = findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Filters.class);
                intent.putExtra("city", city_selected);
                intent.putExtra("category", ID_category);
                startActivity(intent);
            }
        });

        FragmentPagerItems viewPagers = FragmentPagerItems.with(context).add(getResources().getString(R.string.categories), FRCategories.class).create();
        adapter = new FragmentPagerItemAdapter( getSupportFragmentManager(), viewPagers);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        //viewPagers.add(FragmentPagerItem.of(getResources().getString(R.string.business), FRBusiness.class));
        //adapter.notifyDataSetChanged();
        //viewPagers.add(FragmentPagerItem.of(getResources().getString(R.string.reservations), FRMyOrders.class));
        //adapter.notifyDataSetChanged();
        //SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        //viewPagerTab.setViewPager(viewPager);

        ReadUbications();
        ReadCategorysCupon();

        getSupportActionBar().setTitle("");
        //ReadCountCuponsUser();
        ReadLastCommit();
        UpdateToken();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 155) {
            Intent intent = new Intent(context, com.creatio.imm.Menu.class);
            intent.putExtra("ID_business", prefs.getString("ID_business", "0"));
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //GetInformationUser();
        //ReadCountCuponsUser();
    }

    public void ReadUbications() {
        data_ubications.clear();
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvUbications)
                .load(R.layout.item_skeleton)
                .show();

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllCitys")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String city = obj.optString("city");

                            data_ubications.add(new OUbications("" + i, city, false));

                        }
                        final ADUbications adapter = new ADUbications(context, data_ubications);
                        lvUbications.setAdapter(adapter);
                        lvUbications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                city_selected = data_ubications.get(position).getName();
                                for (int i = 0; i < data_ubications.size(); i++) {
                                    data_ubications.get(i).setSelected(false);
                                }
                                data_ubications.get(position).setSelected(true);
                                adapter.notifyDataSetChanged();
                            }
                        });
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

    public void ReadCategorysCupon() {

        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllCategoryCupon")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String description = obj.optString("description");
                            String create_date = obj.optString("create_date");
                            String status = obj.optString("status");
                            String image = obj.optString("image");
                            data.add(new OCategoryCuponRight(ID, name, description, create_date, status, image, false));

                        }
                        final ADCategoriesRight adapter = new ADCategoriesRight(context, data);
                        grid.setAdapter(adapter);
                        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ID_category = data.get(position).getID();
                                for (int i = 0; i < data.size(); i++) {
                                    data.get(i).setSelected(false);
                                }
                                data.get(position).setSelected(true);
                                adapter.notifyDataSetChanged();
                            }
                        });
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

    public void ReadLastCommit() {

        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadLastCommit")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {

                        JSONArray arr = response.getJSONArray("data");
                        String name_bus = response.getString("name_bus");

                        //Falta calificar el último cupon validado
                        JSONObject obj = arr.getJSONObject(0);
                        String ID_cupon = obj.optString("ID_cupon");
                        String ID_r = obj.optString("ID");
                        Intent intet = new Intent(context, RateBus.class);
                        intet.putExtra("ID_cupon", ID_cupon);
                        intet.putExtra("name_bus", name_bus);
                        intet.putExtra("ID_r", ID_r);
                        startActivity(intet);

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            finish();
            final Intent intent = new Intent(MainActivity.this, TarjetaMenuIniActivity.class);
            intent.putExtra("ids",ids);
            intent.putExtra("numTarjeta",numTarjeta);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, MyCupons.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_filters) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener onclickRegress2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent i = new Intent(context,TarjetaMenuIniActivity.class);
            startActivity(i);
        }
    };

    public void UpdateToken() {
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "UpdateToken")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .addBodyParameter("token", token_fb)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("e", response.toString());
                try {
                    String success = response.getString("success");

                    if (success.equalsIgnoreCase("true")) {


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


}
