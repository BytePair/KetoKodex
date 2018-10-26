package com.bytepair.ketokodex.views.addfood;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Food;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bytepair.ketokodex.models.Food.FOOD_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFoodFragment extends Fragment {

    private static final String CUSTOM_KEY = "7mYtbPQ5fjMEccqHvmJn";
    private Unbinder mUnbinder;
    private FloatingActionButton mFab;

    @BindView(R.id.food_name_edit_text)
    TextInputEditText mFoodNameEditText;

    @BindView(R.id.calories_edit_text)
    TextInputEditText mCaloriesEditText;

    @BindView(R.id.carbs_edit_text)
    TextInputEditText mCarbsEditText;

    @BindView(R.id.protein_edit_text)
    TextInputEditText mProteinEditText;

    @BindView(R.id.fat_edit_text)
    TextInputEditText mFatEditText;

    public AddFoodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment depending on if user is signed in or not
        View view;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            view = inflater.inflate(R.layout.fragment_please_sign_in, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_add_food, container, false);
        }
        mUnbinder = ButterKnife.bind(this, view);

        setUpToolbar();
        setUpFab();

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
            mainActivity.setActionBarTitle(getString(R.string.addCustomFood));
            mainActivity.enableNavigationToggle();
        }
    }

    private void setUpFab() {
        if (getActivity() == null) {
            return;
        }
        mFab = getActivity().findViewById(R.id.fab);
        mFab.setImageResource(R.drawable.ic_baseline_add);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFab.setColorFilter(getActivity().getColor(android.R.color.white));
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get input from ui
                final String foodName = (TextUtils.isEmpty(mFoodNameEditText.getText()) ? null : mFoodNameEditText.getText().toString());
                final Integer calories = (TextUtils.isEmpty(mCaloriesEditText.getText())) ? null : Integer.valueOf(mCaloriesEditText.getText().toString());
                final Integer carbs = (TextUtils.isEmpty(mCarbsEditText.getText()) ? null : Integer.valueOf(mCarbsEditText.getText().toString()));
                final Integer protein = (TextUtils.isEmpty(mProteinEditText.getText()) ? null : Integer.valueOf(mProteinEditText.getText().toString()));
                final Integer fat = (TextUtils.isEmpty(mFatEditText.getText()) ? null : Integer.valueOf(mFatEditText.getText().toString()));

                // verify values were entered
                if (foodName == null) {
                    showSnackbar("Please enter a valid food name");
                    return;
                }
                if (calories == null)  {
                    showSnackbar("Please enter a valid calorie count");
                    return;
                }
                if (carbs == null) {
                    showSnackbar("Please enter a valid carb count");
                    return;
                }
                if (protein == null) {
                    showSnackbar("Please enter a valid protein count");
                    return;
                }
                if (fat == null) {
                    showSnackbar("Please enter a valid fat count");
                    return;
                }

                // create object
                Map<String, Object> customFood = new HashMap<>();
                customFood.put(Food.RESTAURANT_KEY, CUSTOM_KEY);
                customFood.put(Food.USER_ID_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                customFood.put(Food.NAME_KEY, foodName);
                customFood.put(Food.DESCRIPTION_KEY, foodName);
                customFood.put(Food.CALORIES_KEY, calories);
                customFood.put(Food.CARBS_KEY, carbs);
                customFood.put(Food.PROTEIN_KEY, protein);
                customFood.put(Food.FAT_KEY, fat);

                // save to firestore db
                saveFood(customFood);
            }
        });
    }

    private void saveFood(Map<String,Object> customFood) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FOOD_KEY).add(customFood)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        showSnackbar("Food added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackbar(e.getMessage());
                    }
                });
    }

    private void showSnackbar(String message) {
        Snackbar.make(mFoodNameEditText, message, Snackbar.LENGTH_SHORT).show();
    }
}
