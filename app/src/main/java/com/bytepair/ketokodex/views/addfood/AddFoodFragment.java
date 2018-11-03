package com.bytepair.ketokodex.views.addfood;


import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bytepair.ketokodex.helpers.Constants.CALORIES_KEY;
import static com.bytepair.ketokodex.helpers.Constants.CARBS_KEY;
import static com.bytepair.ketokodex.helpers.Constants.CUSTOM_KEY;
import static com.bytepair.ketokodex.helpers.Constants.DESCRIPTION_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FAT_KEY;
import static com.bytepair.ketokodex.helpers.Constants.FOOD_KEY;
import static com.bytepair.ketokodex.helpers.Constants.NAME_KEY;
import static com.bytepair.ketokodex.helpers.Constants.PROTEIN_KEY;
import static com.bytepair.ketokodex.helpers.Constants.RESTAURANT_KEY;
import static com.bytepair.ketokodex.helpers.Constants.USER_ID_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFoodFragment extends Fragment {

    private Unbinder mUnbinder;
    private Button mAddFoodButton;
    private FloatingActionButton mFab;
    private TextInputEditText mFoodNameEditText;
    private TextInputEditText mCaloriesEditText;
    private TextInputEditText mCarbsEditText;
    private TextInputEditText mProteinEditText;
    private TextInputEditText mFatEditText;
    private ConstraintLayout mProgressLayout;
    private ConstraintLayout mAddFoodLayout;

    public AddFoodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment depending on if user is signed in or not
        View view;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            view = inflater.inflate(R.layout.fragment_add_food_please_sign_in, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_add_food, container, false);
            bindViews(view);
            setUpButton();
        }
        hideFab();
        mUnbinder = ButterKnife.bind(this, view);

        setUpToolbar();

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

    private void setUpButton() {
        if (getActivity() == null) {
            return;
        }
        mAddFoodButton.setOnClickListener(new View.OnClickListener() {
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
                    mFoodNameEditText.setError(getString(R.string.valid_food_error));
                    return;
                }
                if (calories == null)  {
                    mCaloriesEditText.setError(getString(R.string.valid_calorie_error));
                    return;
                }
                if (carbs == null) {
                    mCarbsEditText.setError(getString(R.string.valid_carb_error));
                    return;
                }
                if (protein == null) {
                    mProteinEditText.setError(getString(R.string.valid_protein_error));
                    return;
                }
                if (fat == null) {
                    mFatEditText.setError(getString(R.string.valid_fat_error));
                    return;
                }

                // create object
                Map<String, Object> customFood = new HashMap<>();
                customFood.put(RESTAURANT_KEY, CUSTOM_KEY);
                customFood.put(USER_ID_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                customFood.put(NAME_KEY, foodName);
                customFood.put(DESCRIPTION_KEY, foodName);
                customFood.put(CALORIES_KEY, calories);
                customFood.put(CARBS_KEY, carbs);
                customFood.put(PROTEIN_KEY, protein);
                customFood.put(FAT_KEY, fat);

                // save to firestore db
                saveFood(customFood);
            }
        });
    }

    private void saveFood(Map<String,Object> customFood) {
        showProgressbar();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FOOD_KEY).add(customFood)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        hideProgressbar();
                        showSnackbar(getString(R.string.food_added_successfully));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressbar();
                        showSnackbar(e.getMessage());
                    }
                });
    }

    private void showSnackbar(String message) {
        Snackbar.make(mFoodNameEditText, message, Snackbar.LENGTH_SHORT).show();
    }

    private void bindViews(View view) {
        // bind views
        mProgressLayout = view.findViewById(R.id.progress_constraint_layout);
        mAddFoodLayout = view.findViewById(R.id.add_food_constraint_layout);
        mFoodNameEditText = view.findViewById(R.id.food_name_edit_text);
        mCaloriesEditText = view.findViewById(R.id.calories_edit_text);
        mProteinEditText = view.findViewById(R.id.protein_edit_text);
        mCarbsEditText = view.findViewById(R.id.carbs_edit_text);
        mAddFoodButton = view.findViewById(R.id.add_food_button);
        mFatEditText = view.findViewById(R.id.fat_edit_text);

        hideProgressbar();

        // set button to correct color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity() != null) {
            mAddFoodButton.getBackground().setColorFilter(getActivity().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            mAddFoodButton.setTextColor(getActivity().getColor(android.R.color.white));
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

    private void hideProgressbar() {
        mAddFoodLayout.setVisibility(View.VISIBLE);
        mProgressLayout.setVisibility(View.GONE);
    }

    private void showProgressbar() {
        mAddFoodLayout.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.VISIBLE);
    }
}
