package cc.coocol.jinxiujob.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by coocol on 2016/3/7.
 */
public class Orientation {

    public Orientation(Context context, BDLocationListener bdLocationListener) {
        this.context = context;
        this.bdLocationListener = bdLocationListener;

        initBDMap();
    }

    private LocationClient locationClient;
    private BDLocationListener bdLocationListener;
    private LocationClientOption option;

    private Context context;

    private void initBDMap(){
        locationClient = new LocationClient(context);
        locationClient.registerLocationListener(bdLocationListener);

        option = new LocationClientOption();
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);

        locationClient.setLocOption(option);
    }

    public void orient() {
        locationClient.start();
    }
}
