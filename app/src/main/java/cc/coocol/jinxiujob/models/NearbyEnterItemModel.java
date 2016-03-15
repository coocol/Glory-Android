package cc.coocol.jinxiujob.models;

/**
 * Created by rrr on 16-2-29.
 */
public class NearbyEnterItemModel extends BaseEnterItemModel {

    private double distance;

    public NearbyEnterItemModel() {

    }

    public NearbyEnterItemModel(int id, String name, String nick, String address, String time, int companyId, double distance) {
        super(id, name, nick, address, time, companyId);
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
