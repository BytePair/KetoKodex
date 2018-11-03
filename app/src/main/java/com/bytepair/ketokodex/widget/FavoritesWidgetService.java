package com.bytepair.ketokodex.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FavoritesWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavoritesWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}
