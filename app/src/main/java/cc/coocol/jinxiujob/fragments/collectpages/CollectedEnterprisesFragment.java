package cc.coocol.jinxiujob.fragments.collectpages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.CompanyDetailActivity;
import cc.coocol.jinxiujob.activities.MainActivity;
import cc.coocol.jinxiujob.adapters.EnterprisesListAdapter;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.enums.EntersListType;
import cc.coocol.jinxiujob.fragments.BaseFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.AllEnterItemModel;
import cc.coocol.jinxiujob.models.BaseEnterItemModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;


public class CollectedEnterprisesFragment extends BaseFragment implements EnterprisesListAdapter.OnLastItemVisibleListener,
        SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private EnterprisesListAdapter adapter;

    private List<BaseEnterItemModel> enterItemModels = new ArrayList<>();
    private static final int GET_SUCCESS = 8;
    private static final int NO_MORE = 43;

    private int startId = 0;

    private MainActivity activity;

    private int REFRESH = 77;
    private int GET_MORE = 99;

    private final int CANCEL_OK = 45;
    private final int CANCEL_FAIL = 46;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            activity.dismissProgressDialog();
            refreshLayout.setRefreshing(false);
            super.handleMessage(msg);
            switch (msg.what) {
                case NO_MORE:
                    activity.showSimpleSnack("没有更多数据", activity);
                    break;
                case GET_SUCCESS:
                    adapter.notifyDataSetChanged();
                    break;
                case CANCEL_FAIL:
                    activity.showSimpleSnack("操作失败", activity);
                    break;
                case CANCEL_OK:
                    activity.showSimpleSnack("已取消收藏", activity);
                    adapter.notifyDataSetChanged();
                    break;
            }

        }
    };



    @Override
    public String getTile() {
        return null;
    }

    public CollectedEnterprisesFragment() {
    }

    public static CollectedEnterprisesFragment newInstance(String param1, String param2) {
        CollectedEnterprisesFragment fragment = new CollectedEnterprisesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_collected_all_enters, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        adapter = new EnterprisesListAdapter(getContext(), enterItemModels, EntersListType.Collect, this);
        adapter.setOnItemClickListener(new EnterprisesListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                if (v.getId() == R.id.container) {
                    Intent intent = new Intent(getContext(), CompanyDetailActivity.class);
                    intent.putExtra("company_id", (int) v.getTag());
                    startActivity(intent);
                } else if (v.getId() == R.id.option) {
                    int p = (Integer) v.getTag();
                    cancelCollect(p, enterItemModels.get(p).getCompanyId());
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (enterItemModels == null || enterItemModels.size() == 0) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            requestEnters(REFRESH);
        }
    }

    public void requestEnters(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(4);
                if (type == GET_MORE) {
                    m.put("start_id", startId);
                }
                m.put("token", MyConfig.token);
                ResponseStatus responseStatus = new HttpClient().get(URL.USER_COLLECTIONS_ENTERS + MyConfig.uid, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    List<AllEnterItemModel> models = HttpClient.getGson().fromJson(responseStatus.getData(),
                            new TypeToken<ArrayList<AllEnterItemModel>>() {
                            }.getType());
                    if (type == REFRESH) {
                        if (models != null) {
                            enterItemModels.clear();
                            for (AllEnterItemModel enterItemModel : models) {
                                enterItemModels.add(enterItemModel);
                            }
                            if (enterItemModels.size() > 0) {
                                startId = enterItemModels.size();
                            }
                            handler.sendEmptyMessage(GET_SUCCESS);
                        } else {
                            handler.sendEmptyMessage(85);
                        }
                    } else if (type == GET_MORE) {
                        if (models == null || models.size() == 0) {
                            handler.sendEmptyMessage(NO_MORE);
                        } else {
                            for (AllEnterItemModel enterItemModel : models) {
                                enterItemModels.add(enterItemModel);
                            }
                            if (enterItemModels.size() > 0) {
                                startId = enterItemModels.size();
                            }
                            handler.sendEmptyMessage(GET_SUCCESS);
                        }
                        handler.sendEmptyMessage(85);
                    } else {
                        handler.sendEmptyMessage(85);
                    }
                }
            }
        }).start();
    }

    public void cancelCollect(final int position, final int eid) {
        activity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(4);
                m.put("enterprise_id", eid);
                m.put("action","discollect");
                m.put("user_id", MyConfig.uid);
                m.put("token", MyConfig.token);
                ResponseStatus responseStatus = new HttpClient().post(URL.ENTERPRISE + eid, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    enterItemModels.remove(position);
                    handler.sendEmptyMessage(CANCEL_OK);
                } else {
                    handler.sendEmptyMessage(CANCEL_FAIL);
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        requestEnters(REFRESH);
    }

    @Override
    public void loadMore() {
        requestEnters(GET_MORE);
    }
}
