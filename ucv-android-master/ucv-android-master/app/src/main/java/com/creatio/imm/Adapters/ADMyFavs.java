package com.creatio.imm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.HelperClass;
import com.creatio.imm.Objects.OMyCupons;
import com.creatio.imm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gerardo on 18/06/18.
 */

public class ADMyFavs extends BaseAdapter {
    Context context;
    ArrayList<OMyCupons> data = new ArrayList<>();


    public ADMyFavs(Context context, ArrayList<OMyCupons> data) {
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
        final View item = inflater.inflate(R.layout.item_my_cupons, parent, false);
        ImageView img = item.findViewById(R.id.img);
        TextView txtName = item.findViewById(R.id.txtName);
        TextView txtPrice = item.findViewById(R.id.txtPrice);
        TextView txtBusiness = item.findViewById(R.id.txtBusiness);
        TextView txtDate = item.findViewById(R.id.txtDate);
        TextView txtStatus = item.findViewById(R.id.txtStatus);
        LinearLayout lyInactive = item.findViewById(R.id.lyInactive);

        txtStatus.setVisibility(View.INVISIBLE);
        lyInactive.setVisibility(View.GONE);
        txtDate.setVisibility(View.GONE);

        txtName.setText(data.get(position).getName());
        if (data.get(position).getPrice().equalsIgnoreCase("0")) {
            txtPrice.setText(data.get(position).getDiscount() + " %");
        } else {
            txtPrice.setText(HelperClass.formatDecimal(Double.parseDouble(data.get(position).getPrice())));
        }
//        if (data.get(position).getStatus().equalsIgnoreCase("1")){
//            lyInactive.setVisibility(View.VISIBLE);
//            txtStatus.setVisibility(View.VISIBLE);
//        }

        txtBusiness.setText(data.get(position).getName_business());

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date strDate = sdf.parse(data.get(position).getDate_reserved());
            if (new Date().before(strDate)) {

            } else {

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        return item;
    }
}
