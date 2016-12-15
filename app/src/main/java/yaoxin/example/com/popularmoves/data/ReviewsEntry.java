package yaoxin.example.com.popularmoves.data;

import android.provider.BaseColumns;

/**
 * Created by yaoxinxin on 2016/12/13.
 * 用户评论
 */

public class ReviewsEntry implements BaseColumns {

    public static final String TABLE_NAME = "movie_reviews";

    public static final String MOVE_ID = "movie_id";

    public static final String AUTHOR_ID = "author_id";

    public static final String AUTHOR = "author";

    public static final String COMMENTS = "comments";

    public static final String AUTHOR_URL = "author_url";


}
