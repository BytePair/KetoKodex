package com.bytepair.ketokodex.views.restaurant;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.bytepair.ketokodex.models.Food;
import com.bytepair.ketokodex.views.food.FoodFragment;
import com.bytepair.ketokodex.views.interfaces.DataLoadingInterface;
import com.bytepair.ketokodex.views.interfaces.OnRecyclerViewClickListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.bytepair.ketokodex.helpers.Constants.CUSTOM_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_ID;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_NAME;
import static com.bytepair.ketokodex.helpers.Constants.NAME_KEY;
import static com.bytepair.ketokodex.helpers.Constants.RESTAURANT_KEY;
import static com.bytepair.ketokodex.helpers.Constants.USER_ID_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends Fragment implements DataLoadingInterface, OnRecyclerViewClickListener {

    public static final String RESTAURANT_NAME = "restaurant_name";
    public static final String RESTAURANT_ID = "restaurant_id";

    @BindView(R.id.restaurant_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_results_include)
    ConstraintLayout mNoResultsLayout;

    @BindView(R.id.loading_include)
    ConstraintLayout mLoadingLayout;

    RestaurantAdapter mAdapter;
    private Unbinder mUnbinder;
    private FloatingActionButton mFab;
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

        // get restaurant name and id for use in toolbar and map search
        if (getArguments() != null) {
            mRestaurantName = getArguments().getString(RESTAURANT_NAME);
            mRestaurantId = getArguments().getString(RESTAURANT_ID);
        }

        // if restaurant name is valid, set up FAB to launch google maps search
        if (mRestaurantName == null || mRestaurantName.equals("-Custom-")) {
            hideFab();
        } else {
            setUpFab();
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
        showLoadingScreen();

        // if no restaurant id was passed in, show the error screen
        if (mRestaurantId == null) {
            showErrorScreen();
            return;
        }

        // build query
        Query query;
        if (CUSTOM_KEY.equals(mRestaurantId)) {
            // user should always be signed in to see custom meals
            if (FirebaseAuth.getInstance().getUid() == null) {
                showErrorScreen();
                return;
            }
            query = getCustomFoodQuery();
        } else {
            query = getDefaultQuery();
        }

        // configure adapter
        FirestoreRecyclerOptions<Food> options = new FirestoreRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();
        mAdapter = new RestaurantAdapter(options, this);
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
        Food food = (Food) mAdapter.getItem(position);
        Timber.d( "Clicked %s", food.getName());
        Bundle data = new Bundle();
        data.putString(RESTAURANT_NAME, mRestaurantName);
        data.putString(FOOD_NAME, food.getName());
        data.putString(FOOD_ID, id);
        Fragment fragment = new FoodFragment();
        fragment.setArguments(data);
        if (getActivity() instanceof MainActivity) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.main_content, fragment).commit();
        }
    }

    private void hideFab() {
        if (getActivity() instanceof MainActivity) {
            View view = getActivity().findViewById(R.id.fab);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    private void setUpFab() {
        if (getActivity() == null) {
            return;
        }
        mFab = getActivity().findViewById(R.id.fab);
        ((View) mFab).setVisibility(View.VISIBLE);
        mFab.setImageResource(R.drawable.ic_baseline_map);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFab.setColorFilter(getActivity().getColor(android.R.color.white));
        }

        mFab.setContentDescription(getString(R.string.restaurant_map_fab_content_description));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Search for restaurants nearby
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + mRestaurantName);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private Query getCustomFoodQuery() {
        return FirebaseFirestore.getInstance()
                .collection(FOOD_KEY)
                .whereEqualTo(RESTAURANT_KEY, mRestaurantId)
                .whereEqualTo(USER_ID_KEY, FirebaseAuth.getInstance().getUid())
                .orderBy(NAME_KEY);
    }

    private Query getDefaultQuery() {
        return FirebaseFirestore.getInstance()
                .collection(FOOD_KEY)
                .whereEqualTo(RESTAURANT_KEY, mRestaurantId)
                .orderBy(NAME_KEY);
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
