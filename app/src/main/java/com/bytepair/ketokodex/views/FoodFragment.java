package com.bytepair.ketokodex.views;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Food;
import com.bytepair.ketokodex.views.interfaces.DataLoadingInterface;
import com.bytepair.ketokodex.views.restaurant.RestaurantAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodFragment extends Fragment implements DataLoadingInterface {

    public static final String FOOD_NAME = "food_name";
    public static final String FOOD_ID = "food_id";

    @BindView(R.id.food_name_text_view)
    TextView mFoodNameTextView;

    @BindView(R.id.calories_text_view)
    TextView mCaloriesTextView;

    @BindView(R.id.carbs_text_view)
    TextView mCarbsTextView;

    @BindView(R.id.protein_text_view)
    TextView mProteinTextView;

    @BindView(R.id.fat_text_view)
    TextView mFatTextView;

    FirestoreRecyclerAdapter mAdapter;
    private Unbinder mUnbinder;
    private String mFoodName;
    private String mFoodId;
    private Food mFood;

    public FoodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mFoodName = getArguments().getString(FOOD_NAME);
            mFoodId = getArguments().getString(FOOD_ID);
        }

        setUpToolbar();
        getFood();

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
            mainActivity.setActionBarTitle((mFoodName == null) ? getString(R.string.food) : mFoodName);
            mainActivity.enableBackNavigation();
        }
    }

    private void getFood() {
        // show loading screen
        showLoadingScreen();

        // if no restaurant id was passed in, show the error screen
        if (mFoodId == null) {
            showErrorScreen();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("foods")
                .document(mFoodId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Timber.d("Food found: %s", task.getResult().toString());
                    mFood = task.getResult().toObject(Food.class);
                    bindFoodToView();
                } else {
                    showErrorScreen();
                }
            }
        });
    }

    private void bindFoodToView() {
        if (mFood == null) {
            showErrorScreen();
            return;
        }
        if (mFoodNameTextView != null) mFoodNameTextView.setText((mFood.getName() == null) ? getString(R.string.unknown) : mFood.getName());
        if (mCaloriesTextView != null) mCaloriesTextView.setText((mFood.getCalories() == null) ? getString(R.string.unknown) : mFood.getCalories().toString());
        if (mCarbsTextView != null) mCarbsTextView.setText((mFood.getCarbs() == null) ? getString(R.string.unknown) : mFood.getCarbs().toString());
        if (mProteinTextView != null) mProteinTextView.setText((mFood.getProtein() == null) ? getString(R.string.unknown) : mFood.getProtein().toString());
        if (mFatTextView != null) mFatTextView.setText((mFood.getFat() == null) ? getString(R.string.unknown) : mFood.getFat().toString());
        showResults();
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
