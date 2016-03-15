package cc.coocol.jinxiujob.models;

/**
 * Created by rrr on 16-2-29.
 */
public class NearbyJobItemModel extends BaseJobItemModel{

    private double distance;

    public NearbyJobItemModel() {
    }

    public NearbyJobItemModel(int id, String name, String company, int companyId, String address, String content, String nick, int collect, int apply, boolean available, String requirement, String description, String salary, int category, String time, double distance) {
        super(id, name, company, companyId, address, content, nick, collect, apply, available, requirement, description, salary, category, time);
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
