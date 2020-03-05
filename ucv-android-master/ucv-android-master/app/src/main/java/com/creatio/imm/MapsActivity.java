package com.creatio.imm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADDetailMapRC;
import com.creatio.imm.Objects.OBusiness;
import com.creatio.imm.Objects.OCategoryCupon;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {
    private MapView mapView;
    private GoogleMap mMap;
    private SharedPreferences prefs;
    private Context context;
    private ArrayList<OBusiness> data_business = new ArrayList<>();
    private boolean flagmove = false;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private RecyclerView rcBusiness;
    ADDetailMapRC adapter;
    private String close_to_me = "1";
    private List<Marker> markerList = new ArrayList<Marker>();
    private ArrayList<OCategoryCupon> data_cat = new ArrayList<>();
    private Boolean yacargo = true;
    private String lattt = "";
    private String lnggg = "";
    private String ID_category = "0";
    Circle mapCircle;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = MapsActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cerca de mi");
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Call your Alert message
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage("Para tener una experiencia enciende tu GPS o ponlo en alta precisión");
            alert.setTitle("Oh Oh!");
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("En otro momento", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();

        }
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        rcBusiness = findViewById(R.id.rcBusiness);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    finish();
                    return;
                }
                mMap.setMyLocationEnabled(true);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {

                        for (int i = 0; i < data_business.size(); i++) {
                            data_business.get(i).setCan_reserve("0");
                            adapter.notifyItemChanged(i);
                        }
                        rcBusiness.scrollToPosition(Integer.parseInt(marker.getSnippet()));
                        data_business.get(Integer.parseInt(marker.getSnippet())).setCan_reserve("1");
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))      // Sets the center of the map to location user
                                .zoom(17)                   // Sets the zoom
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        marker.showInfoWindow();

                        adapter.notifyItemChanged(Integer.parseInt(marker.getSnippet()));
                        return true;
                    }
                });
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null) {

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {


                    }
                });
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        lattt = String.valueOf(arg0.getLatitude());
                        lnggg = String.valueOf(arg0.getLongitude());

                        if (yacargo) {
                            if (mapCircle != null) {
                                mapCircle.remove();
                            }
                            mapCircle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(arg0.getLatitude(), arg0.getLongitude()))
                                    .radius(1000)
                                    .strokeColor(Color.LTGRAY)
                                    .fillColor(Color.argb(55,200,200,200)));
                            ReadAllBranchs();
                            yacargo = false;
                        }


                    }
                });
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.clear();
                        lattt = String.valueOf(latLng.latitude);
                        lnggg = String.valueOf(latLng.longitude);
                        if (mapCircle != null) {
                            mapCircle.remove();
                        }
                        mapCircle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(latLng.latitude, latLng.longitude))
                                .radius(1000)
                                .strokeColor(Color.LTGRAY)
                                .fillColor(Color.argb(55,200,200,200)));
                        ReadAllBranchs();
                    }
                });

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcBusiness.setLayoutManager(layoutManager);

    }


    public void SelectMarker(int position) {
        Marker marker = markerList.get(position);
        marker.showInfoWindow();
        CameraUpdate cu = CameraUpdateFactory.newLatLng(marker.getPosition());
        mMap.moveCamera(cu);
        for (int i = 0; i < data_business.size(); i++) {
            data_business.get(i).setCan_reserve("0");
            adapter.notifyItemChanged(i);
        }
        rcBusiness.scrollToPosition(Integer.parseInt(marker.getSnippet()));
        data_business.get(Integer.parseInt(marker.getSnippet())).setCan_reserve("1");
    }

    public void ReadAllBranchs() {
        adapter = new ADDetailMapRC(context, data_business, MapsActivity.this);
        final SkeletonScreen skeletonScreen = Skeleton.bind(rcBusiness)
                .adapter(adapter)
                .load(R.layout.item_skeleton)
                .show();
        data_business.clear();
        markerList.clear();
        for (int i = 0; i < markerList.size(); i++) {
            markerList.get(i).remove();
        }

        builder = new LatLngBounds.Builder();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllBranchs")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("close_to_me", close_to_me)
                .addBodyParameter("lat", lattt)
                .addBodyParameter("lng", lnggg)
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
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
                            String ID = obj.optString("ID_business");
                            String name = obj.optString("name");
                            String name_business = obj.optString("name_business");
                            String address = obj.optString("address");
                            String create_date = obj.optString("create_date");
                            String image = obj.optString("image");
                            String img_business = obj.optString("img_business");
                            String latlng = obj.optString("location", "0,0");
                            String[] arrlatlng = latlng.split(",");
                            LatLng sydney = new LatLng(Double.parseDouble(arrlatlng[0]), Double.parseDouble(arrlatlng[1]));
                            int height = 100;
                            int width = 100;
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_markericupon);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            Marker mark = mMap.addMarker(new MarkerOptions().position(sydney).title(name_business + ", " + name).snippet("" + i).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                            markerList.add(mark);
                            builder.include(sydney);
                            data_business.add(new OBusiness(ID, name_business + ", " + name, address, create_date, img_business, "0"));
                        }
                        SharedPreferences.Editor edt = prefs.edit();
                        edt.putString("category", "0");
                        edt.apply();
                        if (data_business.size() > 0) {
                            LatLngBounds bounds = builder.build();

                            int padding = 70; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);
                            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
                            flagmove = true;
                            rcBusiness.setAdapter(adapter);
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

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custominfowindow, null);

        }

        @Override
        public View getInfoContents(Marker mark) {

            if (mark != null
                    && mark.isInfoWindowShown()) {
                mark.showInfoWindow();
            }
            return view;
        }

        @Override
        public View getInfoWindow(final Marker mark) {


            final String title = mark.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.txtName));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            return view;
        }

    }
}
