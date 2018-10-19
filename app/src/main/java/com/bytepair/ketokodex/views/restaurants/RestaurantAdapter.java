package com.bytepair.ketokodex.views.restaurants;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * https://github.com/firebase/firebaseui-android
     *
     * @param options
     */
    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RestaurantHolder holder, int position, @NonNull Restaurant model) {
        holder.getRestaurantNameView().setText(model.getName());
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.restaurant_list_item, viewGroup, false);
        return new RestaurantHolder(view);
    }

    /**
     * Called each time there is a new query snapshot. You may want to use this method
     * to hide a loading spinner or check for the "no documents" state and update your UI.
     */
    @Override
    public void onDataChanged() {
    }

    /**
     * Called when there is an error getting a query snapshot. You may want to update
     * your UI to display an error message to the user.
     *
     * @param e
     */
    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
    }
}
