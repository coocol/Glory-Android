package cc.coocol.jinxiujob.fragments.enterpages;

import android.content.Context;
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
import cc.coocol.jinxiujob.activities.MainActivity;
import cc.coocol.jinxiujob.adapters.EnterprisesListAdapter;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.enums.EntersListType;
import cc.coocol.jinxiujob.fragments.BaseFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.AllEnterItemModel;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseEnterItemModel;
import cc.coocol.jinxiujob.models.HotEnterItemModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;


public class HotEnterprisesFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, EnterprisesListAdapter.OnLastItemVisibleListener{

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private EnterprisesListAdapter adapter;

    private List<BaseEnterItemModel> enterItemModels;

    private static final int GET_SUCCESS = 8;
    private static final int NO_MORE = 43;

    private int startId = 0;

    private MainActivity activity;

    private int REFRESH = 77;
    private int GET_MORE = 99;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NO_MORE:
                    activity.showSimpleSnack("没有更多数据", activity);
                    break;
                case GET_SUCCESS:
                    adapter.notifyDataSetChanged();
                    break;
            }
            refreshLayout.setRefreshing(false);
        }
    };


    @Override
    public String getTile() {
        return null;
    }

    public HotEnterprisesFragment() {

    }

    public static AllEnterprisesFragment newInstance(String param1, String param2) {
        AllEnterprisesFragment fragment = new AllEnterprisesFragment();
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
        View view = inflater.inflate(R.layout.fragment_hot_enters, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        enterItemModels = new ArrayList<>();
        adapter = new EnterprisesListAdapter(getContext(), enterItemModels, EntersListType.HotEnters, this);
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
            requestHotEnters(REFRESH);
        }
    }


    public void requestHotEnters(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(4);
                m.put("type", "hot");
                if (type == GET_MORE) {
                    m.put("start_id", startId);
                }
                m.put("city_id", MyConfig.cityId);
                ResponseStatus responseStatus = new HttpClient().get(URL.ALL_ENTERPRISES, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    List<HotEnterItemModel> models = HttpClient.getGson().fromJson(responseStatus.getData(),
                            new TypeToken<ArrayList<HotEnterItemModel>>() {
                            }.getType());
                    if (type == REFRESH) {
                        if (models != null) {
                            enterItemModels.clear();
                            for (HotEnterItemModel enterItemModel : models) {
                                enterItemModels.add(enterItemModel);
                            }
                            if (enterItemModels.size() > 0) {
                                startId = enterItemModels.size();
                            }
                            handler.sendEmptyMessage(GET_SUCCESS);
                        }
                    } else if (type == GET_MORE) {
                        if (models == null || models.size() == 0) {
                            handler.sendEmptyMessage(NO_MORE);
                        } else {
                            for (HotEnterItemModel enterItemModel : models) {
                                enterItemModels.add(enterItemModel);
                            }if (enterItemModels.size() > 0) {
                                startId = enterItemModels.size();
                            }
                            handler.sendEmptyMessage(GET_SUCCESS);
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        requestHotEnters(REFRESH);
    }

    @Override
    public void loadMore() {
        requestHotEnters(GET_MORE);
    }
}
