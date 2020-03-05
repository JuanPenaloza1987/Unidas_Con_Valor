package com.creatio.imm.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.creatio.imm.FRBusiness;
import com.creatio.imm.Objects.OCategoryCupon;
import com.creatio.imm.Objects.OCitys;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class ADCategoriesRC extends RecyclerView.Adapter<ADCategoriesRC.MyViewHolder> {
    private Context context;
    private ArrayList<OCategoryCupon> data = new ArrayList<>();
    private ArrayList<OCitys> dataCitys = new ArrayList<>();
    private FRBusiness fragment;

    public ADCategoriesRC(Context context, ArrayList<OCategoryCupon> data, FRBusiness fragment, ArrayList<OCitys> dataCitys) {
        this.context = context;
        this.data = data;
        this.dataCitys = dataCitys;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ADCategoriesRC.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categories_rc, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ADCategoriesRC.MyViewHolder holder, final int position) {
        holder.txtName.setText(data.get(position).getName());
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < data.size(); i++) {
                    data.get(i).setSelect(false);
                }
                data.get(position).setSelect(true);
                notifyDataSetChanged();
                if (data.get(position).getName().contains("Todos")){
                    fragment.RCAction("","");
                }else if (data.get(position).getName().contains("Con reservación")){
                    fragment.RCAction("0","");
                }else if (data.get(position).getName().contains("Buscar por ciudad")){
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenu().add(Menu.NONE, 0, Menu.NONE, "Todas las ciudades" );

                    for (int i = 0; i < dataCitys.size(); i++) {
                        popup.getMenu().add(Menu.NONE, Integer.parseInt(dataCitys.get(i).getID()), Menu.NONE, dataCitys.get(i).getCity() );
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            fragment.RCAction("",item.getTitle().toString());
                            return true;
                        }
                    });
                    popup.show();


                }else{
                    fragment.RCAction(data.get(position).getID(),"");
                }
            }
        });
        if (data.get(position).getSelect()){
            holder.ly.setBackgroundResource(R.drawable.btn_primary);
            holder.txtName.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            holder.ly.setBackgroundResource(R.drawable.btn_white);
            holder.txtName.setTextColor(context.getResources().getColor(R.color.colorAccentDark));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public LinearLayout ly;
        public MyViewHolder(View view) {
            super(view);

            txtName = (TextView) view.findViewById(R.id.txtName);
            ly = (LinearLayout) view.findViewById(R.id.lycat);

        }
    }
}