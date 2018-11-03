package com.bytepair.ketokodex.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.bytepair.ketokodex";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "plants" directory
    public static final String PATH_FAVORITES = "favorites";

    public static final long INVALID_FAVORITE_ID = -1;

    public static final class FavoriteEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String FAVORITE_ID = "favoriteId";
        public static final String FAVORITE_NAME = "favoriteName";
    }
}