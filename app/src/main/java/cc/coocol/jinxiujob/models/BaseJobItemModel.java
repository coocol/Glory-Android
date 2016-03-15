package cc.coocol.jinxiujob.models;

/**
 * Created by rrr on 16-2-29.
 */
public abstract class BaseJobItemModel {

    protected int id;
    protected String name;
    protected String company;
    protected int companyId;
    protected String address;
    protected String content;
    protected String nick;
    protected int collect;
    protected int apply;
    protected boolean available;
    protected String requirement;
    protected String description;
    protected String salary;
    protected int category;
    protected String time;

    public BaseJobItemModel(int id, String name, String company, int companyId, String address, String content, String nick, int collect, int apply, boolean available, String requirement, String description, String salary, int category, String time) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.companyId = companyId;
        this.address = address;
        this.content = content;
        this.nick = nick;
        this.collect = collect;
        this.apply = apply;
        this.available = available;
        this.requirement = requirement;
        this.description = description;
        this.salary = salary;
        this.category = category;
        this.time = time;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getApply() {
        return apply;
    }

    public void setApply(int apply) {
        this.apply = apply;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public BaseJobItemModel() {
    }
}
