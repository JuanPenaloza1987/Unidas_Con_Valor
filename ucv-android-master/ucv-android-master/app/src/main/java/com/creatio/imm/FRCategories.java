package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.creatio.imm.Adapters.ADCategories;
import com.creatio.imm.Objects.OBanner;
import com.creatio.imm.Objects.OCategoryCupon;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import me.relex.circleindicator.CircleIndicator;

public class FRCategories extends Fragment {
    private SharedPreferences prefs;
    private Context context;
    private GridViewWithHeaderAndFooter grid;
    private EditText edtFind;
    private ArrayList<OCategoryCupon> data = new ArrayList<>();
    private ArrayList<OBanner> data_last = new ArrayList<>();
    ViewPager viewPager;
    CircleIndicator indicator;
    ADBanner adapterBanner;
    ImageButton btnFilter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fr_view_categories, container, false);
        context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        grid = view.findViewById(R.id.grid);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        edtFind = view.findViewById(R.id.edtFind);
        edtFind.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    //Action to search

                    Intent intent = new Intent(context, Categorie_detail.class);

                    intent.putExtra("category", "0");
                    intent.putExtra("words", v.getText().toString());
                    intent.putExtra("name_category", "Busqueda de cupones");

                    startActivity(intent);
                }
                return false;
            }
        });
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View headerView = layoutInflater.inflate(R.layout.header_categories, null);
        viewPager = headerView.findViewById(R.id.viewpager);
        indicator = (CircleIndicator) headerView.findViewById(R.id.indicator);
        Button btnMap = headerView.findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermissions(
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
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

        grid.addHeaderView(headerView);
        //ReadCategorysCupon();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean("datauser", false)) {
            Intent intent = new Intent(context, DataUser.class);
            startActivity(intent);
        }
        /*
        if (prefs.getBoolean("primeravez", true)) {
            startActivity(new Intent(context, Intro.class));
        }
        */

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ReadCategorysCupon();
        ReadLastCupon();
    }

    public void ReadCategorysCupon() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(grid)
                .load(R.layout.item_skeleton)
                .show();
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
                            data.add(new OCategoryCupon(ID, name, description, create_date, status, image, false));

                        }
                        if(data.size() > 0) {
                            ADCategories adapter = new ADCategories(context, data);
                            grid.setAdapter(adapter);
                            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(context, Categorie_detail.class);
                                    intent.putExtra("category", data.get(position).getID());
                                    intent.putExtra("name_category", data.get(position).getName());
                                    intent.putExtra("words", "");
                                    startActivity(intent);
                                }
                            });
                        }else{
                            ReadCategorysCupon();
                        }
                        ReadLastCupon();
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

    public void ReadLastCupon() {

        data_last.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadBannerTop")
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

                    } else {

                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Error catch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                viewPager.setVisibility(View.GONE);
            }
        });
    }
}
