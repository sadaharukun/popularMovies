package yaoxin.example.com.popularmoves.data;

import android.provider.BaseColumns;

/**
 * Created by yaoxinxin on 2016/12/23.
 */

public class MovieTypeEntry implements BaseColumns {

    public static final String TABLENAME = "movie_type";//popular or voteaverage

    public static final String MOVIEID = "moive_id";

    public static final String POPULAR = "popular";

    public static final String VOTEAVERAGE = "voteaverage";

}
