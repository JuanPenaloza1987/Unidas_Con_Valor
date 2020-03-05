package com.creatio.imm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creatio.imm.Objects.OUbications;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by gerardo on 20/06/18.
 */

public class ADUbications extends BaseAdapter {
    Context context;
    ArrayList<OUbications> data = new ArrayList<>();

    public ADUbications(Context context, ArrayList<OUbications> data) {
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
        final View item = inflater.inflate(R.layout.item_ubications, parent, false);
        TextView txtName = item.findViewById(R.id.txtName);
        txtName.setText(data.get(position).getName());
        if (data.get(position).isSelected()){
            txtName.setBackgroundResource(R.drawable.btn_primary);
            txtName.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            txtName.setBackgroundResource(R.drawable.btn_white);
            txtName.setTextColor(context.getResources().getColor(R.color.colorAccentDark));
        }
        return item;

    }
}
