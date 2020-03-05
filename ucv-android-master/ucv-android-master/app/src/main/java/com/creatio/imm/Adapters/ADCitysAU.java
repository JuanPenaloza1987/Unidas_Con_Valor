package com.creatio.imm.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.creatio.imm.Objects.OCitys;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by Layge on 18/12/2017.
 */

public class ADCitysAU extends ArrayAdapter<String> implements Filterable {
    Context context;
    ArrayList<OCitys> data = new ArrayList<>();
    private ArrayList<OCitys> filterdata = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();
    private int itemLayout;


    public ADCitysAU(@NonNull Context context, int resource, ArrayList<OCitys> data) {
        super(context, resource);
        this.context = context;
        this.data = data;
        this.filterdata = data;
        this.itemLayout = resource;
    }

    @Override
    public int getCount() {
        return (filterdata == null) ? 0 : filterdata.size();
    }

    @Override
    public String getItem(int i) {
        return filterdata.get(i).getCity() + ", " + filterdata.get(i).getState();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(itemLayout, viewGroup, false);
        }
        TextView txtTitle;
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("id_city",filterdata.get(i).getID());
                edit.apply();
            }
        });
        txtTitle.setText(filterdata.get(i).getCity() + ", " + filterdata.get(i).getState());

        return view;
    }

    @Override
    public Filter getFilter() {


        return mFilter;

    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<OCitys> list = data;

            int count = list.size();
            final ArrayList<OCitys> nlist = new ArrayList<OCitys>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getCity();

                String id = list.get(i).getID();
                String city = list.get(i).getCity();
                String state = list.get(i).getState();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(new OCitys(id, city, state));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            filterdata = (ArrayList<OCitys>) results.values;
            notifyDataSetChanged();
        }

    }



}

