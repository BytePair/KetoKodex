package com.bytepair.ketokodex.views.food;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Favorite;
import com.bytepair.ketokodex.models.Food;
import com.bytepair.ketokodex.provider.FavoriteContract;
import com.bytepair.ketokodex.views.interfaces.DataLoadingInterface;
import com.bytepair.ketokodex.widget.FavoritesWidgetProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bytepair.ketokodex.helpers.Constants.CALORIES_KEY;
import static com.bytepair.ketokodex.helpers.Constants.CARBS_KEY;
import static com.bytepair.ketokodex.helpers.Constants.DESCRIPTION_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FAT_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FAVORITES_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_ID;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_ID_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_NAME;
import static com.bytepair.ketokodex.helpers.Constants.NAME_KEY;
import static com.bytepair.ketokodex.helpers.Constants.PROTEIN_KEY;
import static com.bytepair.ketokodex.helpers.Constants.RESTAURANT_NAME;
import static com.bytepair.ketokodex.helpers.Constants.RESTAURANT_NAME_KEY;
import static com.bytepair.ketokodex.helpers.Constants.USER_ID_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodFragment extends Fragment implements DataLoadingInterface {

    @BindView(R.id.food_details_constraint_layout)
    ConstraintLayout mFoodDetailsLayout;

    @BindView(R.id.no_results_include)
    ConstraintLayout mNoResultsLayout;

    @BindView(R.id.loading_include)
    ConstraintLayout mLoadingLayout;

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

    private FirestoreRecyclerAdapter mAdapter;
    private FloatingActionButton mFab;
    private Unbinder mUnbinder;
    private String mRestaurantName;
    private String mFoodName;
    private String mFoodId;
    private Favorite mFavorite;
    private Food mFood;

    public FoodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mRestaurantName = getArguments().getString(RESTAURANT_NAME);
            mFoodName = getArguments().getString(FOOD_NAME);
            mFoodId = getArguments().getString(FOOD_ID);
        }

        hideFab();
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
                .collection(FOOD_KEY)
                .document(mFoodId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mFood = task.getResult().toObject(Food.class);
                    mFood.setId(mFoodId);
                    bindFoodToView();
                    setUpFab();
                    showResults();
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

    private void setUpFab() {
        if (getActivity() == null || FirebaseAuth.getInstance().getUid() == null) {
            return;
        }

        FirebaseFirestore.getInstance()
                .collection(FAVORITES_KEY)
                .whereEqualTo(FOOD_ID_KEY, mFood.getId())
                .whereEqualTo(USER_ID_KEY, FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().getDocuments().size() == 0) {
                        mFavorite = null;
                        mFab.setImageResource(R.drawable.ic_favorite_border);
                        mFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addFavorite();
                            }
                        });
                    } else {
                        mFavorite = task.getResult().getDocuments().get(0).toObject(Favorite.class);
                        mFavorite.setId(task.getResult().getDocuments().get(0).getId());
                        mFab.setImageResource(R.drawable.ic_favorite);
                        mFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeFavorite();
                            }
                        });
                    }
                } else {
                    showErrorScreen();
                }
            }
        });

        mFab = getActivity().findViewById(R.id.fab);
        ((View) mFab).setVisibility(View.VISIBLE);
        mFab.setContentDescription(getString(R.string.food_favorite_fab_content_description));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFab.setColorFilter(getActivity().getColor(android.R.color.white));
        }
    }

    private void addFavorite() {
        // create object
        Map<String, Object> favoriteFood = new HashMap<>();
        favoriteFood.put(USER_ID_KEY, FirebaseAuth.getInstance().getUid());
        favoriteFood.put(FOOD_ID_KEY, mFood.getId());
        favoriteFood.put(NAME_KEY, mFood.getName());
        favoriteFood.put(DESCRIPTION_KEY, mFood.getName());
        favoriteFood.put(CALORIES_KEY, mFood.getCalories());
        favoriteFood.put(CARBS_KEY, mFood.getCarbs());
        favoriteFood.put(PROTEIN_KEY, mFood.getProtein());
        favoriteFood.put(FAT_KEY, mFood.getFat());
        favoriteFood.put(RESTAURANT_NAME_KEY, mRestaurantName);

        // save to favorites
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FAVORITES_KEY).add(favoriteFood)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        showSnackbar(getString(R.string.added_to_favorites));
                        addToDatabase(documentReference);
                        setUpFab();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackbar(e.getMessage());
                        setUpFab();
                    }
                });
    }

    private void addToDatabase(DocumentReference documentReference) {
        String id = documentReference.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteContract.FavoriteEntry.FAVORITE_ID, id);
        contentValues.put(FavoriteContract.FavoriteEntry.FAVORITE_NAME, mFood.getName());
        getContext().getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
        AppWidgetManager.getInstance(getContext()).notifyAppWidgetViewDataChanged(
                AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), FavoritesWidgetProvider.class)),
                R.id.widget_list_view
        );
    }

    private void removeFavorite() {
        // remove from favorites
        final String id = mFavorite.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FAVORITES_KEY).document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showSnackbar(getString(R.string.removed_from_favorites));
                        deleteFromDatabase(id);
                        setUpFab();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackbar(getString(R.string.error_removing_from_favorites));
                        setUpFab();
                    }
                });
    }

    private void deleteFromDatabase(String id) {
        getContext().getContentResolver().delete(FavoriteContract.FavoriteEntry.CONTENT_URI, "favoriteId=?", new String[]{id});
        AppWidgetManager.getInstance(getContext()).notifyAppWidgetViewDataChanged(
                AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), FavoritesWidgetProvider.class)),
                R.id.widget_list_view
        );
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
        mFoodDetailsLayout.setVisibility(View.GONE);
    }

    @Override
    public void showErrorScreen() {
        mLoadingLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.VISIBLE);
        mFoodDetailsLayout.setVisibility(View.GONE);
    }

    @Override
    public void showResults() {
        mLoadingLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.GONE);
        mFoodDetailsLayout.setVisibility(View.VISIBLE);
    }

    private void showSnackbar(String message) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showSnackbar(message);
        }
    }
}
