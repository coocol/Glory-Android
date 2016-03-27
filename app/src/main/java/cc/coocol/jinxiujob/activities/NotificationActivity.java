package cc.coocol.jinxiujob.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.adapters.NotificationListAdapter;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.NotificationItemModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;

public class NotificationActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, NotificationListAdapter.OnLastItemVisibleListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private NotificationListAdapter adapter;
    private final int GET_SUCCESS = 8;
    private final int NO_MORE = 43;

    private final int NO_NOTIFICATION = 111;

    private int startId = 0;

    private int REFRESH = 77;
    private int GET_MORE = 99;

    private final int DELETE_OK = 65;
    private final int DELETE_FAIL = 64;

    private List<NotificationItemModel> notifications = new ArrayList<>();


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            refreshLayout.setRefreshing(false);
            switch (msg.what) {
                case NO_MORE:
                    showSimpleSnack("没有更多数据", NotificationActivity.this);
                    break;
                case GET_SUCCESS:
                    adapter.notifyDataSetChanged();
                    break;
                case NO_NOTIFICATION:
                    showSimpleSnack("没有通知", NotificationActivity.this);
                    break;
                case DELETE_OK:
                    showSimpleSnack("已删除该条数据", NotificationActivity.this);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的通知");

        initView();
        requestANotifications(REFRESH);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    void initView() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        adapter = new NotificationListAdapter(this, notifications, this);
        adapter.setOnItemClickListener(new NotificationListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                if (v.getId() == R.id.company_rl) {
                    Intent intent = new Intent(NotificationActivity.this, CompanyDetailActivity.class);
                    intent.putExtra("company_id", (int) v.getTag());
                    startActivity(intent);
                } else if (v.getId() == R.id.container || v.getId() == R.id.a_job) {
                    Intent intent = new Intent(NotificationActivity.this, JobDetailActivity.class);
                    intent.putExtra("job_id", (int) v.getTag());
                    startActivity(intent);
                } else if (v.getId() == R.id.a_delete || v.getId() == R.id.n_delete) {
                    delete((Integer) v.getTag());
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }


    private void delete(final int pos) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<String, Object>(4);
                m.put("action", "delete");
                m.put("token", MyConfig.token);
                m.put("nid", notifications.get(pos).getId());
                ResponseStatus s = new HttpClient().post(URL.NOTIFICATIONS + MyConfig.uid,m,false);
                if (s != null && s.getStatus().equals("success")) {
                    notifications.remove(pos);
                    handler.sendEmptyMessage(DELETE_OK);
                } else {
                    handler.sendEmptyMessage(DELETE_FAIL);
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        requestANotifications(REFRESH);
    }

    public void requestANotifications(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(4);
                m.put("token", MyConfig.token);
                if (type == GET_MORE) {
                    m.put("start_id", startId);
                }
                ResponseStatus responseStatus = new HttpClient().get(URL.NOTIFICATIONS + MyConfig.uid, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    List<NotificationItemModel> models = HttpClient.getGson().fromJson(responseStatus.getData(),
                            new TypeToken<ArrayList<NotificationItemModel>>() {
                            }.getType());
                    if (type == REFRESH) {
                        if (models != null) {
                            notifications.clear();
                            for (NotificationItemModel allJobItemModel : models) {
                                notifications.add(allJobItemModel);
                            }
                            if (notifications.size() > 0) {
                                startId = notifications.size();
                            }
                            handler.sendEmptyMessage(GET_SUCCESS);
                        } else {
                            handler.sendEmptyMessage(NO_NOTIFICATION);
                        }
                    } else if (type == GET_MORE) {
                        if (models == null || models.size() == 0) {
                            handler.sendEmptyMessage(NO_MORE);
                        } else {
                            for (NotificationItemModel allJobItemModel : models) {
                                notifications.add(allJobItemModel);
                            }
                            if (notifications.size() > 0) {
                                startId = notifications.size();
                            }
                            handler.sendEmptyMessage(GET_SUCCESS);
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void loadMore() {
        requestANotifications(GET_MORE);
    }
}
