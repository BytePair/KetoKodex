package com.bytepair.ketokodex.views.restaurant;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bytepair.ketokodex.R;

public class RestaurantHolder extends RecyclerView.ViewHolder {

    private TextView foodNameView;
    private CardView cardView;

    public RestaurantHolder(@NonNull View itemView) {
        super(itemView);
        foodNameView = itemView.findViewById(R.id.food_name_text_view);
        cardView = itemView.findViewById(R.id.food_card_view);
    }

    public TextView getFoodNameView() {
        return foodNameView;
    }

    public CardView getCardView() {
        return cardView;
    }
}
