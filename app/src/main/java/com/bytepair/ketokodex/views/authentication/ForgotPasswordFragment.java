package com.bytepair.ketokodex.views.authentication;


import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {

    @BindView(R.id.reset_password_button)
    Button mResetPasswordButton;

    @BindView(R.id.email_input_edit_text)
    TextInputEditText mEmailInputEditText;

    private Unbinder mUnbinder;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        mUnbinder = ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity() != null) {
            mResetPasswordButton.getBackground().setColorFilter(getActivity().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            mResetPasswordButton.setTextColor(getActivity().getColor(android.R.color.white));
        }

        hideFab();
        setUpToolbar();

        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPasswordReset();
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
            mainActivity.setActionBarTitle(getString(R.string.reset_password));
            mainActivity.enableBackNavigation();
        }
    }

    void sendPasswordReset() {
        if (mEmailInputEditText != null && mEmailInputEditText.getText() != null && mEmailInputEditText.getText().toString().length() > 0) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(mEmailInputEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Timber.i(task.getException());
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(R.string.password_reset_title);
                            builder.setMessage(R.string.password_reset_message);
                            builder.setPositiveButton(R.string.caps_ok, null);
                            builder.create().show();
                        }
                    });
        } else {
            Snackbar.make(getView(), R.string.password_reset_blank_email_message, Snackbar.LENGTH_SHORT).show();
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
