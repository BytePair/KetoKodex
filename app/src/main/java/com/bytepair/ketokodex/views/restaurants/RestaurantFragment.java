package com.bytepair.ketokodex.views.restaurants;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends Fragment {

    @BindView(R.id.restaurant_recycler_view)
    RecyclerView mRecyclerView;

    FirestoreRecyclerAdapter mAdapter;
    private Unbinder mUnbinder;

    public RestaurantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setUpToolbar();
        getRestaurants();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void setUpToolbar() {
        if (getActivity() instanceof MainActivity) {
            final MainActivity mainActivity = ((MainActivity) getActivity());
            mainActivity.setActionBarTitle(getString(R.string.restaurants));
            mainActivity.enableNavigationToggle();
        }
    }

    private void getRestaurants() {

        // build query
        Query query = FirebaseFirestore.getInstance()
                .collection("restaurants")
                .orderBy("name")
                .limit(50);

        // configure adapter
        FirestoreRecyclerOptions<Restaurant> options = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class)
                .build();
        mAdapter = new RestaurantAdapter(options);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        // set adapter on recycler view
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
