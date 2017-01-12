package yaoxin.example.com.popularmoves.support;

import java.io.Serializable;

/**
 * Created by yaoxinxin on 2017/1/12.
 */

public class AccountBean implements Serializable {

    public String id;
    public String hash;
    public boolean adult;
    public String name;
    public String username;

    public AccountBean(String id, String hash, boolean adult, String name, String username) {
        this.id = id;
        this.hash = hash;
        this.adult = adult;
        this.name = name;
        this.username = username;
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
