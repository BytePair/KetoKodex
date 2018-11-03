package com.bytepair.ketokodex;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bytepair.ketokodex.models.Favorite;
import com.bytepair.ketokodex.provider.FavoriteContract;
import com.bytepair.ketokodex.views.addfood.AddFoodFragment;
import com.bytepair.ketokodex.views.authentication.SignOutFragment;
import com.bytepair.ketokodex.views.authentication.SignUpFragment;
import com.bytepair.ketokodex.views.calculator.CalculatorFragment;
import com.bytepair.ketokodex.views.favorites.FavoritesFragment;
import com.bytepair.ketokodex.views.restaurants.RestaurantsFragment;
import com.bytepair.ketokodex.widget.FavoritesWidgetProvider;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

import static com.bytepair.ketokodex.helpers.Constants.FAVORITES_KEY;
import static com.bytepair.ketokodex.helpers.Constants.USER_ID_KEY;

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

        hideFab();
        setupGoogleAuth();
        enableNavigationToggle();

        mNavigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.nav_restaurants);
            onNavigationItemSelected(menuItem);
            menuItem.setChecked(true);
        }
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
        // getMenuInflater().inflate(R.menu.main, menu);
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
                fragmentClass = FavoritesFragment.class;
                Timber.i("clicked nav favorites...");
                break;
            case R.id.nav_add:
                fragmentClass = AddFoodFragment.class;
                Timber.i("clicked nav add food...");
                break;
            case R.id.nav_calculator:
                fragmentClass = CalculatorFragment.class;
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

    private void hideFab() {
        View view = findViewById(R.id.fab);
        if (view != null) {
            view.setVisibility(View.GONE);
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
        updateFavoritesWidget();
    }

    private void updateFavoritesWidget() {
        // delete existing favorites
        getContentResolver().delete(FavoriteContract.FavoriteEntry.CONTENT_URI, null, null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FAVORITES_KEY)
                .whereEqualTo(USER_ID_KEY, FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.w("favorite: task complete");
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                // add favorite to local db
                                Favorite favorite = documentSnapshot.toObject(Favorite.class);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(FavoriteContract.FavoriteEntry.FAVORITE_ID, documentSnapshot.getId());
                                contentValues.put(FavoriteContract.FavoriteEntry.FAVORITE_NAME, favorite.getName());
                                getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
                            }
                        }
                        // update the widget
                        AppWidgetManager.getInstance(getApplicationContext()).notifyAppWidgetViewDataChanged(
                                AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(new ComponentName(getApplicationContext(), FavoritesWidgetProvider.class)),
                                R.id.widget_list_view
                        );
                    }
                });
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
                showSnackbar(getString(R.string.google_sign_in_failed));
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                            refreshSignInTab();
                        } else {
                            // If sign in fails, display a message to the user.
                            showSnackbar(getString(R.string.auth_failed));
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
        showSnackbar(getString(R.string.signed_out));
    }

    public void emailSignUp(String email, String password) {
        if (email == null || email.length() < 1) {
            showSnackbar(getString(R.string.email_required));
            return;
        }
        if (password == null || password.length() < 1) {
            showSnackbar(getString(R.string.password_required));
            return;
        }
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            showSnackbar(getString(R.string.user_creation_success));
                            updateUI(user);
                            refreshSignInTab();
                        } else {
                            // If sign in fails, display a message to the user.
                            showSnackbar(task.getException().getMessage());
                            updateUI( null);
                        }
                    }
                });
    }

    public void emailSignIn(String email, String password) {
        if (email == null || email.length() < 1) {
            showSnackbar(getString(R.string.email_required));
            return;
        }
        if (password == null || password.length() < 1) {
            showSnackbar(getString(R.string.password_required));
            return;
        }
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            showSnackbar(getString(R.string.sign_in_success));
                            updateUI(user);
                            refreshSignInTab();
                        } else {
                            // If sign in fails, display a message to the user.
                            showSnackbar(task.getException().getMessage());
                            updateUI( null);
                        }
                    }
                });
    }

    public void showSnackbar(String message) {
        Snackbar.make(mDrawerLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
