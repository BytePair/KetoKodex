package com.bytepair.ketokodex.views.favorites;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Favorite;
import com.bytepair.ketokodex.views.interfaces.DataLoadingInterface;
import com.bytepair.ketokodex.views.interfaces.OnRecyclerViewClickListener;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FavoriteAdapter extends FirestoreRecyclerAdapter<Favorite, FavoriteHolder> {

    private OnRecyclerViewClickListener mOnRecyclerViewClickListener;
    private DataLoadingInterface mDataLoadingInterface;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * https://github.com/firebase/firebaseui-android
     *
     * @param options
     */
    public FavoriteAdapter(@NonNull FirestoreRecyclerOptions<Favorite> options) {
        super(options);
    }

    public FavoriteAdapter(@NonNull FirestoreRecyclerOptions<Favorite> options, DataLoadingInterface dataLoadingInterface) {
        super(options);
        this.mDataLoadingInterface = dataLoadingInterface;
    }

    @Override
    protected void onBindViewHolder(@NonNull final FavoriteHolder holder, int position, @NonNull Favorite model) {
        holder.getRestaurantNameView().setText(model.getRestaurantName());
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
    public FavoriteHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_list_item, viewGroup, false);
        return new FavoriteHolder(view);
    }

    /**
     * Called each time there is a new query snapshot. You may want to use this method
     * to hide a loading spinner or check for the "no documents" state and update your UI.
     */
    @Override
    public void onDataChanged() {
        if (getItemCount() < 1) {
            mDataLoadingInterface.showErrorScreen();
        } else {
            mDataLoadingInterface.showResults();
        }
    }

    /**
     * Called when there is an error getting a query snapshot. You may want to update
     * your UI to display an error message to the user.
     *
     * @param e
     */
    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        mDataLoadingInterface.showErrorScreen();
        super.onError(e);
    }

    public void setOnRecyclerViewClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener) {
        this.mOnRecyclerViewClickListener = onRecyclerViewClickListener;
    }
}
