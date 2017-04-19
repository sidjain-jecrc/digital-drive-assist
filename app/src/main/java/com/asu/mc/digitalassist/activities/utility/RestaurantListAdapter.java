package com.asu.mc.digitalassist.activities.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.activities.models.Restaurant;

import java.util.List;

/**
 * Created by Siddharth on 4/17/2017.
 */

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    private final Context context;
    private final List<Restaurant> restaurants;

    public RestaurantListAdapter(@NonNull Context context, @NonNull List<Restaurant> objects) {
        super(context, R.layout.restaurant, objects);
        this.context = context;
        this.restaurants = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.restaurant, parent, false);

        TextView nameTextView = (TextView) rowView.findViewById(R.id.restaurant_textName);
        TextView ratingTextView = (TextView) rowView.findViewById(R.id.restaurant_textRating);
        TextView categoryTextView = (TextView) rowView.findViewById(R.id.restaurant_textCategory);
        TextView phoneTextView = (TextView) rowView.findViewById(R.id.restaurant_textPhone);

        Restaurant res = getItem(position);
        if (res != null) {
            nameTextView.setText(res.getName());
            ratingTextView.setText(res.getRating());
            categoryTextView.setText(res.getCategory());
            phoneTextView.setText(res.getPhone());
        }
        return rowView;
    }
}
