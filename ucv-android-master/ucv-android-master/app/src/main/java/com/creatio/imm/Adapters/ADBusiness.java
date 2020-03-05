package com.creatio.imm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.ItemsBusiness;
import com.creatio.imm.Objects.OBusiness;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class ADBusiness extends BaseAdapter {
    private Context context;
    private ArrayList<OBusiness> data = new ArrayList<>();


    public ADBusiness(Context context, ArrayList<OBusiness> data) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_business, parent, false);
        ImageView img = item.findViewById(R.id.img);
        TextView txtName = item.findViewById(R.id.txtName);
        TextView txtCan = item.findViewById(R.id.txtCan);
        TextView txtDescription = item.findViewById(R.id.txtDesc);
        Button btnCupons = item.findViewById(R.id.btnCupons);
        CardView cv = item.findViewById(R.id.cv);
        if (data.get(position).getCan_reserve().equalsIgnoreCase("0")){
            txtCan.setVisibility(View.INVISIBLE);
        }else{
            txtCan.setVisibility(View.INVISIBLE);
        }
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ItemsBusiness.class);
                intent.putExtra("ID_business", data.get(position).getID());
                intent.putExtra("image", data.get(position).getImage());
                intent.putExtra("name", data.get(position).getName());
                intent.putExtra("can_reserve", data.get(position).getCan_reserve());
                context.startActivity(intent);
            }
        });
        txtName.setText(data.get(position).getName());
        txtDescription.setText(data.get(position).getDescription());
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(1000, 1000)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(data.get(position).getImage())
                .apply(options)
                .into(img);
        btnCupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemsBusiness.class);
                intent.putExtra("ID_business", data.get(position).getID());
                intent.putExtra("image", data.get(position).getImage());
                intent.putExtra("name", data.get(position).getName());
                intent.putExtra("can_reserve", data.get(position).getCan_reserve());
                context.startActivity(intent);
            }
        });
        return item;
    }
}
