package com.bytepair.ketokodex.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignOutFragment extends Fragment {

    @BindView(R.id.sign_out_button)
    Button mSignOutButton;

    @BindView(R.id.email_text_view)
    TextView mEmailTextView;

    private Unbinder mUnbinder;

    public SignOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_out, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setUpToolbar();
        setUserDetails();

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
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
            mainActivity.setActionBarTitle(getString(R.string.sign_out));
            mainActivity.enableNavigationToggle();
        }
    }

    private void setUserDetails() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            mEmailTextView.setText(account.getEmail());
        } else {
            mEmailTextView.setText("No email address found");
        }
    }

    private void signOut() {
        Timber.i("sign out clicked");
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).googleSignOut();
        }
    }

}
