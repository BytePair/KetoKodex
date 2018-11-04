package com.bytepair.ketokodex.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bytepair.ketokodex.MainActivity;
import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.provider.FavoriteContract;
import com.bytepair.ketokodex.views.favorites.FavoritesFragment;

import static com.bytepair.ketokodex.MainActivity.TAB_KEY;

public class FavoritesWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    public FavoritesWidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void onDataSetChanged() {
        Uri FAVORITES_URI = FavoriteContract.BASE_CONTENT_URI.buildUpon().appendPath(FavoriteContract.PATH_FAVORITES).build();
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(
                FAVORITES_URI,
                null,
                null,
                null,
                FavoriteContract.FavoriteEntry.FAVORITE_NAME
        );
    }

    @Override
    public int getCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        if (mCursor == null || mCursor.getCount() == 0) {
            return null;
        }

        mCursor.moveToPosition(i);
        int name = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.FAVORITE_NAME);

        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        row.setTextViewText(R.id.name, mCursor.getString(name));

        Intent favIntent = new Intent(mContext, MainActivity.class);
        favIntent.putExtra(TAB_KEY, FavoritesFragment.class.getSimpleName());
        favIntent.setAction(Long.toString(System.currentTimeMillis()));
        row.setOnClickFillInIntent(R.id.name, favIntent);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
