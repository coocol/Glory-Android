package cc.coocol.jinxiujob.persistents;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by coocol on 2016/3/12.
 */

@Table(name = "cities")
public class City extends Model {


    @Column(name = "province_id", index = true)
    public int provinceId;


    @Column(name = "city_id")
    public int cityId;


    @Column(name = "city")
    public String city;

    public City() {
        super();
    }

    public City(int provinceId, int cityId, String city) {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.city = city;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
