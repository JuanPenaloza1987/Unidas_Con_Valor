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

import com.creatio.imm.Objects.OSchools;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by gerardo on 20/06/18.
 */

public class ADSchoolsAU extends ArrayAdapter<String> implements Filterable {
    Context context;
    ArrayList<OSchools> data = new ArrayList<>();
    private ArrayList<OSchools> filterdata = new ArrayList<>();
    private ADSchoolsAU.ItemFilter mFilter = new ADSchoolsAU.ItemFilter();
    private int itemLayout;


    public ADSchoolsAU(@NonNull Context context, int resource, ArrayList<OSchools> data) {
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
        return filterdata.get(i).getID() ;
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
                edit.putString("id_rel",filterdata.get(i).getID());
                edit.apply();
            }
        });
        txtTitle.setText(filterdata.get(i).getName() + ", " + filterdata.get(i).getDescription());

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

            final ArrayList<OSchools> list = data;

            int count = list.size();
            final ArrayList<OSchools> nlist = new ArrayList<OSchools>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();

                String id = list.get(i).getID();
                String name = list.get(i).getName();
                String description = list.get(i).getDescription();
                String create_date = list.get(i).getCreate_date();
                String image = list.get(i).getImage();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(new OSchools(id,name,description,create_date,image));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            filterdata = (ArrayList<OSchools>) results.values;
            notifyDataSetChanged();
        }

    }
}
