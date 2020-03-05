package com.creatio.imm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creatio.imm.Objects.OSchools;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by gerardo on 20/06/18.
 */

public class ADSchools extends BaseAdapter {
    Context context;
    ArrayList<OSchools> data =  new ArrayList<>();

    public ADSchools(Context context, ArrayList<OSchools> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_spinner_login, parent, false);
        TextView txtName = item.findViewById(R.id.txtName);
        if (!data.get(position).getName().contains("¿A")){

            txtName.setText(data.get(position).getName() + ", " + data.get(position).getDescription());
        }else{
            txtName.setText(data.get(position).getName());
        }
        return item;
    }
}
