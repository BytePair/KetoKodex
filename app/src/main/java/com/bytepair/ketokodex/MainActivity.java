package com.bytepair.ketokodex;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bytepair.ketokodex.views.authentication.SignOutFragment;
import com.bytepair.ketokodex.views.authentication.SignUpFragment;
import com.bytepair.ketokodex.views.restaurants.RestaurantsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;
    private Class mAuthFragmentClass;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        }

        setSupportActionBar(mToolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setupGoogleAuth();
        enableNavigationToggle();

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Class fragmentClass = null;
        switch (id) {
            case R.id.nav_restaurants:
                fragmentClass = RestaurantsFragment.class;
                Timber.i("clicked nav restaurants...");
                break;
            case R.id.nav_favorites:
                Timber.i("clicked nav favorites...");
                break;
            case R.id.nav_map:
                Timber.i("clicked nav map...");
                break;
            case R.id.nav_add:
                Timber.i("clicked nav add...");
                break;
            case R.id.nav_calculator:
                Timber.i("clicked nav calculator...");
                break;
            case R.id.nav_sign_up:
                fragmentClass = mAuthFragmentClass;
                Timber.i("clicked nav sign in/out...");
                break;
            default:
                Timber.i("default map...");
        }

        swapFragment(fragmentClass);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void swapFragment(Class fragmentClass) {
        Fragment fragment = null;

        // get instance of the fragment
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            Timber.e(e);
        }

        // swap fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
    }

    /**
     * Used to show back button and enable the user to navigate between fragments
     */
    public void enableBackNavigation() {
        // show the back button
        showBackButton();
        // lock navigation drawer so it can't be swiped open
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        // add listener for the now displayed back button
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    private void showBackButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Used to show navigation menu toggle icon
     * Can be called from fragments to help with navigation
     */
    public void enableNavigationToggle() {
        hideBackButton();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void hideBackButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * Used by fragments to set the title
     *
     * @param title New title on action bar
     */
    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupGoogleAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Updates the sign in/out UI. Will set the title text in the navigation drawer and change
     * the fragment that is launched when selected
     *
     * @param account   FirebaseUser (username/password)
     */
    public void updateUI(FirebaseUser account) {
        if (account == null) {
            mNavigationView.getMenu().findItem(R.id.nav_sign_up).setTitle(R.string.sign_up);
            mAuthFragmentClass = SignUpFragment.class;
        } else {
            mNavigationView.getMenu().findItem(R.id.nav_sign_up).setTitle(R.string.sign_out);
            mAuthFragmentClass = SignOutFragment.class;
        }
    }

    public void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Snackbar.make(mDrawerLayout, "Google sign in failed", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                            refreshSignInTab();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(mDrawerLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void refreshSignInTab() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        }
        onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_sign_up));
    }

    public void signOut() {
        // Firebase sign out
        mFirebaseAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut();

        updateUI(null);
        refreshSignInTab();
        Snackbar.make(mDrawerLayout, "Signed Out", Snackbar.LENGTH_SHORT).show();
    }

    public void emailSignUp(String email, String password) {
        if (email == null || email.length() < 1) {
            Snackbar.make(mDrawerLayout, "Email address required", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (password == null || password.length() < 1) {
            Snackbar.make(mDrawerLayout, "Password required", Snackbar.LENGTH_LONG).show();
            return;
        }
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("createUserWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Snackbar.make(mNavigationView, "User created successfully", Snackbar.LENGTH_LONG).show();
                            updateUI(user);
                            refreshSignInTab();
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.w(task.getException(), "createUserWithEmail:failure");
                            Snackbar.make(mDrawerLayout, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                            updateUI( null);
                        }
                    }
                });
    }

    public void emailSignIn(String email, String password) {
        if (email == null || email.length() < 1) {
            Snackbar.make(mDrawerLayout, "Email address required", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (password == null || password.length() < 1) {
            Snackbar.make(mDrawerLayout, "Password required", Snackbar.LENGTH_LONG).show();
            return;
        }
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("createUserWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Snackbar.make(mNavigationView, "Signed In Successfully", Snackbar.LENGTH_LONG).show();
                            updateUI(user);
                            refreshSignInTab();
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.w(task.getException(), "createUserWithEmail:failure");
                            Snackbar.make(mDrawerLayout, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                            updateUI( null);
                        }
                    }
                });
    }
}
