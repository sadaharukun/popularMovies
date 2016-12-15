package yaoxin.example.com.popularmoves.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yaoxinxin on 2016/12/14.
 */

public class ReviewsSQLiteOpenHelper extends SQLiteOpenHelper {

    private static int databaseVersion = 1;

    public ReviewsSQLiteOpenHelper(Context context) {
        super(context, MovieSQLiteOpenHelper.databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + "(" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewsEntry.AUTHOR + " VARCHAR(255) NOT NULL," +
                ReviewsEntry.AUTHOR_ID + " TEXT NOT NULL," +
                ReviewsEntry.COMMENTS + " TEXT NOT NULL," +
                ReviewsEntry.AUTHOR_URL + " TEXT NOT NULL" +
                "FOREIGN KEY (" + ReviewsEntry.MOVE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.MOVIEID + ")" +

                ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        onCreate(db);
    }
}
