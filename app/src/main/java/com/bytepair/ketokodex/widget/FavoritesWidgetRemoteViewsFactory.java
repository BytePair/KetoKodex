package com.bytepair.ketokodex.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bytepair.ketokodex.R;
import com.bytepair.ketokodex.provider.FavoriteContract;

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
        int id = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.FAVORITE_ID);

        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        row.setTextViewText(R.id.name, mCursor.getString(name));

        Intent intent = new Intent();
        Bundle extras = new Bundle();

        extras.putString("id", mCursor.getString(id));
        intent.putExtras(extras);
        row.setOnClickFillInIntent(R.id.name, intent);

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
