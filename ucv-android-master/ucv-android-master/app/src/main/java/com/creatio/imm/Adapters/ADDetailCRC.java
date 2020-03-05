package com.creatio.imm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.HelperClass;
import com.creatio.imm.ItemsMenu;
import com.creatio.imm.Objects.OCupons;
import com.creatio.imm.R;

import java.util.ArrayList;

public class ADDetailCRC extends RecyclerView.Adapter<ADDetailCRC.MyViewHolder> {
    Context context;
    ArrayList<OCupons> data = new ArrayList<>();


    public ADDetailCRC(Context context, ArrayList<OCupons> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_categorie, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.get(position).getS_vencido().equalsIgnoreCase("vencido")) {
                    Intent intent = new Intent(context, ItemsMenu.class);
                    intent.putExtra("ID_cupon", data.get(position).getID());
                    intent.putExtra("image", data.get(position).getImage());
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "Cupón vencido", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.btnSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.get(position).getS_vencido().equalsIgnoreCase("vencido")) {
                    Intent intent = new Intent(context, ItemsMenu.class);
                    intent.putExtra("ID_cupon", data.get(position).getID());
                    intent.putExtra("image", data.get(position).getImage());
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "Cupón vencido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (data.get(position).getS_vencido().equalsIgnoreCase("vencido")){
            holder.lyVencidov.setVisibility(View.VISIBLE);
        }else{
            holder.lyVencidov.setVisibility(View.GONE);
        }
        holder.txtName.setText(data.get(position).getName());
        holder.txtDesc.setText(data.get(position).getDescription());
        if (data.get(position).getPrice().equalsIgnoreCase("0")) {
            holder.txtPriceDesc.setText(data.get(position).getDiscount() + " %");
        } else {
            holder.txtPriceDesc.setText(HelperClass.formatDecimal(Double.parseDouble(data.get(position).getPrice())));
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
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView img;
        TextView txtName;
        TextView txtDesc;
        TextView txtPriceDesc;
        Button btnSee;
        CardView cv;
        LinearLayout lyVencidov;

        public MyViewHolder(View item) {
            super(item);
            img = item.findViewById(R.id.img);
            txtName = item.findViewById(R.id.txtName);
            txtDesc = item.findViewById(R.id.txtDesc);
            txtPriceDesc = item.findViewById(R.id.txtPriceDesc);
            btnSee = item.findViewById(R.id.btnSee);
            lyVencidov = item.findViewById(R.id.lyVencido);
            cv = item.findViewById(R.id.cv);

        }
    }

}
