package com.bytepair.ketokodex.views.restaurant;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Food;
import com.bytepair.ketokodex.models.Restaurant;
import com.bytepair.ketokodex.views.interfaces.OnRecyclerViewClickListener;
import com.bytepair.ketokodex.views.restaurants.RestaurantsHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<Food, RestaurantHolder> {

    private OnRecyclerViewClickListener mOnRecyclerViewClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * https://github.com/firebase/firebaseui-android
     *
     * @param options
     */
    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<Food> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RestaurantHolder holder, int position, @NonNull Food model) {
        holder.getFoodNameView().setText(model.getName());
        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnRecyclerViewClickListener != null) {
                    mOnRecyclerViewClickListener.onItemClick(holder.getAdapterPosition(), view, getSnapshots().getSnapshot(holder.getAdapterPosition()).getId());
                }
            }
        });
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_list_item, viewGroup, false);
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

    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener) {
        this.mOnRecyclerViewClickListener = onRecyclerViewClickListener;
    }
}
