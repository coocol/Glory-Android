package cc.coocol.jinxiujob.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;
import cc.coocol.jinxiujob.persistents.City;
import cc.coocol.jinxiujob.persistents.Province;
import cc.coocol.jinxiujob.utils.Orientation;
import cc.coocol.jinxiujob.networks.RequestCities;
import cc.coocol.jinxiujob.utils.ViewLayoutUtil;

public class LocationActivity extends BaseActivity implements BDLocationListener, View.OnClickListener {

    private ListView listView;
    private GridView gridView;
    private TextView orientTextView;
    private CardView orientCard;

    private City city;

    private List<Integer> onlyProvincesIds;

    private City[] hotCities = new City[]{new City(0, 0, "全国"), new City(110000, 110100, "北京"),
            new City(310000, 310100, "上海"), new City(0, 0, "深圳"), new City(440000, 440100, "广州"),
            new City(510000, 5101000, "成都"), new City(330000, 330100, "杭州"), new City(320000, 320100, "南京"),
            new City(420000, 420100, "武汉"), new City(610000, 610100, "西安"), new City(210000, 210100, "沈阳"),
            new City(120000, 0, "天津")
    };
    private String[] hotCitiesNames;
    private List<Province> provinces = new LinkedList<>();
    private List<String> provincesNames;
    private List<City> theCities;
    private boolean orientStatus = false;

    private final int MODIFY_SUCCESS = 45;
    private final int MODIFY_FAIL = 44;
    private final int REFRESH_LIST = 32;
    private final int TOKEN_ERROR = 72;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RequestCities.SUCCESS) {
                showSimpleSnack("位置数据库同步成功", LocationActivity.this);
                refreshProvincesList();
            } else if (msg.what == MODIFY_SUCCESS) {
                showSimpleSnack("修改成功", LocationActivity.this);
                MyConfig.cityId = city.getCityId();
                MyConfig.cityName = city.getCity();
                saveConfig();
                gotoActivity(MainActivity.class);

            } else if (msg.what == REFRESH_LIST) {
                listView.setAdapter(
                        new ArrayAdapter<>(LocationActivity.this, R.layout.location_province_list, provincesNames));
                ViewLayoutUtil.setListViewHeightBasedOnChildren(listView);
            } else if (msg.what == TOKEN_ERROR) {
                gotoActivity(LoginActivity.class, "token_error", "error");
                showSimpleSnack("请重新登录", LocationActivity.this);
            }
        }
    };

    public LocationActivity() {
        hotCitiesNames = new String[hotCities.length];
        for (int i = 0; i < hotCities.length; i++) {
            hotCitiesNames[i] = hotCities[i].getCity();
        }

        onlyProvincesIds = new ArrayList<>(4);
        onlyProvincesIds.add(110000);
        onlyProvincesIds.add(120000);
        onlyProvincesIds.add(310000);
        onlyProvincesIds.add(500000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initView();
        
        if (!getSharedPreferences("config", MODE_PRIVATE).getBoolean("location", false)) {
            new RequestCities(this, this, handler).request();
        } else {
            refreshProvincesList();
        }

        new Orientation(getApplicationContext(), this).orient();
    }

    
    private void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_loc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_clear_mtrl_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("城市：" + MyConfig.cityName);

        orientCard = (CardView) findViewById(R.id.orient_card);
        orientCard.setOnClickListener(this);
        gridView = (GridView) findViewById(R.id.city_grid);
        orientTextView = (TextView) findViewById(R.id.orient_city);
        listView = (ListView) findViewById(R.id.list_view);
        gridView.setAdapter(new ArrayAdapter<>(this, R.layout.location_province_list, hotCitiesNames));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = hotCities[position];
                getSupportActionBar().setTitle("城市：" + city.getCity());
            }
        });

        ViewLayoutUtil.setGridViewHeightBasedOnChildren(gridView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pid = provinces.get(position).getProvinceId();
                if (onlyProvincesIds.contains(pid)) {
                    city = new Select().from(City.class).where("province_id = ?", pid).executeSingle();
                    getSupportActionBar().setTitle("城市：" + city.getCity());
                } else {
                    theCities = new Select().from(City.class).where("province_id = ?", pid).execute();
                    String[] nns = new String[theCities.size()];
                    for (int i = 0; i < theCities.size(); i++) {
                        nns[i] = theCities.get(i).getCity();
                    }
                    showCitiesDialog(provinces.get(position).getProvince(), nns);
                }
            }
        });
    }

    private void showCitiesDialog(String province, String[] cities) {
        new MaterialDialog.Builder(this)
                .title(province).items(cities).autoDismiss(true).negativeText("取消")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        city = theCities.get(which);
                        getSupportActionBar().setTitle("城市：" + city.getCity());
                    }
                })
                .show();
    }

    private void refreshProvincesList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                provinces = new Select().from(Province.class).execute();
                provincesNames = new ArrayList<>(provinces.size());
                for (int i = 0; i < provinces.size(); i++) {
                    provincesNames.add(provinces.get(i).getProvince());
                }
                handler.sendEmptyMessage(32);
            }
        }).start();
    }

    private void refreshCitiesGrid() {
        provinces = new Select().from(City.class).execute();
        provincesNames = new ArrayList<>(provinces.size());
        for (int i = 0; i < provinces.size(); i++) {
            provincesNames.add(provinces.get(i).getProvince());
        }
        listView.setAdapter(
                new ArrayAdapter<>(LocationActivity.this, R.layout.location_province_list, provincesNames));
        ViewLayoutUtil.setListViewHeightBasedOnChildren(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_ok) {
            if (city != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, Object> m = new HashMap<>(8);
                        m.put("uid", MyConfig.uid);
                        m.put("token", MyConfig.token);
                        if (city != null) {
                            Map<String, Object> mm = new HashMap<>(2);
                            mm.put("cityId", city.getCityId());
                            mm.put("city", city.getCity());
                            m.put("values",mm);
                        }
                        ResponseStatus responseStatus = new HttpClient().post(URL.USER_LOCATION,m, true);
                        if (responseStatus != null && responseStatus.getStatus() != null) {
                            if (responseStatus.getStatus().equals("fail") && responseStatus.getMsg().equals("token error")){
                                handler.sendEmptyMessage(TOKEN_ERROR);
                            } else if (responseStatus.getStatus().equals("success")) {
                                handler.sendEmptyMessage(MODIFY_SUCCESS);
                            }
                        } else {
                            handler.sendEmptyMessage(MODIFY_FAIL);
                        }
                    }
                }).start();
            }
        }
        return true;
    }
    

    @Override
    public void onReceiveLocation(BDLocation location) {

        Address address = location.getAddress();
        orientTextView.setTextColor(Color.BLACK);
        orientTextView.setText(address.city);
        orientStatus = true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.orient_card) {
            if (orientStatus) {
                String s = orientTextView.getText().toString();
                city = new Select().from(City.class).where("city LIKE '%" + s + "%'").executeSingle();
                getSupportActionBar().setTitle("城市：" + city.getCity());
            }
        }
    }
}
