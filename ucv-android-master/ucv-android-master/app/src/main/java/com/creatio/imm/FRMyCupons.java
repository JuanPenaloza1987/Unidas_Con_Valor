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
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADMyCupons;
import com.creatio.imm.Objects.OCupons;
import com.creatio.imm.Objects.OMyCupons;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class FRMyCupons extends Fragment {
    private SharedPreferences prefs;
    private Context context;
    private ListView lvCupons;
    private ArrayList<OMyCupons> data_last = new ArrayList<>();

    private ArrayList<OCupons> data = new ArrayList<>();

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
        ReadMyReservedCupons();
        super.onResume();

    }

    public void ReadMyReservedCupons() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvCupons)
                .load(R.layout.item_skeleton)
                .show();
        data_last.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadMyReservedCupons")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("e", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String ID_r = obj.optString("ID_r");
                            String name = obj.optString("name");
                            String description = obj.optString("description");
                            String create_date = obj.optString("create_date");
                            String status = obj.optString("status_c");
                            String type = obj.optString("type");
                            String category = obj.optString("category");
                            String quantity = obj.optString("quantity");
                            String image = obj.optString("image");
                            String price = obj.optString("price");
                            String discount = obj.optString("discount");
                            String name_branch = obj.optString("name_branch");
                            String name_business = obj.optString("name_business");
                            String date_reserved = obj.optString("date_reserved");
                            String count_days = obj.optString("count_days");
                            String is_cuponcode = obj.optString("is_cuponcode");

                            data_last.add(new OMyCupons(ID, name, description, create_date, status, quantity, type, category, image, price, discount, name_branch, name_business, date_reserved, count_days, ID_r,is_cuponcode));

                        }
                        if (data_last.size() == 0) {
                            lvCupons.setVisibility(View.GONE);
                        } else {
                            lvCupons.setVisibility(View.VISIBLE);
                        }
                        ADMyCupons adapter = new ADMyCupons(context, data_last, "my");
                        lvCupons.setAdapter(adapter);
                        lvCupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(context, CuponActive.class);
                                intent.putExtra("ID_cupon", data_last.get(position).getID());
                                intent.putExtra("ID_r", data_last.get(position).getID_r());
                                intent.putExtra("name", data_last.get(position).getName_business());
                                intent.putExtra("is_special", false);
                                startActivity(intent);


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

}
