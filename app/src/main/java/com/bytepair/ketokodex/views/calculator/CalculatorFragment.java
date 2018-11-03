package com.bytepair.ketokodex.views.calculator;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalculatorFragment extends Fragment {

    @BindView(R.id.calculate_button)
    Button mCalculateButton;

    @BindView(R.id.activity_level_spinner)
    Spinner mActivityLevelSpinner;

    @BindView(R.id.age_edit_text)
    TextInputEditText mAgeEditText;

    @BindView(R.id.weight_edit_text)
    TextInputEditText mWeightEditText;

    @BindView(R.id.height_edit_text)
    TextInputEditText mHeightEditText;

    @BindView(R.id.gender_radio_group)
    RadioGroup mGenderRadioGroup;

    @BindView(R.id.weight_radio_group)
    RadioGroup mWeightRadioGroup;

    @BindView(R.id.height_radio_group)
    RadioGroup mHeightRadioGroup;

    @BindView(R.id.calories_text_view)
    TextView mCaloriesTextView;

    private static final String[] ACTIVITY_LEVELS = {
            "Basal Metabolic Rate (BMR)",
            "Sedentary (Little or no exercise)",
            "Lightly Active (1-3 workouts/week)",
            "Moderately Active (3-5 workouts/week)",
            "Very Active (6-7 workouts/week)",
            "Extra Active (Physical Job and 6-7 workouts/week)"};
    private Unbinder mUnbinder;

    public CalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        mUnbinder = ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity() != null) {
            mCalculateButton.getBackground().setColorFilter(getActivity().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            mCalculateButton.setTextColor(getActivity().getColor(android.R.color.white));
        }

        hideFab();
        setUpToolbar();
        setUpSpinner();

        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer calories = getCalories();
                mCaloriesTextView.setText(calories == null ? "" : String.valueOf(calories));
                mCaloriesTextView.setPaintFlags(mCaloriesTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        });

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
            mainActivity.setActionBarTitle(getString(R.string.calculator));
            mainActivity.enableNavigationToggle();
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

    private void setUpSpinner() {
        ArrayAdapter<String> spinnerAdapter
                = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, Arrays.asList(ACTIVITY_LEVELS));
        mActivityLevelSpinner.setAdapter(spinnerAdapter);
    }

    private Integer getCalories() {
        if (isInputValid()) {
            return calculateCalories();
        }
        return null;
    }

    private boolean isInputValid() {
        // validate age
        if (mAgeEditText.getText() == null || mAgeEditText.getText().toString().isEmpty()) {
            mAgeEditText.setError(getString(R.string.age_error_one));
            return false;
        }
        Integer age = Integer.valueOf(mAgeEditText.getText().toString());
        if (age < 1 || age > 120) {
            mAgeEditText.setError(getString(R.string.age_error_two));
            return false;
        }
        // validate gender

        // validate weight
        if (mWeightEditText.getText() == null || mWeightEditText.getText().toString().isEmpty()) {
            mWeightEditText.setError(getString(R.string.weight_error_one));
            return false;
        }
        Integer weight = Integer.valueOf(mWeightEditText.getText().toString());
        if (weight < 1) {
            mWeightEditText.setError(getString(R.string.weight_error_two));
            return false;
        }
        // validate lb/kilo

        // validate height
        if (mHeightEditText.getText() == null || mHeightEditText.getText().toString().isEmpty()) {
            mHeightEditText.setError(getString(R.string.height_error_one));
            return false;
        }
        Integer height = Integer.valueOf(mHeightEditText.getText().toString());
        if (height < 1) {
            mHeightEditText.setError(getString(R.string.height_error_two));
            return false;
        }
        // validate in/cm

        // validate activity level
        return true;
    }

    /**
     * Calculates calories using the Harris-Benedict Formula
     *
     *     1. Calculate your BMR (basal metabolic rate):
     *
     *         Women: BMR = 655 + ( 4.35 x weight in pounds ) + ( 4.7 x height in inches ) - ( 4.7 x age in years )
     *         Men: BMR = 66 + ( 6.23 x weight in pounds ) + ( 12.7 x height in inches ) - ( 6.8 x age in years )
     *
     *     2. Multiply your BMR by the appropriate activity factor, as follows:
     *
     *        Sedentary (little or no exercise): BMR x 1.2
     *        Lightly active (light exercise/sports 1-3 days/week): BMR x 1.375
     *        Moderately active (moderate exercise/sports 3-5 days/week): BMR x 1.55
     *        Very active (hard exercise/sports 6-7 days a week): BMR x 1.725
     *        Extra active (very hard exercise/sports & physical job or 2x training): BMR x 1.9
     */
    private Integer calculateCalories() {
        // get age
        Integer age = Integer.valueOf(Objects.requireNonNull(mAgeEditText.getText()).toString());

        // get gender
        String gender = ((RadioButton) getActivity().findViewById(mGenderRadioGroup.getCheckedRadioButtonId())).getText().toString();

        // calculate weight in lbs
        Double weight = Double.valueOf(Objects.requireNonNull(mWeightEditText.getText()).toString());
        String weightType = ((RadioButton) getActivity().findViewById(mWeightRadioGroup.getCheckedRadioButtonId())).getText().toString();
        if (weightType.equals("Kilos")) {
            weight /= 2.205;
        }

        // calculate height in inches
        Double height = Double.valueOf(Objects.requireNonNull(mHeightEditText.getText()).toString());
        String heightType = ((RadioButton) getActivity().findViewById(mHeightRadioGroup.getCheckedRadioButtonId())).getText().toString();
        if (heightType.equals("CM")) {
            weight /= 2.54;
        }

        // calculate bmr
        Double bmr;
        if (gender.equals("Male")) {
            bmr = 66 + (6.23 * weight) + (12.7 * height) - (6.8 * age);
        } else {
            bmr = 66 + (4.35 * weight) + (4.7 * height) - (4.7 * age);
        }

        // multiply by activity level
        switch (mActivityLevelSpinner.getSelectedItemPosition()) {
            case 1:
                return ((Double) (bmr * 1.2)).intValue();
            case 2:
                return ((Double) (bmr * 1.375)).intValue();
            case 3:
                return ((Double) (bmr * 1.55)).intValue();
            case 4:
                return ((Double) (bmr * 1.725)).intValue();
            case 5:
                return ((Double) (bmr * 1.9)).intValue();
            case 0:
            default:
                return bmr.intValue();
        }
    }
}
