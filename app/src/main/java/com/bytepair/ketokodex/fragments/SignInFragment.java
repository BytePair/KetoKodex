package com.bytepair.ketokodex.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    @BindView(R.id.sign_in_button)
    Button mSignInButton;

    @BindView(R.id.google_register_button)
    SignInButton mGoogleRegisterButton;

    @BindView(R.id.forgot_password_link)
    TextView mForgotPasswordLink;

    private Unbinder mUnbinder;


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setUpToolbar();
        setUpGoogleButton();

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        mGoogleRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
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
            mainActivity.enableBackNavigation();
        }
    }

    private void setUpGoogleButton() {
        mGoogleRegisterButton.setSize(SignInButton.SIZE_WIDE);
    }

    private void signIn() {
        Timber.i("sign in clicked");
    }

    private void googleSignIn() {
        Timber.i("google sign in clicked");
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).googleSignIn();
        }
    }

    private void forgotPassword() {
        Timber.i("forgot password clicked");
    }
}
