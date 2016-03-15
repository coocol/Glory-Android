package cc.coocol.jinxiujob.models;

/**
 * Created by rrr on 16-2-29.
 */
public class HotEnterItemModel extends BaseEnterItemModel {

    private int apply;

    public HotEnterItemModel() {

    }

    public HotEnterItemModel(int id, String name, String nick, String address, String time, int companyId, int apply) {
        super(id, name, nick, address, time, companyId);
        this.apply = apply;
    }

    public int getApply() {
        return apply;
    }

    public void setApply(int apply) {
        this.apply = apply;
    }
}
