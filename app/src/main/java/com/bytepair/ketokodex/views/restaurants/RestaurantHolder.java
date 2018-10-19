package com.bytepair.ketokodex.views.restaurants;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bytepair.ketokodex.R;

public class RestaurantHolder extends RecyclerView.ViewHolder {

    private TextView restaurantNameView;

    public RestaurantHolder(@NonNull View itemView) {
        super(itemView);
        restaurantNameView = itemView.findViewById(R.id.restaurant_name_text_view);
    }

    public TextView getRestaurantNameView() {
        return restaurantNameView;
    }
}
