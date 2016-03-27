package cc.coocol.jinxiujob.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Map;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.fragments.BaseFragment;

public abstract class BaseActivity extends AppCompatActivity {

    protected android.support.v4.app.FragmentManager fragmentManager;
    protected Fragment currentFragment;
    protected Fragment lastFragment;

    protected AlertDialog dialog;

    MaterialDialog progressDialog;

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    public void showProgressDialog(String title) {
        progressDialog = new MaterialDialog.Builder(this)
                .title(title).content("请稍后").progress(true, 0)
                .show();
    }

    public void showProgressDialog() {
        progressDialog = new MaterialDialog.Builder(this)
                .content("请稍后").progress(true, 0)
                .show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
    }

    public void showFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.container, fragment, "");
        }
        if (currentFragment != null && currentFragment.isAdded()) {
            transaction.hide(currentFragment);
        }
        transaction.commit();
        lastFragment = currentFragment;
        currentFragment = fragment;
        if (lastFragment == null) {
            lastFragment = currentFragment;
        }
        try {
            String t = ((BaseFragment)fragment).getTile();
            if (t != null) {
                getSupportActionBar().setTitle(t);
            }
        } catch (Exception e) {

        }

    }

    public void hideFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null && fragment.isAdded()) {
            transaction.hide(currentFragment);
        }
        transaction.commit();
    }


    public void showFragment(Fragment fragment, int titleRes) {
        if (currentFragment == fragment) {
            return;
        }
        getSupportActionBar().setTitle(getResources().getString(titleRes));
        showFragment(fragment);
    }


    public void showSimpleSnack(String content, Activity targetActivity) {
        com.nispok.snackbar.Snackbar.with(this).text(content).show(targetActivity);
    }

    public void gotoActivity(Class activity) {
        startActivity(new Intent(this, activity));
    }

    public void gotoActivity(Class activity, int FLAG) {
        Intent intent = new Intent(this, activity);
        intent.addFlags(FLAG);
        startActivity(intent);
    }

    public void gotoActivity(Class activity, String key, String value) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    public void saveConfig() {
        SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
        editor.putBoolean("appliable", MyConfig.appliable);
        editor.putString("city", MyConfig.cityName);
        editor.putString("token", MyConfig.token);
        editor.putString("phone", MyConfig.phone);
        editor.putInt("cityId", MyConfig.cityId);
        editor.putInt("uid", MyConfig.uid);
        editor.putFloat("lat", (float) MyConfig.lat);
        editor.commit();
    }

    public void loadConfig() {
        SharedPreferences s = getSharedPreferences("config", MODE_PRIVATE);
        MyConfig.appliable = s.getBoolean("appliable", false);
        MyConfig.cityId = s.getInt("cityId", 0);
        MyConfig.cityName = s.getString("city", "全国");
        MyConfig.token = s.getString("token", null);
        MyConfig.phone = s.getString("phone", null);
        MyConfig.uid = s.getInt("uid", 0);
    }


}
