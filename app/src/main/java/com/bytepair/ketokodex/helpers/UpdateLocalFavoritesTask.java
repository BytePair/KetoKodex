package com.bytepair.ketokodex.helpers;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.models.Favorite;
import com.bytepair.ketokodex.provider.FavoriteContract;
import com.bytepair.ketokodex.widget.FavoritesWidgetProvider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import timber.log.Timber;

public class UpdateLocalFavoritesTask extends AsyncTask<Task<QuerySnapshot>, Void, Void> {

    private Context mContext;

    public UpdateLocalFavoritesTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Task<QuerySnapshot>... tasks) {
        Timber.d("Starting update favorites task.");
        mContext.getContentResolver().delete(FavoriteContract.FavoriteEntry.CONTENT_URI, null, null);

        for (Task<QuerySnapshot> task : tasks) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    // add favorite to local db
                    Favorite favorite = documentSnapshot.toObject(Favorite.class);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FavoriteContract.FavoriteEntry.FAVORITE_ID, documentSnapshot.getId());
                    contentValues.put(FavoriteContract.FavoriteEntry.FAVORITE_NAME, favorite.getName());
                    mContext.getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Timber.d("Update favorites task complete.");
        updateWidgetUi();
    }

    private void updateWidgetUi() {
        Timber.d("Updating favorites widget.");
        AppWidgetManager.getInstance(mContext.getApplicationContext()).notifyAppWidgetViewDataChanged(
                AppWidgetManager.getInstance(mContext.getApplicationContext()).getAppWidgetIds(new ComponentName(mContext.getApplicationContext(), FavoritesWidgetProvider.class)),
                R.id.widget_list_view
        );
    }
}
