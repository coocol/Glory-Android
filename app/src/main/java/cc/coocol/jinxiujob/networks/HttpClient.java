package cc.coocol.jinxiujob.networks;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by coocol on 2016/3/9.
 */
public class HttpClient {

    private static OkHttpClient httpClient = new OkHttpClient();

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    public static final MediaType MEDIA_TYPE_IMAGES
            = MediaType.parse("image/*; charset=utf-8");

    private static Gson gson = new GsonBuilder().serializeNulls().create();

    public static Gson getGson() {
        return gson;
    }

    static {
    }

    private String makeGetArgs(Map<String, Object> args, boolean withToken) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            stringBuilder.append(entry.getKey() + "=" + entry.getValue().toString() + "&");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1).toString();
    }

    private String makePostValues(Map<String, Object> values, boolean withToken) {
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
            return new Gson().fromJson(response.body().string(), ResponseStatus.class);
        } catch (Exception e) {
            //Log.e("http", e.getMessage());
        }
        return new ResponseStatus();
    }

    public ResponseStatus postUserHead(File file, int user, String token) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("token", token)
                .addFormDataPart("file", user + ".jpg", RequestBody.create(MediaType.parse("image/jpeg"), file)).build();
        Response response = null;
        Request request = new Request.Builder()
                .url(URL.ROOT_PATH + "user/head/" + user)
                .post(requestBody)
                .build();
        try {
            response = httpClient.newCall(request).execute();
            String t = response.body().string();
            return gson.fromJson(t, ResponseStatus.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean downloadResume(Map<String, Object> args) {
        String url = URL.ROOT_PATH + URL.RESUME + "file";
        if (args != null) {
            url += "?";
            url += makeGetArgs(args, false);
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                File f = new File(Environment.getExternalStorageDirectory() + "/GloryResume/" + args.get("name").toString());
                if (f.exists()) {
                    f.delete();
                }
                f.getParentFile().mkdirs();
                f.createNewFile();

                BufferedOutputStream outputStream = new BufferedOutputStream(
                        new FileOutputStream(f));
                outputStream.write(response.body().bytes());
                outputStream.flush();
                outputStream.close();
                return true;
            }
        } catch (Exception e) {
            e.toString();
        }
        return false;
    }

    public ResponseStatus postResumePhoto(File file, int user, String token) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("token", token)
                .addFormDataPart("file", user + ".jpg", RequestBody.create(MediaType.parse("image/jpeg"), file)).build();
        Response response = null;
        Request request = new Request.Builder()
                .url(URL.ROOT_PATH + "resume/photo/" + user)
                .post(requestBody)
                .build();
        try {
            response = httpClient.newCall(request).execute();
            String t = response.body().string();
            return gson.fromJson(t, ResponseStatus.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public ResponseStatus postResumeFile(File file, String mediaTypeString, int user, String token) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("token", token)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(mediaTypeString), file)).build();
        Response response = null;
        Request request = new Request.Builder()
                .url(URL.ROOT_PATH + "resume/document/" + user)
                .post(requestBody)
                .build();
        try {
            response = httpClient.newCall(request).execute();
            String t = response.body().string();
            return gson.fromJson(t, ResponseStatus.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
