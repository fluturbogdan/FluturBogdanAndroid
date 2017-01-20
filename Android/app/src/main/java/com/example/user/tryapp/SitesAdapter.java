package com.example.user.tryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 12/1/2016.
 */

public class SitesAdapter extends ArrayAdapter<List_of_items>{

    public SitesAdapter(Context context,ArrayList<List_of_items> list_of_items ) {
        super(context,0,list_of_items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        List_of_items site = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView tvSite_link = (TextView) convertView.findViewById(R.id.site_link);
        TextView tvComment = (TextView) convertView.findViewById(R.id.comment);
        // Populate the data into the template view using the data object
        tvSite_link.setText(site.link_site);
        tvComment.setText(site.comment);
        // Return the completed view to render on screen
        return convertView;
    }
}
