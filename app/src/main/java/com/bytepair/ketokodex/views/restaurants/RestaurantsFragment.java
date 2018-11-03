package com.bytepair.ketokodex.views.restaurants;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Restaurant;
import com.bytepair.ketokodex.views.interfaces.DataLoadingInterface;
import com.bytepair.ketokodex.views.interfaces.OnRecyclerViewClickListener;
import com.bytepair.ketokodex.views.restaurant.RestaurantFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.bytepair.ketokodex.views.restaurant.RestaurantFragment.RESTAURANT_ID;
import static com.bytepair.ketokodex.views.restaurant.RestaurantFragment.RESTAURANT_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantsFragment extends Fragment implements OnRecyclerViewClickListener, DataLoadingInterface {

    @BindView(R.id.restaurant_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_results_include)
    ConstraintLayout mNoResultsLayout;

    @BindView(R.id.loading_include)
    ConstraintLayout mLoadingLayout;

    private RestaurantsAdapter mAdapter;
    private Unbinder mUnbinder;

    public RestaurantsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        hideFab();
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
        showLoadingScreen();

        // build query
        Query query;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            query = getGeneralQuery();
        } else {
            query = getUserSpecificQuery();
        }

        // configure adapter
        FirestoreRecyclerOptions<Restaurant> options = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class)
                .build();
        mAdapter = new RestaurantsAdapter(options, this);
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
    public void onItemClick(int position, View view, String id) {
        Restaurant restaurant = mAdapter.getItem(position);
        Timber.d( "Clicked %s", restaurant.getName());
        Bundle data = new Bundle();
        data.putString(RESTAURANT_NAME, restaurant.getName());
        data.putString(RESTAURANT_ID, id);
        Fragment fragment = new RestaurantFragment();
        fragment.setArguments(data);
        if (getActivity() instanceof MainActivity) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.main_content, fragment).commit();
        }
    }

    private Query getUserSpecificQuery() {
        return FirebaseFirestore.getInstance()
                .collection("restaurants")
                .orderBy("name");
    }

    private Query getGeneralQuery() {
        return FirebaseFirestore.getInstance()
                .collection("restaurants")
                .orderBy("name")
                .startAfter("-Custom-");
    }

    private void hideFab() {
        if (getActivity() instanceof MainActivity) {
            View view = getActivity().findViewById(R.id.fab);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showLoadingScreen() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mNoResultsLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showErrorScreen() {
        mLoadingLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showResults() {
        mLoadingLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
