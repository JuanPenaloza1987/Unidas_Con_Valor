package com.creatio.imm.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.HelperClass;
import com.creatio.imm.Menu;
import com.creatio.imm.Objects.OCategorieMenu;
import com.creatio.imm.Objects.OMenu;
import com.creatio.imm.R;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gerardo on 29/08/18.
 */

public class ADMenu extends BaseExpandableListAdapter{
    private ArrayList<OCategorieMenu> data =  new ArrayList<>();
    private Context context;

    public ADMenu(ArrayList<OCategorieMenu> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).getDataMenu().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_group_menu, parent, false);
        TextView txtName = item.findViewById(R.id.txtName);

        txtName.setText(data.get(groupPosition).getName());
        if (data.get(groupPosition).getName().equalsIgnoreCase("Cupones disponibles")){
            txtName.setBackgroundResource(R.drawable.btn_primary);
        }else{
            txtName.setBackgroundResource(R.drawable.btn_green);
        }
        return item;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_children_menu, parent, false);
        final ArrayList<OMenu> dataMenu = data.get(groupPosition).getDataMenu();

        TextView txtName = item.findViewById(R.id.txtName);
        TextView txtDesc = item.findViewById(R.id.txtDesc);
        TextView txtPrice = item.findViewById(R.id.txtPrice);
        final Button btnOrdenar = item.findViewById(R.id.btnOrdenar);
        if (data.get(groupPosition).getName().equalsIgnoreCase("Cupones disponibles")){
            btnOrdenar.setText("Comprar");
        }else{
            btnOrdenar.setText("Ordenar");
        }
        txtName.setText(dataMenu.get(childPosition).getName());
        txtDesc.setText(dataMenu.get(childPosition).getDescription());
        if (dataMenu.get(childPosition).getPrice().equalsIgnoreCase("0")){
            txtPrice.setText(dataMenu.get(childPosition).getIs_offer() + "%");
        }else {
            txtPrice.setText(HelperClass.formatDecimal(Double.parseDouble(dataMenu.get(childPosition).getPrice())));
        }
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        btnOrdenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getBoolean("login", false)) {
                    String total = dataMenu.get(childPosition).getPrice();
                    String ID_menu = dataMenu.get(childPosition).getID();
                    String ID_cupon = dataMenu.get(childPosition).getID();
                    if (btnOrdenar.getText().toString().equalsIgnoreCase("Comprar")){
                        //Es un cupón
                        ID_menu = "0";
                        ID_cupon = dataMenu.get(childPosition).getID();
                    }else{
                        ID_cupon = "0";
                        ID_menu = dataMenu.get(childPosition).getID();
                    }

                    String ID_cat = dataMenu.get(childPosition).getID_categorie();
                    String quantity = "1";


                    AndroidNetworking.post(context.getResources().getString(R.string.apiurl) + "SetCar")
                            .addBodyParameter("ID_menu", ID_menu)
                            .addBodyParameter("ID_cupon", ID_cupon)
                            .addBodyParameter("ID_cat", ID_cat)
                            .addBodyParameter("total", total)
                            .addBodyParameter("quantity", quantity)
                            .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                            .addBodyParameter("apikey", context.getResources().getString(R.string.apikey))
                            .setPriority(Priority.MEDIUM)
                            .build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("carrito menu", response.toString());
                                String success = response.getString("success");
                                if (success.equalsIgnoreCase("true")) {
                                    ((Menu)context).GetBadgeCar();
                                    Alerter.create(((Menu)context))
                                            .setTitle("Hecho")
                                            .setText("Se ha agregado el platillo al carrito.")
                                            .setBackgroundColorInt(context.getResources().getColor(R.color.green))
                                            .show();
                                }else{
                                    Alerter.create(((Menu)context))
                                            .setTitle("Ups")
                                            .setText("Solo puedes añadir artículos de un negocio a la vez.")
                                            .setBackgroundColorInt(context.getResources().getColor(R.color.red))
                                            .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Log.e("Orden error", error.toString());
                        }
                    });

                } else {
                    Toast.makeText(context, "Debes ser usuario para hacer ordenes. ¡Es muy fácil!", Toast.LENGTH_SHORT).show();

                    //startActivity(new Intent(context, Login.class));
                }
            }
        });
        return item;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
