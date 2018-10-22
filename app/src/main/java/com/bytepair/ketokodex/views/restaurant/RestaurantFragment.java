package com.bytepair.ketokodex.views.restaurant;


import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Food;
import com.bytepair.ketokodex.views.FoodFragment;
import com.bytepair.ketokodex.views.interfaces.OnRecyclerViewClickListener;
import com.bytepair.ketokodex.views.interfaces.DataLoadingInterface;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.bytepair.ketokodex.views.FoodFragment.FOOD_ID;
import static com.bytepair.ketokodex.views.FoodFragment.FOOD_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends Fragment implements DataLoadingInterface, OnRecyclerViewClickListener {

    public static final String RESTAURANT_NAME = "restaurant_name";
    public static final String RESTAURANT_ID = "restaurant_id";

    @BindView(R.id.restaurant_recycler_view)
    RecyclerView mRecyclerView;

    RestaurantAdapter mAdapter;
    private Unbinder mUnbinder;
    private String mRestaurantName;
    private String mRestaurantId;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mRestaurantName = getArguments().getString(RESTAURANT_NAME);
            mRestaurantId = getArguments().getString(RESTAURANT_ID);
        }

        setUpToolbar();
        getMenu();

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
            mainActivity.setActionBarTitle((mRestaurantName == null) ? getString(R.string.menu) : mRestaurantName);
            mainActivity.enableBackNavigation();
        }
    }

    private void getMenu() {

        // if no restaurant id was passed in, show the error screen
        if (mRestaurantId == null) {
            showErrorScreen();
            return;
        }

        // build query
        Query query = FirebaseFirestore.getInstance()
                .collection("foods")
                .whereEqualTo("restaurantId", mRestaurantId)
                .orderBy("name");

        // configure adapter
        FirestoreRecyclerOptions<Food> options = new FirestoreRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();
        mAdapter = new RestaurantAdapter(options);
        mAdapter.setOnRecyclerViewClickListener(this);

        // set layout manager on recycler view
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

    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    @Override
    public void onItemClick(int position, View view, String id) {
        Food food = (Food) mAdapter.getItem(position);
        Timber.d( "Clicked %s", food.getName());
        Bundle data = new Bundle();
        data.putString(FOOD_NAME, food.getName());
        data.putString(FOOD_ID, id);
        Fragment fragment = new FoodFragment();
        fragment.setArguments(data);
        if (getActivity() instanceof MainActivity) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.main_content, fragment).commit();
        }
    }

    @Override
    public void showLoadingScreen() {

    }

    @Override
    public void showErrorScreen() {

    }

    @Override
    public void showResults() {

    }
}
