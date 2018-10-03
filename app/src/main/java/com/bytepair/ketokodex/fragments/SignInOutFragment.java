package com.bytepair.ketokodex.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInOutFragment extends Fragment {

    @BindView(R.id.step_one_sign_in_button)
    Button signInButton;

    @BindView(R.id.step_one_sign_up_button)
    Button signUpButton;

    private Unbinder mUnbinder;

    public SignInOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in_out, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setUpToolbar();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignUp();
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
            mainActivity.setActionBarTitle(getString(R.string.sign_in));
            mainActivity.enableNavigationToggle();
        }
    }

    void showSignIn() {
        Timber.i("sign in clicked");
        try {
            MainActivity mainActivity = ((MainActivity) getActivity());
            if (mainActivity != null) {
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(SignInOutFragment.class.getSimpleName()).replace(R.id.main_content, SignInFragment.class.newInstance()).commit();
            }
        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            Timber.e(e);
        }
    }

    void showSignUp() {
        Timber.i("sign up clicked");
    }
}
