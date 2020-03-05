package com.creatio.imm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADBusiness;
import com.creatio.imm.Objects.OBusiness;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class FRReservations extends Fragment {
    private SharedPreferences prefs;
    private Context context;
    private ListView lvBusiness;
    private ArrayList<OBusiness> data_business = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fr_view_reservations, container, false);
        context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        lvBusiness = view.findViewById(R.id.lvBus);
        ReadAllBusiness("");
        return view;
    }

    public void ReadAllBusiness(String category) {
        final ADBusiness adapter = new ADBusiness(context, data_business);
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvBusiness)
                .load(R.layout.item_skeleton)
                .show();
        data_business.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllBusiness")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("category", category)
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
                            if (can_reserve.equalsIgnoreCase("1")) {
                                data_business.add(new OBusiness(ID, name, description, create_date, image, can_reserve));
                            }
                        }
                        if (data_business.size() == 0) {
                            lvBusiness.setVisibility(View.GONE);
                        } else {
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
