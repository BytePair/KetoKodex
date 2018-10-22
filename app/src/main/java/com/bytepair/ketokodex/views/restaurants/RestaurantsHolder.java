package com.bytepair.ketokodex.views.restaurants;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Restaurant;

public class RestaurantsHolder extends RecyclerView.ViewHolder {

    private TextView restaurantNameView;
    private CardView cardView;

    public RestaurantsHolder(@NonNull View itemView) {
        super(itemView);
        restaurantNameView = itemView.findViewById(R.id.restaurant_name_text_view);
        cardView = itemView.findViewById(R.id.restaurant_card_view);
    }

    public TextView getRestaurantNameView() {
        return restaurantNameView;
    }

    public CardView getCardView() {
        return cardView;
    }
}
