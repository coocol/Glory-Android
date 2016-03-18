package cc.coocol.jinxiujob.application;


import android.content.SharedPreferences;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.facebook.drawee.backends.pipeline.Fresco;

import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.utils.Orientation;


public class GloryApplication extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);

        new Orientation(getApplicationContext(), new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                MyConfig.lat = bdLocation.getAltitude();
                MyConfig.lng = bdLocation.getLongitude();
                SharedPreferences.Editor editor = getSharedPreferences("config",MODE_PRIVATE).edit();
                editor.putFloat("lat", (float) MyConfig.lat);
                editor.putFloat("lng", (float) MyConfig.lng);
                editor.commit();

            }
        }).orient();
    }

}
