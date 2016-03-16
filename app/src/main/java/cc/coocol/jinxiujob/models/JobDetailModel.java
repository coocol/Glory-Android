package cc.coocol.jinxiujob.models;

/**
 * Created by coocol on 2016/3/16.
 */
public class JobDetailModel extends BaseJobItemModel {

    private String other;
    private String period;

    public JobDetailModel(){}

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
