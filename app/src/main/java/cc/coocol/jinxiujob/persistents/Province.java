package cc.coocol.jinxiujob.persistents;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by coocol on 2016/3/12.
 */

@Table(name = "provinces")
public class Province extends Model {


    @Column(name = "province_id")
    public int provinceId;

    @Column(name = "province")
    public String province;

    public Province() {
        super();
    }


    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


    public Province(int provinceId, String province) {
        this.provinceId = provinceId;
        this.province = province;
    }
}
