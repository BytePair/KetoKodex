package com.bytepair.ketokodex.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    @BindView(R.id.register_button)
    Button mRegisterButton;

    @BindView(R.id.google_register_button)
    SignInButton mGoogleRegisterButton;

    @BindView(R.id.sign_in_link)
    TextView mSignInLink;

    @BindView(R.id.sign_up_username_input_edit_text)
    TextInputEditText mEmailInput;

    @BindView(R.id.sign_up_password_input_edit_text)
    TextInputEditText mPasswordInput;

    private GoogleSignInClient mGoogleSignInClient;
    private Unbinder mUnbinder;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setUpToolbar();
        setUpGoogleButton();

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        mGoogleRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignUp();
            }
        });

        mSignInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignIn();
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
            mainActivity.setActionBarTitle(getString(R.string.sign_up));
            mainActivity.enableNavigationToggle();
        }
    }

    private void setUpGoogleButton() {
        mGoogleRegisterButton.setSize(SignInButton.SIZE_WIDE);
    }

    private void showSignIn() {
        Timber.i("sign in clicked");
        try {
            MainActivity mainActivity = ((MainActivity) getActivity());
            if (mainActivity != null) {
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(SignUpFragment.class.getSimpleName()).replace(R.id.main_content, SignInFragment.class.newInstance()).commit();
            }
        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            Timber.e(e);
        }
    }

    private void signUp() {
        Timber.i("sign up clicked");
        String email = (mEmailInput.getText() == null) ? null : mEmailInput.getText().toString();
        String password = (mPasswordInput.getText() == null) ? null : mPasswordInput.getText().toString();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).emailSignUp(email, password);
        }
    }

    private void googleSignUp() {
        Timber.i("google sign in clicked");
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).googleSignIn();
        }
    }
}
