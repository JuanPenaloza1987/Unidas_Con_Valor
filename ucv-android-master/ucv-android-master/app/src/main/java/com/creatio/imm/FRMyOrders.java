package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.creatio.imm.Adapters.ADPedidos;
import com.creatio.imm.Objects.OPEdidos;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class FRMyOrders extends Fragment {
    private SharedPreferences prefs;
    private Context context;
    private ListView lvCupons;
    private ArrayList<OPEdidos> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fr_view_mycupons, container, false);
        context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        lvCupons = view.findViewById(R.id.lvCupons);

        return view;
    }


    @Override
    public void onResume() {
        GetPedidos();
        super.onResume();

    }

    public void GetPedidos() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvCupons)
                .load(R.layout.item_skeleton)
                .show();
        data.clear();
        AndroidNetworking.post(R.string.apiurl + "GetPedidos")
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("pedidos", response.toString());
                try {
                    double totalgral = 0.0;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String ID = object.optString("ID");
                        String create_date = object.optString("create_date");
                        String status = object.optString("status");
                        String location = object.optString("location");
                        String total = object.optString("total");
                        String type = object.optString("type");
                        String is_dom = object.optString("is_dom");
                        String location_branch = object.optString("location_branch");
                        String name_business = object.optString("name_business");
                        String horarev = object.optString("horarev");
                        String persons = object.optString("persons");
                        String date_rev = object.optString("date_rev");
                        String area = object.optString("area");
                        String ID_rev = object.optString("ID_rev");
                        String has_rev = object.optString("has_rev");
                        String name_area = object.optString("name_area");

                        data.add(new OPEdidos(ID, create_date, status, location, total, type, is_dom, location_branch, name_business,horarev,persons,date_rev,area,has_rev,ID_rev, name_area));
                    }
                    skeletonScreen.hide();
                    ADPedidos adapter = new ADPedidos(context, data, FRMyOrders.this);
                    lvCupons.setAdapter(adapter);
                    lvCupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent  = new Intent(context,DetailOrder.class);
                            intent.putExtra("ID_order",data.get(position).getID());
                            intent.putExtra("has_reservation",data.get(position).getHas_rev());
                            intent.putExtra("reservation",data.get(position).getDate_rev() + " a las " + data.get(position).getHorarev() + " - área: " + data.get(position).getName_area());
                            startActivity(intent);
                        }
                    });
                    

                } catch (JSONException e) {
                    Log.e("pedidos error", e.toString());
                }


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("pedidos error", error.toString());
            }
        });
    }

}
