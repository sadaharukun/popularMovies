package yaoxin.example.com.popularmoves.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by yaoxinxin on 2016/12/1.
 * 定义数据库
 */

public class MovieContract extends Object {

    public static final String AUTHORITY = "yaoxin.example.com.popularmoves";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_MOVIE_COLLECTED = "collected";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_VIDEOS = "videos";

    /*ContentUri*/
    public static final Uri CONTENT_MOVE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
    public static final Uri CONTENT_MOVIE_COLLECTED_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_COLLECTED).build();
    public static final Uri CONTENT_REVIEWS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
    public static final Uri CONTENT_VIDEOS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();
    /*MIME*/
    public static final String CONTENT_MOVIE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_MOVIE;
    public static final String CONTENT_MOVIE_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_MOVIE;
    public static final String CONTENT_REVIEWS_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_REVIEWS;
    public static final String CONTENT_REVIEWS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_REVIEWS;
    public static final String CONTENT_VIDEOS_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_VIDEOS;
    public static final String CONTENT_VIDEOS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_REVIEWS;

    public static Uri buildMovieUri(long _id) {
        return ContentUris.withAppendedId(CONTENT_MOVE_URI, _id);
    }

    public static Uri buildReviewUri(long _id) {
        return ContentUris.withAppendedId(CONTENT_REVIEWS_URI, _id);
    }

    public static Uri buildVideoUri(long _id) {
        return ContentUris.withAppendedId(CONTENT_VIDEOS_URI, _id);
    }

    public static Uri buildMovieCollectedUri(String collected) {
        return CONTENT_MOVE_URI.buildUpon().appendQueryParameter(MovieEntry.COLLECTED, collected).build();
    }


}
