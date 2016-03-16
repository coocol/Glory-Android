package cc.coocol.jinxiujob.models;

/**
 * Created by coocol on 2016/3/16.
 */
public class BaseUserModel {
    private int id;
    private String phone;
    private String nick;
    private String signature;

    public BaseUserModel() {}

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

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
