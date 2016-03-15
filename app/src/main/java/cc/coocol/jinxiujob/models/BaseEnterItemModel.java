package cc.coocol.jinxiujob.models;

/**
 * Created by rrr on 16-2-29.
 */
public abstract class BaseEnterItemModel {


    public BaseEnterItemModel() {
    }

    protected int id;
    protected String name;
    protected String nick;
    protected String address;
    protected String time;
    protected int companyId;

    public BaseEnterItemModel(int id, String name, String nick, String address, String time, int companyId) {
        this.id = id;
        this.name = name;
        this.nick = nick;
        this.address = address;
        this.time = time;
        this.companyId = companyId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
