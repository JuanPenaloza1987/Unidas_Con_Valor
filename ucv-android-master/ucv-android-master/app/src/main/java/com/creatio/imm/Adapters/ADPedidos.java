package com.creatio.imm.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.creatio.imm.FRMyOrders;
import com.creatio.imm.HelperClass;
import com.creatio.imm.Objects.OPEdidos;
import com.creatio.imm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Layge on 18/12/2017.
 */

public class ADPedidos extends BaseAdapter {
    Context context;
    ArrayList<OPEdidos> data = new ArrayList<>();
    FRMyOrders fragment;
    String statusstr = "";

    public ADPedidos(Context context, ArrayList<OPEdidos> data, FRMyOrders fragment) {
        this.context = context;
        this.data = data;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.ad_pedidos, viewGroup, false);
        TextView txtTotal, txtOrder, txtFecha, txtName, txtStatus;
        txtOrder = item.findViewById(R.id.txtOrder);
        txtTotal = item.findViewById(R.id.txtTotal);
        txtName = item.findViewById(R.id.txtName);
        txtStatus = item.findViewById(R.id.txtStatus);
        txtFecha = item.findViewById(R.id.txtDate);
        Button btnCancel = item.findViewById(R.id.btnCancel);

        txtTotal.setText(HelperClass.formatDecimal(Double.parseDouble(data.get(i).getTotal())));
        txtOrder.setText("Orden número: " + data.get(i).getID());
        if (data.get(i).getHas_rev().equalsIgnoreCase("1")){
            btnCancel.setVisibility(View.VISIBLE);
            txtFecha.setVisibility(View.VISIBLE);
        }else{
            txtFecha.setVisibility(View.GONE);
            btnCancel.setVisibility(View.INVISIBLE);
        }
        txtFecha.setText(HelperClass.FormatDate(data.get(i).getDate_rev() + " " + data.get(i).getHorarev()));
        txtName.setText(data.get(i).getName_branch());


        if (data.get(i).getStatus().equalsIgnoreCase("1")) {
            //Creada
            txtStatus.setTextColor(context.getResources().getColor(R.color.alerter_default_success_background));
            txtStatus.setText("Creada");
        }
        if (data.get(i).getStatus().equalsIgnoreCase("2")) {
            //En camino
            txtStatus.setTextColor(context.getResources().getColor(R.color.light_blue_500));
            txtStatus.setText("En camino");
        }
        if (data.get(i).getStatus().equalsIgnoreCase("3")) {
            //Pagada
            txtStatus.setTextColor(context.getResources().getColor(R.color.colorAccent));
            txtStatus.setText("Pagada");
        }
        if (data.get(i).getStatus().equalsIgnoreCase("4")) {
            //Cancelada
            txtStatus.setTextColor(context.getResources().getColor(R.color.alert_default_error_background));
            txtStatus.setText("Cancelada");
        }
        if (data.get(i).getStatus().equalsIgnoreCase("5")) {
            //terminada
            txtStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            txtStatus.setText("Terminada");
        }
        if (data.get(i).getStatus().equalsIgnoreCase("6")) {
            //terminada
            txtStatus.setTextColor(context.getResources().getColor(R.color.teal_500));
            txtStatus.setText("En proceso");
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                ChangeStatusReservations("2", data.get(i).getID(),prefs.getString("ID","0"),data.get(i).getID_rev());
            }
        });
        return item;
    }


    public void ChangeStatusReservations(final String status, final String ID_order, final String ID_user, final String ID_reservation) {
        if (status.equalsIgnoreCase("2")) {
            statusstr = "Cancelada";
        }else{
            statusstr = "Aceptada";
        }



        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Precaución");
        alert.setMessage("¿Estás seguro de cambiar el estatus de la reservación seleccionada a " + statusstr + "?");
        alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AndroidNetworking.post(R.string.apiurl +"ChangeStatusReservations")
                        .addBodyParameter("status", status)
                        .addBodyParameter("ID_order", ID_order)
                        .addBodyParameter("ID_reservation", ID_reservation)
                        .addBodyParameter("ID_user", ID_user)
                        .addBodyParameter("statusstr", statusstr)
                        .addBodyParameter("apikey", context.getResources().getString(R.string.apikey))
                        .setPriority(Priority.MEDIUM)
                        .build().getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("status branch", response.toString());
                        //HelperClass.SendNotification(ID_user,"Orden número " + ID_order , "Tu orden ha cambiado de estatus: " + statusstr, "0");

                        Toast.makeText(context, "Se ha cambiado el estatus", Toast.LENGTH_SHORT).show();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                String ID = object.optString("ID");
                            }

                        } catch (JSONException e) {
                            Log.e("status ex", e.toString());
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("status error", error.toString());
                    }
                });
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();

    }
}
