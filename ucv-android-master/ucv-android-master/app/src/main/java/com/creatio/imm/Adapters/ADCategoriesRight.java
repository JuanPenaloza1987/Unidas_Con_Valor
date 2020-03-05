package com.creatio.imm.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.creatio.imm.Objects.OCategoryCuponRight;
import com.creatio.imm.R;

import java.util.ArrayList;

public class ADCategoriesRight extends BaseAdapter {
    private Context context;
    private ArrayList<OCategoryCuponRight> data = new ArrayList<>();

    public ADCategoriesRight(Context context, ArrayList<OCategoryCuponRight> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_categories_right, parent, false);
        ImageView img = item.findViewById(R.id.img);
        TextView txtName = item.findViewById(R.id.txtName);
        CardView cv = item.findViewById(R.id.cv);
        txtName.setText(data.get(position).getName());

        //img.setVisibility(View.GONE);
        if (data.get(position).isSelected()){
            cv.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            txtName.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            cv.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
            txtName.setTextColor(context.getResources().getColor(R.color.colorAccentDark));
        }
        return item;
    }
}
