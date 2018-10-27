package com.bytepair.ketokodex.views.authentication;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.google.android.gms.common.SignInButton;

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

    @BindView(R.id.sign_in_email_input_edit_text)
    TextInputEditText mEmailInput;

    @BindView(R.id.sign_in_password_input_edit_text)
    TextInputEditText mPasswordInput;

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

        hideFab();
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

    void forgotPassword() {
        Timber.i("forgot password clicked");
        try {
            MainActivity mainActivity = ((MainActivity) getActivity());
            if (mainActivity != null) {
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.main_content, ForgotPasswordFragment.class.newInstance()).commit();
            }
        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            Timber.e(e);
        }
    }

    private void signIn() {
        Timber.i("sign in clicked");
        Timber.i("sign up clicked");
        String email = (mEmailInput.getText() == null) ? null : mEmailInput.getText().toString();
        String password = (mPasswordInput.getText() == null) ? null : mPasswordInput.getText().toString();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).emailSignIn(email, password);
        }
    }

    private void googleSignIn() {
        Timber.i("google sign in clicked");
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).googleSignIn();
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

}
