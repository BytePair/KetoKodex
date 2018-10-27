package com.bytepair.ketokodex.views.favorites;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bytepair.ketokodex.R;

public class FavoriteHolder extends RecyclerView.ViewHolder {

    private TextView restaurantNameView;
    private TextView foodNameView;
    private CardView cardView;

    public FavoriteHolder(@NonNull View itemView) {
        super(itemView);

        restaurantNameView = itemView.findViewById(R.id.restaurant_name_text_view);
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
