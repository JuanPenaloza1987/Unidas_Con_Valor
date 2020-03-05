package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADBusiness;
import com.creatio.imm.Adapters.ADCategoriesRC;
import com.creatio.imm.Objects.OBusiness;
import com.creatio.imm.Objects.OCategoryCupon;
import com.creatio.imm.Objects.OCitys;
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

/**
 * Created by gerardo on 18/06/18.
 */

public class FRBusiness extends Fragment {
    private SharedPreferences prefs;
    private Context context;
    private RecyclerView rcCategory;
    private ListView lvBusiness;
    private EditText edtFind;
    private ArrayList<OCategoryCupon> data = new ArrayList<>();
    private ArrayList<OCitys> dataCitys = new ArrayList<>();
    private ArrayList<OBusiness> data_business = new ArrayList<>();

    private ImageButton btnFilter;
    private Button btnMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fr_view_business, container, false);
        context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        lvBusiness = view.findViewById(R.id.lvBusiness);
        rcCategory = view.findViewById(R.id.rcCategory);
        btnMap = view.findViewById(R.id.btnMap);
        rcCategory = view.findViewById(R.id.rcCategory);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        edtFind = view.findViewById(R.id.edtFind);
        edtFind.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    //Action to search

                    Intent intent = new Intent(context, Categorie_detail.class);

                    intent.putExtra("category", "0");
                    intent.putExtra("words", v.getText().toString());
                    intent.putExtra("name_category", "Búsqueda de negocios");

                    startActivity(intent);
                }
                return false;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcCategory.setLayoutManager(layoutManager);
        //ReadCategorysCupon();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ReadCategorysCupon();
        ReadAllBusiness("","");
    }

    public void ReadCategorysCupon() {
        final ADCategoriesRC adapter = new ADCategoriesRC(context, data, FRBusiness.this,dataCitys);
        final SkeletonScreen skeletonScreen = Skeleton.bind(rcCategory)
                .adapter(adapter)
                .load(R.layout.item_skeleton)
                .show();
        data.clear();
        dataCitys.clear();
        data.add(new OCategoryCupon("0", "Todos", "","", "", "", false));
        data.add(new OCategoryCupon("0", "Buscar por ciudad", "","", "", "", false));
        data.add(new OCategoryCupon("0", "Con reservación", "","", "", "", false));
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

                            dataCitys.add(new OCitys("" + i, city, ""));

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

                        rcCategory.setAdapter(adapter);


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

    public void RCAction(String category,String city){

        ReadAllBusiness(category, city);
    }
    public void ReadAllBusiness(String category, String city) {
        final ADBusiness adapter = new ADBusiness(context, data_business);
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvBusiness)
                .load(R.layout.item_skeleton)
                .show();
        data_business.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllBusiness")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("category", category)
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .addBodyParameter("city", city)
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
                            String image = obj.optString("image");
                            String can_reserve = obj.optString("can_reserve");
                            data_business.add(new OBusiness(ID, name, description, create_date, image,can_reserve));

                        }
                        if (data_business.size() == 0){
                            lvBusiness.setVisibility(View.GONE);
                        }else{
                            lvBusiness.setVisibility(View.VISIBLE);
                        }

                        lvBusiness.setAdapter(adapter);

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

}
