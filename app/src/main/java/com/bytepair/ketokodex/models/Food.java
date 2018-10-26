package com.bytepair.ketokodex.models;

import java.io.Serializable;

public class Food implements Serializable {

    public static final String FOOD_KEY = "foods";
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    public static final String RESTAURANT_KEY = "restaurantId";
    public static final String USER_ID_KEY = "userId";
    public static final String CALORIES_KEY = "calories";
    public static final String CARBS_KEY = "carbs";
    public static final String PROTEIN_KEY = "protein";
    public static final String FAT_KEY = "fat";

    private String name;
    private String description;
    private String restaurantId;
    private String userId;
    private Integer calories;
    private Integer carbs;
    private Integer protein;
    private Integer fat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getCarbs() {
        return carbs;
    }

    public void setCarbs(Integer carbs) {
        this.carbs = carbs;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }
}
