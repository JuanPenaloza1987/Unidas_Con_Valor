package com.creatio.imm.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.creatio.imm.HelperClass;
import com.creatio.imm.Objects.OCar;
import com.creatio.imm.OrderCar;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by Layge on 18/12/2017.
 */

public class ADLineOrder extends BaseAdapter {
    Context context;
    ArrayList<OCar> data = new ArrayList<>();

    public ADLineOrder(Context context, ArrayList<OCar> data) {
        this.context = context;
        this.data = data;
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
        final View item = inflater.inflate(R.layout.ad_line_order, viewGroup, false);
        TextView txtName = item.findViewById(R.id.txtName);
        TextView txtQ = item.findViewById(R.id.txtQuantity);
        TextView txtTotal = item.findViewById(R.id.txtTotal);
        ImageView img = item.findViewById(R.id.img);
        final ImageButton btnDelete = item.findViewById(R.id.btnDelete);
        txtName.setText(data.get(i).getName_menu());
        if (data.get(i).getQuantity().equalsIgnoreCase("1")) {
            txtQ.setText("x"+data.get(i).getQuantity());

        } else {

            txtQ.setText("x"+data.get(i).getQuantity());
        }
        if (data.get(i).getID_menu().equalsIgnoreCase("0")){
            img.setImageResource(R.drawable.ic_tag_white);
        }else{
            img.setImageResource(R.drawable.ic_restaurant);
        }
//        btnMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int q = Integer.parseInt(data.get(i).getQuantity());
//                if (q > 0) {
//                    q = q + 1;
//                    UpdateCar(data.get(i).getID(), String.valueOf(q));
//                }
//            }
//        });
//        btnLess.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int q = Integer.parseInt(data.get(i).getQuantity());
//                if (q > 1) {
//                    q = q - 1;
//                    UpdateCar(data.get(i).getID(), String.valueOf(q));
//                }
//            }
//        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);

                dialog.setContentView(R.layout.layout_alert_lottie);
                // set the custom dialog components - text, image and button
                TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                txtTitle.setText("¿Estás seguro de eliminar el platillo?");
                txtMsj.setText("Podrás agregarlo en el menu.");


                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);

                btnAceptar.setText("Si");
                btnCancelar.setText("No");
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteLine(data.get(i).getID());
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        double total = Double.parseDouble(data.get(i).getPrice_menu()) * Double.parseDouble(data.get(i).getQuantity());
        txtTotal.setText(HelperClass.formatDecimal(total));


        return item;
    }

    public void DeleteLine(final String ID) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        AndroidNetworking.post(R.string.apiurl + "DeleteLine")
                .addBodyParameter("ID", ID)
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .addBodyParameter("apikey", context.getResources().getString(R.string.apikey))
                .setPriority(Priority.MEDIUM)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.d("line", response.toString());

                ((OrderCar) context).GetCar();


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("line error", error.toString());
            }
        });

    }

//    public void UpdateCar(String ID, String quantity) {
//        AndroidNetworking.post("http://api.entrayecto.com/UpdateCar")
//                .addBodyParameter("ID", ID)
//                .addBodyParameter("quantity", quantity)
//                .addBodyParameter("apikey", "QuBvJ3w6dhOkC2vawLrf")
//                .setPriority(Priority.MEDIUM)
//                .build().getAsJSONArray(new JSONArrayRequestListener() {
//            @Override
//            public void onResponse(JSONArray response) {
//                //Reload
//            }
//
//            @Override
//            public void onError(ANError error) {
//                // handle error
//                Log.e("Orden error", error.toString());
//            }
//        });
//    }
}
