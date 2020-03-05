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
import com.creatio.imm.ItemsBusiness;
import com.creatio.imm.Objects.OCupons;
import com.creatio.imm.R;

import java.util.ArrayList;

public class ADDetailCBusiness extends BaseAdapter{
    Context context;
    ArrayList<OCupons> data = new ArrayList<>();
    String category;

    public ADDetailCBusiness(Context context, ArrayList<OCupons> data, String category) {
        this.context = context;
        this.data = data;
        this.category = category;
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
        final View item = inflater.inflate(R.layout.item_detail_categorie_business, parent, false);
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
                    /*
                    Intent intent = new Intent(context, Categorie_detail_business.class);
                    intent.putExtra("category", category);
                    intent.putExtra("ID_business", data.get(position).getID());
                    intent.putExtra("name_category", data.get(position).getName());
                    intent.putExtra("words", "");
                    context.startActivity(intent);
                    */
                    Intent intentb = new Intent(context, ItemsBusiness.class);
                    intentb.putExtra("ID_business", data.get(position).getID());
                    intentb.putExtra("image", data.get(position).getImage());
                    intentb.putExtra("name", data.get(position).getName());
                    context.startActivity(intentb);
                }else{
                    Toast.makeText(context, "Cupón vencido", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.get(position).getS_vencido().equalsIgnoreCase("vencido")) {
                    /*
                    Intent intent = new Intent(context, Categorie_detail_business.class);
                    intent.putExtra("category", category);
                    intent.putExtra("ID_business", data.get(position).getID());
                    intent.putExtra("name_category", data.get(position).getName());
                    intent.putExtra("words", "");
                    context.startActivity(intent);
                    */
                    Intent intentb = new Intent(context, ItemsBusiness.class);
                    intentb.putExtra("ID_business", data.get(position).getID());
                    intentb.putExtra("image", data.get(position).getImage());
                    intentb.putExtra("name", data.get(position).getName());
                    context.startActivity(intentb);
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
