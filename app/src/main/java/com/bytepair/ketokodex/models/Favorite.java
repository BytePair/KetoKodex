package com.bytepair.ketokodex.models;

import java.io.Serializable;

public class Favorite extends Food implements Serializable {

    private String restaurantName;
    private String foodId;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
}
