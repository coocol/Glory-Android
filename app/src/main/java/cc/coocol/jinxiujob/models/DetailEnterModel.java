package cc.coocol.jinxiujob.models;

/**
 * Created by coocol on 2016/3/16.
 */
public class DetailEnterModel extends BaseEnterItemModel {

    private int apply;
    private int collect;
    private int jobs;
    private String introduction;

    public DetailEnterModel(){}

    public int getApply() {
        return apply;
    }

    public void setApply(int apply) {
        this.apply = apply;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getJobs() {
        return jobs;
    }

    public void setJobs(int jobs) {
        this.jobs = jobs;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
