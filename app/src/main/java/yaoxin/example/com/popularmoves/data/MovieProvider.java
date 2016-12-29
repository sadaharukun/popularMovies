package yaoxin.example.com.popularmoves.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yaoxinxin on 2016/12/1.
 */

public class MovieProvider extends ContentProvider {

    /*SQLiteOpenHelper*/
    private MovieSQLiteOpenHelper mOpenHelper;
//    private ReviewsSQLiteOpenHelper mReviewsOpenHelper;

    private ContentResolver mResolver;

    /*UriMatcher and matcherCode*/
    private static final int MOVIE = 100;
    private static final int MOVIE_ID = 101;
    private static final int REVIEWS = 102;
    private static final int REVIEWS_ID = 103;
    private static final int VIDEOS = 104;
    private static final int VIDEOS_ID = 105;
    private static final int TYPE = 106;
    private static final int TYPE_ID = 107;
    private static final int MOVIE_MOVIETYPE = 108;
    //    private static final int MOVIE_COLLECTED = 102;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_ID);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS + "/#", REVIEWS_ID);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS, VIDEOS);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS + "/#", VIDEOS_ID);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TYPE, TYPE);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TYPE + "/#", TYPE_ID);
        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE_MOVIETYPE, MOVIE_MOVIETYPE);
//        mUriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE_COLLECTED, MOVIE_COLLECTED);
    }


    /*隐藏数据库内部设计，给每个列名重新起一个别名*/
    private static Map<String, String> mProjectionMap;

    static {
        mProjectionMap = new HashMap<>();

    }


    private DatabaseManager databaseManager;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieSQLiteOpenHelper(this.getContext());
//        mReviewsOpenHelper = new ReviewsSQLiteOpenHelper(this.getContext());
        mResolver = getContext().getContentResolver();
        databaseManager = DatabaseManager.getInstance(mOpenHelper);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //select * form table
//        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        Cursor cursor = null;
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                builder.setTables(MovieEntry.TABLE_NAME);
                break;
            case MOVIE_ID:
                builder.setTables(MovieEntry.TABLE_NAME);
                String id = uri.getPathSegments().get(1);
                builder.appendWhere(MovieEntry.MOVIEID + "=" + id);
                break;
            case REVIEWS:
                builder.setTables(ReviewsEntry.TABLE_NAME);
                break;
            case REVIEWS_ID:
                builder.setTables(ReviewsEntry.TABLE_NAME);
                String review_id = uri.getPathSegments().get(1);
                builder.appendWhere(ReviewsEntry._ID + "=" + review_id);
                break;
            case VIDEOS:
                builder.setTables(VideosEntry.TABLE_NAME);
                break;
            case VIDEOS_ID:
                builder.setTables(VideosEntry.TABLE_NAME);
                String video_id = uri.getPathSegments().get(1);
                builder.appendWhere(VideosEntry._ID + "=" + video_id);
                break;
            case TYPE:
                builder.setTables(MovieTypeEntry.TABLENAME);
                break;
            case TYPE_ID:
                builder.setTables(MovieTypeEntry.TABLENAME);
                String type_id = uri.getPathSegments().get(1);
                builder.appendWhere(MovieTypeEntry._ID + "=" + type_id);
                break;
            case MOVIE_MOVIETYPE:
//                foo LEFT OUTER JOIN bar ON
                builder.setTables(MovieEntry.TABLE_NAME + "," + MovieTypeEntry.TABLENAME);
                break;
        }
        cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(mResolver, uri);
//        db.close();
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                return MovieContract.CONTENT_MOVIE_TYPE;
//            case MOVIE_COLLECTED:
//                return MovieContract.CONTENT_MOVIE_TYPE;
            case MOVIE_ID:
                return MovieContract.CONTENT_MOVIE_ITEM_TYPE;
            case REVIEWS:
                return MovieContract.CONTENT_REVIEWS_TYPE;
            case REVIEWS_ID:
                return MovieContract.CONTENT_REVIEWS_ITEM_TYPE;
            case VIDEOS:
                return MovieContract.CONTENT_VIDEOS_TYPE;
            case VIDEOS_ID:
                return MovieContract.CONTENT_VIDEOS_ITEM_TYPE;
            case TYPE:
                return MovieContract.CONTENT_TYPE_TYPE;
            case TYPE_ID:
                return MovieContract.CONTENT_TYPE_ITEM_TYPE;

        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //insert into tableName() values();
//        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        SQLiteDatabase db = databaseManager.getWritableDatabase();
//        if (mUriMatcher.match(uri) != MOVIE || mUriMatcher.match(uri) != REVIEWS) {
//            throw new IllegalArgumentException("unknown uri error");
//        }

        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                long rowid = db.insert(MovieEntry.TABLE_NAME, MovieEntry._ID, values);
                if (rowid == -1) {
                    throw new SQLiteException("insert error...");
                }

                Uri newUri = MovieContract.buildMovieUri(rowid);
                mResolver.notifyChange(newUri, null);
                return newUri;
            case REVIEWS:
                long review_rowId = db.insert(ReviewsEntry.TABLE_NAME, MovieEntry._ID, values);
                if (review_rowId == -1) {
                    throw new SQLiteException("insert error");
                }
                Uri review_uri = MovieContract.buildReviewUri(review_rowId);
                return review_uri;
            case VIDEOS:
                long video_rowId = db.insert(VideosEntry.TABLE_NAME, VideosEntry._ID, values);
                if (video_rowId == -1) {
                    throw new SQLiteException("insert error");
                }
                Uri video_uri = MovieContract.buildVideoUri(video_rowId);
                return video_uri;
            case TYPE:
                long type_id = db.insert(MovieTypeEntry.TABLENAME, MovieTypeEntry._ID, values);
                if (type_id == -1) {
                    throw new SQLiteException("insert error");
                }
                return MovieContract.buildTypeUri(type_id);
        }
        return null;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //delete table tableName where id= ? ,"3"
//        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        int count = 0;
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                count = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + "=" + id + (TextUtils.isEmpty(selection) ?
                        "" : " and (" + selection + ")"), selectionArgs);
                break;
            case REVIEWS:
                count = db.delete(ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS_ID:
                String reviews_id = uri.getPathSegments().get(1);
                count = db.delete(ReviewsEntry.TABLE_NAME, ReviewsEntry._ID + "=" + reviews_id +
                        (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
                break;
            case VIDEOS:
                count = db.delete(VideosEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEOS_ID:
                String video_id = uri.getPathSegments().get(1);
                count = db.delete(VideosEntry.TABLE_NAME, VideosEntry._ID + "=" + video_id +
                        (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
                break;
            case TYPE:
                count = db.delete(MovieTypeEntry.TABLENAME, selection, selectionArgs);
                break;
            case TYPE_ID:
                String type_id = uri.getPathSegments().get(1);
                count = db.delete(MovieTypeEntry.TABLENAME, MovieTypeEntry._ID + "=" + type_id +
                        (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
                break;
        }
        if (count != 0) {
            mResolver.notifyChange(uri, null);
        }
//        db.close();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //update table tablename set key = value where selection=? selectionargs
//        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        int count = 0;
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                count = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(MovieEntry.TABLE_NAME, values, MovieEntry.MOVIEID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
                break;
            case REVIEWS:
                count = db.update(ReviewsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEWS_ID:
                String reviews_id = uri.getPathSegments().get(1);
                count = db.update(ReviewsEntry.TABLE_NAME, values, ReviewsEntry._ID + "=" + reviews_id +
                        (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
                break;
            case VIDEOS:
                count = db.update(VideosEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case VIDEOS_ID:
                String video_id = uri.getPathSegments().get(1);
                count = db.update(VideosEntry.TABLE_NAME, values, VideosEntry._ID + "=" + video_id +
                        (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
                break;
            case TYPE:
                count = db.update(MovieTypeEntry.TABLENAME, values, selection, selectionArgs);
                break;
            case TYPE_ID:
                String type_id = uri.getPathSegments().get(1);
                count = db.update(MovieTypeEntry.TABLENAME, values, MovieTypeEntry._ID + "=" + type_id +
                        (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs
                );
                break;
        }
        if (count != 0) {
            mResolver.notifyChange(uri, null);
        }
//        db.close();

        return count;
    }

    /*利用事物插入多条数据到数据库*/
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
//        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                int rowCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {

                        long id = db.insert(MovieEntry.TABLE_NAME, MovieEntry._ID, value);
                        if (id != -1) {
                            rowCount++;
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                mResolver.notifyChange(uri, null);
                return rowCount;

            case REVIEWS:
                int review_count = 0;
                db.beginTransaction();
                try {
                    for (ContentValues values1 : values) {
                        long review_id = db.insert(ReviewsEntry.TABLE_NAME, ReviewsEntry._ID, values1);
                        if (review_id != -1) {
                            review_count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                mResolver.notifyChange(uri, null);
                return review_count;

            case VIDEOS:
                int video_count = 0;
                db.beginTransaction();
                try {
                    for (ContentValues val : values) {
                        long video_id = db.insert(MovieEntry.TABLE_NAME, MovieEntry._ID, val);
                        if (video_id != -1) {
                            video_count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                mResolver.notifyChange(uri, null);
                return video_count;
            case TYPE:
                int type_count = 0;
                db.beginTransaction();
                try {
                    for (ContentValues val : values) {
                        long type_id = db.insert(MovieTypeEntry.TABLENAME, MovieTypeEntry._ID, val);
                        if (type_id != -1) {
                            type_count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                mResolver.notifyChange(uri, null);
                return type_count;
            default:

                return super.bulkInsert(uri, values);
        }


    }


    public Bundle getColumnCount(Uri uri) {
        //select count(*) from table tableName;
//        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        int count = -1;
        Bundle bundle = null;
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                Cursor cursor = db.rawQuery("select count(*) from " + MovieEntry.TABLE_NAME, null);
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
                cursor.close();
                bundle = new Bundle();
                bundle.putInt(MovieEntry._COUNT, count);
                break;
            case REVIEWS:
                Cursor cursor1 = db.rawQuery("select count(*) from " + ReviewsEntry.TABLE_NAME, null);
                if (cursor1.moveToFirst()) {
                    count = cursor1.getInt(0);
                }
                cursor1.close();
                bundle = new Bundle();
                bundle.putInt(ReviewsEntry._COUNT, count);
                break;
            case TYPE:
                Cursor type_cursor = db.rawQuery("select count(*) from " + MovieTypeEntry.TABLENAME, null);
                if (type_cursor.moveToFirst()) {
                    count = type_cursor.getInt(0);
                }
                type_cursor.close();
                bundle = new Bundle();
                bundle.putInt(MovieTypeEntry._COUNT, count);
                break;
        }
//        Cursor cursor = db.rawQuery("select count(*) from " + MovieEntry.TABLE_NAME, null);
//        int count = -1;
//        if (cursor.moveToFirst()) {
//            count = cursor.getInt(0);
//        }


        return bundle;

    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        super.shutdown();
    }


}
