package com.bytepair.ketokodex.views.favorites;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Favorite;
import com.bytepair.ketokodex.views.food.FoodFragment;
import com.bytepair.ketokodex.views.interfaces.DataLoadingInterface;
import com.bytepair.ketokodex.views.interfaces.OnRecyclerViewClickListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bytepair.ketokodex.helpers.Constants.FAVORITES_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_ID;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_NAME;
import static com.bytepair.ketokodex.helpers.Constants.RESTAURANT_NAME;
import static com.bytepair.ketokodex.helpers.Constants.RESTAURANT_NAME_KEY;
import static com.bytepair.ketokodex.helpers.Constants.USER_ID_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment implements OnRecyclerViewClickListener, DataLoadingInterface {

    private ConstraintLayout mNoResultsLayout;
    private ConstraintLayout mLoadingLayout;
    private RecyclerView mRecyclerView;
    private FavoriteAdapter mAdapter;
    private Unbinder mUnbinder;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment depending on if user is signed in or not
        View view;

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            view = inflater.inflate(R.layout.fragment_favorites_please_sign_in, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_favorites, container, false);
            mUnbinder = ButterKnife.bind(this, view);
            mRecyclerView = view.findViewById(R.id.favorites_recycler_view);
            mNoResultsLayout = view.findViewById(R.id.no_results_include);
            mLoadingLayout = view.findViewById(R.id.loading_include);
            getFavorites();
        }

        hideFab();
        setUpToolbar();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    private void setUpToolbar() {
        if (getActivity() instanceof MainActivity) {
            final MainActivity mainActivity = ((MainActivity) getActivity());
            mainActivity.setActionBarTitle(getString(R.string.favorites));
            mainActivity.enableNavigationToggle();
        }
    }

    private void getFavorites() {
        showLoadingScreen();

        // build query
        Query query = FirebaseFirestore.getInstance()
                .collection(FAVORITES_KEY)
                .whereEqualTo(USER_ID_KEY, FirebaseAuth.getInstance().getUid())
                .orderBy(RESTAURANT_NAME_KEY);

        // configure adapter
        FirestoreRecyclerOptions<Favorite> options = new FirestoreRecyclerOptions.Builder<Favorite>()
                .setQuery(query, Favorite.class)
                .build();
        mAdapter = new FavoriteAdapter(options, this, getContext());
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
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onItemClick(int position, View view, String id) {
        Favorite favorite = mAdapter.getItem(position);
        Bundle data = new Bundle();
        data.putString(RESTAURANT_NAME, favorite.getRestaurantName());
        data.putString(FOOD_NAME, favorite.getName());
        data.putString(FOOD_ID, favorite.getFoodId());
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
