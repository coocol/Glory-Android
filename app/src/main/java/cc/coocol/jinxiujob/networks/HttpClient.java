package cc.coocol.jinxiujob.networks;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOError;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by coocol on 2016/3/9.
 */
public class HttpClient {

    private static OkHttpClient httpClient = new OkHttpClient();

    private static Gson gson = new GsonBuilder().serializeNulls().create();

    public static Gson getGson() {
        return gson;
    }

    private String makeGetArgs(Map<String, Object> args, boolean withToken) {
//        if (withToken) {
//            args.put("token", MyConfig.token);
//        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry: args.entrySet()) {
            stringBuilder.append(entry.getKey()+ "=" + entry.getValue().toString() + "&");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1).toString();
    }

    private String makePostValues(Map<String, Object> values, boolean withToken) {
//        if (withToken) {
//            values.put("token", MyConfig.token);
//        }
        return gson.toJson(values);
    }

    public ResponseStatus get(String url, Map<String, Object> args, boolean withToken) {
        url = URL.ROOT_PATH + url;
        if (args != null) {
            url += "?";
            url += makeGetArgs(args, withToken);
        }
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = httpClient.newCall(request).execute();
            return new Gson().fromJson(response.body().string(),ResponseStatus.class);
        } catch (Exception e) {
            Log.e("http", e.getMessage());
        }
        return new ResponseStatus();
    }



    public ResponseStatus post(String url, Map<String, Object> data, boolean withToken) {
        url = URL.ROOT_PATH + url;
        RequestBody body = RequestBody.create(MediaType.parse("JSON"), makePostValues(data, withToken));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
                return gson.fromJson(response.body().string(), ResponseStatus.class);
        } catch (Exception e) {
            Log.e("http", e.getMessage());
        }
        return null;
    }
}
