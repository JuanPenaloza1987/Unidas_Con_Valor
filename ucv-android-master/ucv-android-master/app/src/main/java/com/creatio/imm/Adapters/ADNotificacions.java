package com.creatio.imm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.ItemsMenu;
import com.creatio.imm.Objects.ONotifications;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class ADNotificacions extends BaseAdapter {
    Context context;
    ArrayList<ONotifications> data = new ArrayList<>();


    public ADNotificacions(Context context, ArrayList<ONotifications> data) {
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
        final View item = inflater.inflate(R.layout.item_notificacions, parent, false);
        ImageView img = item.findViewById(R.id.img);
        TextView txtName = item.findViewById(R.id.txtName);
        TextView txtDesc = item.findViewById(R.id.txtDesc);
        TextView txtDate = item.findViewById(R.id.txtDate);

        txtName.setText(data.get(position).getName());
        txtDesc.setText(data.get(position).getDescription());
        txtDate.setText(data.get(position).getDate());
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
        CardView cv = item.findViewById(R.id.cv);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position).getID_cupon().equalsIgnoreCase("0")){
                    Toast.makeText(context, "Notificación solo informativa", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(context, ItemsMenu.class);
                    intent.putExtra("ID_cupon", data.get(position).getID_cupon());
                    intent.putExtra("image", data.get(position).getImage());
                    context.startActivity(intent);
                }
            }
        });
        return item;
    }
}
