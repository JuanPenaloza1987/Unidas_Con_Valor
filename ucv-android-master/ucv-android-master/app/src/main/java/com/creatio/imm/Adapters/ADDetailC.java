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

public class ADDetailC extends BaseAdapter{
    Context context;
    ArrayList<OCupons> data = new ArrayList<>();


    public ADDetailC(Context context, ArrayList<OCupons> data) {
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
        final View item = inflater.inflate(R.layout.item_detail_categorie, parent, false);
        ImageView img = item.findViewById(R.id.img);
        TextView txtName = item.findViewById(R.id.txtName);
        TextView txtDesc = item.findViewById(R.id.txtDesc);
        TextView txtPriceDesc = item.findViewById(R.id.txtPriceDesc);
        LinearLayout lyVencidov = item.findViewById(R.id.lyVencido);
        Button btnSee = item.findViewById(R.id.btnSee);
        CardView cv = item.findViewById(R.id.cv);
        cv.setOnClickListener(new View.OnClickListener() {
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
        btnSee.setOnClickListener(new View.OnClickListener() {
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
            lyVencidov.setVisibility(View.VISIBLE);
        }else{
            lyVencidov.setVisibility(View.GONE);
        }
        txtName.setText(data.get(position).getName());
        txtDesc.setText(data.get(position).getDescription());
        if (data.get(position).getPrice().equalsIgnoreCase("0")){
            txtPriceDesc.setText(data.get(position).getDiscount()  + " %");
        }else{
            txtPriceDesc.setText(HelperClass.formatDecimal(Double.parseDouble(data.get(position).getPrice())));
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
