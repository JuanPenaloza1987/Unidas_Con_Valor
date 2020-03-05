package com.creatio.imm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.ItemsBusiness;
import com.creatio.imm.MapsActivity;
import com.creatio.imm.Objects.OBusiness;
import com.creatio.imm.R;


import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class ADDetailMapRC extends RecyclerView.Adapter<ADDetailMapRC.MyViewHolder> {
    private Context context;
    private ArrayList<OBusiness> data = new ArrayList<>();
    private MapsActivity activity;

    public ADDetailMapRC(Context context, ArrayList<OBusiness> data, MapsActivity activity) {
        this.context = context;
        this.activity = activity;
        this.data = data;

    }

    @NonNull
    @Override
    public ADDetailMapRC.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_map, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ADDetailMapRC.MyViewHolder holder, final int position) {
        holder.txtName.setText(data.get(position).getName());
        holder.txtDesc.setText(data.get(position).getDescription());


        if (data.get(position).getCan_reserve().equalsIgnoreCase("1")){
            holder.cv.setBackgroundResource(R.drawable.corner_stroke);
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ItemsBusiness.class);
                    intent.putExtra("ID_business", data.get(position).getID());
                    intent.putExtra("image", data.get(position).getImage());
                    intent.putExtra("name", data.get(position).getName());
                    context.startActivity(intent);

                }
            });

        }else{
            holder.cv.setBackgroundResource(R.drawable.corner_stroke_2);
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.SelectMarker(position);

                }
            });

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
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public ImageView img;
        public TextView txtDesc;
        public TextView txtPriceDesc;
        public CardView cv;
        public MyViewHolder(View view) {
            super(view);

            txtName = (TextView) view.findViewById(R.id.txtName);
            txtDesc = (TextView) view.findViewById(R.id.txtDesc);
            txtPriceDesc = (TextView) view.findViewById(R.id.txtPriceDesc);
            img = (ImageView) view.findViewById(R.id.img);
            cv = (CardView) view.findViewById(R.id.cv);

        }
    }
}