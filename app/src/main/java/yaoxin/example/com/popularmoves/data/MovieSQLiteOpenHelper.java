package yaoxin.example.com.popularmoves.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yaoxinxin on 2016/12/1.
 */

public class MovieSQLiteOpenHelper extends SQLiteOpenHelper {

    public static int databaseVersion = 22;

    public static String databaseName = "yaoxin.db";


    public MovieSQLiteOpenHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + MovieEntry.TABLE_NAME + "(" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.TITLE + " TEXT NOT NULL," +
                MovieEntry.MOVIEID + " TEXT NOT NULL," +
                MovieEntry.POSTURL + " TEXT NOT NULL," +
                MovieEntry.BACKDROPURL + " TEXT NOT NULL," +
                MovieEntry.OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.VOTEAVERAGE + " REAL," +
                MovieEntry.POPULARITY + " REAL," +
                MovieEntry.COLLECTED + " TEXT," +
                MovieEntry.REALEASEDATE + " TEXT NOT NULL," +
                MovieEntry.RUNTIME + " REAL," +
                MovieEntry.GENRES + " TEXT NOT NULL," +
                MovieEntry.PRODUCTIONS_COUNTRY + " TEXT"
                + ")";

        String sql2 = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + "(" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewsEntry.AUTHOR + " VARCHAR(255) NOT NULL," +
                ReviewsEntry.AUTHOR_ID + " TEXT NOT NULL," +
                ReviewsEntry.COMMENTS + " TEXT NOT NULL," +
                ReviewsEntry.AUTHOR_URL + " TEXT NOT NULL," +
                ReviewsEntry.MOVE_ID + " TEXT," +
                "FOREIGN KEY (" + ReviewsEntry.MOVE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.MOVIEID + ")" +

                ")";

        String sql3 = "CREATE TABLE " + VideosEntry.TABLE_NAME + "(" +
                VideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideosEntry.MOVIE_ID + " TEXT NOT NULL," +
                VideosEntry.VIDEO_KEYS + " TEXT," +
                "FOREIGN KEY (" + VideosEntry.MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.MOVIEID + ")"
                + ")";

        String sql4 = "CREATE TABLE " + MovieTypeEntry.TABLENAME + "(" +
                MovieTypeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieTypeEntry.MOVIEID + " TEXT NOT NULL UNIQUE," +
                MovieTypeEntry.POPULAR + " TEXT DEFAULT 1," +
                MovieTypeEntry.VOTEAVERAGE + " TEXT DEFAULT 1," +
                "FOREIGN KEY (" + MovieTypeEntry.MOVIEID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.MOVIEID + ")" +
                ")";

        System.out.println("create table:" + sql);

        db.execSQL(sql);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        switch (newVersion){//保存用户数据
//
//        }
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideosEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieTypeEntry.TABLENAME);
        onCreate(db);

    }
}
