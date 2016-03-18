package cc.coocol.jinxiujob.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.HashMap;
import java.util.Map;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.fragments.AppliedFragment;
import cc.coocol.jinxiujob.fragments.CollectedFragment;
import cc.coocol.jinxiujob.fragments.EnterpriseFragment;
import cc.coocol.jinxiujob.fragments.JobFragment;
import cc.coocol.jinxiujob.fragments.SearchFragment;
import cc.coocol.jinxiujob.fragments.SettingsFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.BaseUserModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private JobFragment jobFragment;
    private EnterpriseFragment enterpriseFragment;
    private CollectedFragment collectedFragment;
    private SearchFragment searchFragment;
    private AppliedFragment appliedFragment;
    private SettingsFragment settingsFragment;

    private MaterialSearchView searchView;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    private BaseUserModel userModel;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 44) {
                showSimpleSnack("未能获取用户信息", MainActivity.this);
            } else if (msg.what == 88) {
                TextView nickView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nick);
                TextView signVIew = (TextView) navigationView.getHeaderView(0).findViewById(R.id.sign);
                if (userModel.getNick() == null) {
                    nickView.setText(userModel.getPhone());
                } else {
                    nickView.setText(userModel.getNick());
                }
                if (userModel.getSignature() == null) {
                    signVIew.setText("（尚未编辑数据）");
                } else {
                    signVIew.setText(userModel.getSignature());
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initSearchView();

        requestUserHeader(MyConfig.uid);
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            if (currentFragment != searchFragment) {
                searchView.showSearch();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_job:
                if (jobFragment == null) {
                    jobFragment = JobFragment.newInstance("", "");
                }
                showFragment(jobFragment, R.string.navigation_item_job);
                break;
            case R.id.nav_enter:
                if (enterpriseFragment == null) {
                    enterpriseFragment = EnterpriseFragment.newInstance("", "");
                }
                showFragment(enterpriseFragment, R.string.navigation_item_enterprise);
                break;
            case R.id.nav_apply:
                if (appliedFragment == null) {
                    appliedFragment = AppliedFragment.newInstance("", "");
                }
                showFragment(appliedFragment, R.string.navigation_item_apply);
                break;
            case R.id.nav_collect:
                if (collectedFragment == null) {
                    collectedFragment = CollectedFragment.newInstance("", "");
                }
                showFragment(collectedFragment, R.string.navigation_item_collect);
                break;
            case R.id.nav_location:
                gotoActivity(LocationActivity.class);
                break;
            case R.id.nav_settings:
                if (settingsFragment == null) {
                    settingsFragment = SettingsFragment.newInstance("", "");
                }
                showFragment(settingsFragment, R.string.action_settings);
                break;
        }
        drawer.closeDrawers();
        return true;
    }


    private void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Uri uri = Uri.parse("http://115.28.22.98:7652/api/v1.0/static/head/" + MyConfig.uid + ".jpg");
        final SimpleDraweeView draweeView = (SimpleDraweeView) navigationView.getHeaderView(0).findViewById(R.id.user_logo);
        draweeView.setImageURI(uri);
        draweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(MainActivity.this, UserHomeActivity.class);
                intent.putExtra("user_id", MyConfig.uid);
                startActivity(intent);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        navigationView.getMenu().getItem(4).setTitle(MyConfig.cityName);

    }

    private void initSearchView() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setCursorDrawable(R.drawable.search_cursor);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.equals("")) {
                    if (searchFragment != null) {
                        searchFragment.startQuery(query);
                        return true;
                    }
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(28, 28, 28, 20);
                searchView.setLayoutParams(params);
                if (searchFragment == null) {
                    searchFragment = SearchFragment.newInstance("", "");
                }
                showFragment(searchFragment, R.string.search);
            }

            @Override
            public void onSearchViewClosed() {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 0);
                searchView.setLayoutParams(params);
                showFragment(lastFragment);

            }
        });
        searchView.closeSearch();
    }

    private void requestUserHeader(final int userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object>m = new HashMap<>(2);
                m.put("token", MyConfig.token);
                ResponseStatus responseStatus = new HttpClient().get(URL.USER_HEADER + userId + "/header" , m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    userModel = HttpClient.getGson().fromJson(responseStatus.getData(),
                            BaseUserModel.class);
                    if (userModel != null) {
                        handler.sendEmptyMessage(88);
                    } else {
                        handler.sendEmptyMessage(44);
                    }
                } else  {
                    handler.sendEmptyMessage(44);
                }
            }
        }).start();
    }
}
