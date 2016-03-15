package cc.coocol.jinxiujob.models;

/**
 * Created by rrr on 16-2-29.
 */
public class NearbyJobItemModel extends BaseJobItemModel{

    private int distance;

    public NearbyJobItemModel() {
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
