package yaoxin.example.com.popularmoves.data;

import android.provider.BaseColumns;

/**
 * Created by yaoxinxin on 2016/12/5.
 * <p>
 * Movie实体
 */

public class MovieEntry implements BaseColumns {

    public static final String DEFAULT_SORT_ORDER = "_id asc";

    public static final String TABLE_NAME = "movie";

    public static final String TITLE = "title";

    public static final String MOVIEID = "movieid";

    public static final String POSTURL = "postUrl";

    public static final String BACKDROPURL = "backdropUrl";

    public static final String OVERVIEW = "overview";

    public static final String VOTEAVERAGE = "voteAverage";

    public static final String REALEASEDATE = "releaseDate";

//    public static final String COMMENT="comment";

    public static final String COLLECTED = "collected";

    public static final String GENRES = "genres";

    public static final String PRODUCTIONS_COUNTRY = "production_country";
}
