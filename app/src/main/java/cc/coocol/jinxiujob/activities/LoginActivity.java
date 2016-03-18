package cc.coocol.jinxiujob.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.nispok.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.fragments.logins.EntryFragment;
import cc.coocol.jinxiujob.fragments.logins.LoginFragment;
import cc.coocol.jinxiujob.fragments.logins.RegisterFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.gsons.TokenConfigs;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;
import cc.coocol.jinxiujob.networks.RequestCities;
import cc.coocol.jinxiujob.utils.StringUtil;

public class LoginActivity extends BaseActivity {

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private EntryFragment entryFragment;

    private enum CurrentFace {ENTRY, LOGIN, REGISTER}

    CurrentFace currentFace;

    SharedPreferences sharedPreferences;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RequestCities.SUCCESS) {
                showSimpleSnack("城市同步成功", LoginActivity.this);
            } else {
                showSimpleSnack("城市同步失败", LoginActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        if (!sharedPreferences.getBoolean("location", false)) {
            new RequestCities(getApplicationContext(), this, null).request();
        }

        if (sharedPreferences.getBoolean("first", true) ||
                sharedPreferences.getString("token", "").equals("")) {
            changeToEntry();
        } else {
            Intent intent = getIntent();
            if (intent != null && intent.getStringExtra("modify_password") != null) {
                changeToLogin(intent.getStringExtra("modify_password"));
            } else if (intent != null && intent.getStringExtra("token_error") != null) {
                changeToLogin(null);
            }else {
                loadConfig();
                gotoActivity(MainActivity.class);
            }
        }


    }

    public void getToken(String phone, String password) {
        new MyTask<>().execute(phone, password);
    }

    class MyTask<S, R, R1> extends AsyncTask<String, ResponseStatus, ResponseStatus> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoginActivity.this.showProgressDialog("正在验证");
        }

        @Override
        protected ResponseStatus doInBackground(String... s) {
            Map<String, Object> args = new HashMap<>(4);
            args.put("phone", s[0]);
            args.put("password", StringUtil.sha1String(s[1]));
            return new HttpClient().post(URL.TOEKN, args, false);
        }

        private void saveConfig(TokenConfigs tokenConfigs) {

            MyConfig.cityName = tokenConfigs.getCity();
            MyConfig.appliable = tokenConfigs.isAppliable();
            MyConfig.cityId = tokenConfigs.getCityId();
            MyConfig.uid = tokenConfigs.getId();
            MyConfig.phone = tokenConfigs.getPhone();
            MyConfig.token = tokenConfigs.getToken();
            MyConfig.lng = sharedPreferences.getFloat("lng", 114.37702f);
            MyConfig.lat = sharedPreferences.getFloat("lat", 30.54961f);
            LoginActivity.this.saveConfig();
        }

        @Override
        protected void onPostExecute(ResponseStatus resp) {
            LoginActivity.this.dismissProgressDialog();
            if (resp == null) {
                return;
            }
            if (resp.getStatus().equals("fail")) {
                Snackbar.with(LoginActivity.this).text(resp.getMsg()).show(LoginActivity.this);
                if (currentFace != CurrentFace.LOGIN) {
                    changeToLogin(null);
                }
            } else {
                TokenConfigs tokenConfigs = HttpClient.getGson().fromJson(resp.getData(), TokenConfigs.class);
                saveConfig(tokenConfigs);
                SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
                editor.putBoolean("first", false);
                editor.commit();
                gotoActivity(MainActivity.class);
            }
        }
    }


    public void changeToRegister() {
        if (registerFragment == null) {
            registerFragment = RegisterFragment.newInstance();
        }
        showFragment(registerFragment);
        currentFace = CurrentFace.REGISTER;
    }

    public void changeToLogin(String prePhone) {
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance(prePhone);
        }
        showFragment(loginFragment);
        currentFace = CurrentFace.LOGIN;
    }

    public void changeToEntry() {
        if (entryFragment == null) {
            entryFragment = EntryFragment.newInstance();
        }
        showFragment(entryFragment);
        currentFace = CurrentFace.ENTRY;
    }

}
