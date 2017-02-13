package yaoxin.example.com.popularmoves.fragment.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yaoxinxin on 2017/2/9.
 *
 * popular people
 */

public class PopularPeople implements Parcelable {


    public int id;

    public String name;

    public boolean adult;

    public String profile_path;

    public float popularity;

    public String biography;

    public String brithday;

    public String deathday;

    public int gender;

    public int imdb_id;

    public String place_of_birth;


    public PopularPeople() {
    }


    protected PopularPeople(Parcel in) {
        id = in.readInt();
        name = in.readString();
        adult = in.readByte() != 0;
        profile_path = in.readString();
        popularity = in.readFloat();
        biography = in.readString();
        brithday = in.readString();
        deathday = in.readString();
        gender = in.readInt();
        imdb_id = in.readInt();
        place_of_birth = in.readString();
    }

    public static final Creator<PopularPeople> CREATOR = new Creator<PopularPeople>() {
        @Override
        public PopularPeople createFromParcel(Parcel in) {
            return new PopularPeople(in);
        }

        @Override
        public PopularPeople[] newArray(int size) {
            return new PopularPeople[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(profile_path);
        dest.writeFloat(popularity);
        dest.writeString(biography);
        dest.writeString(brithday);
        dest.writeString(deathday);
        dest.writeInt(gender);
        dest.writeInt(imdb_id);
        dest.writeString(place_of_birth);
    }
}
