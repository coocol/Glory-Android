package cc.coocol.jinxiujob.models;

/**
 * Created by coocol on 2016/3/9.
 */
public class User {
    public User() {
    }

    public User(int id, String phone, String token) {
        this.id = id;
        this.phone = phone;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private int id;
    private String phone;
    private String token;


}
