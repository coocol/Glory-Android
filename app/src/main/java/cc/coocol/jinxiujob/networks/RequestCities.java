package cc.coocol.jinxiujob.networks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.nispok.snackbar.Snackbar;

import java.util.List;

import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;
import cc.coocol.jinxiujob.persistents.City;
import cc.coocol.jinxiujob.persistents.Province;

/**
 * Created by coocol on 2016/3/12.
 */


public class RequestCities {

    public static final int SUCCESS = 8;

    private Context context;

    private Activity activity;

    private Handler handler;

    public RequestCities(Context context, Activity activity, Handler handler) {
        this.context = context;
        this.activity = activity;
        this.handler = handler;
    }

    public void request() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ResponseStatus responseStatus = new HttpClient().get(URL.PROVINCES_CITIES, null, false);
                    CityGson tmp = HttpClient.getGson().fromJson(responseStatus.getData(), CityGson.class);
                    ActiveAndroid.beginTransaction();
                    for (Province p: tmp.provinces) {
                        p.save();
                    }
                    for (City c: tmp.cities) {
                        c.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    SharedPreferences.Editor editor = context.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("location", true);
                    editor.commit();
                    if (handler != null) {
                        handler.sendEmptyMessage(SUCCESS);
                    }
                } catch (Exception e) {
                }
            }

        }).start();
    }

    class CityGson {
        public List<Province> provinces;
        public List<City> cities;

        public CityGson() {
        }
    }

}
