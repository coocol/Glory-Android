package cc.coocol.jinxiujob.fragments.jobpages;

import android.graphics.Color;
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

import com.google.gson.reflect.TypeToken;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.MainActivity;
import cc.coocol.jinxiujob.adapters.JobsListAdapter;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.models.HotJobItemModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;


public class HotJobsFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        JobsListAdapter.OnLastItemVisibleListener {


    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private JobsListAdapter adapter;

    private List<BaseJobItemModel> hotJobItemModels = new ArrayList<>();

    private static final int GET_JOBS_SUCCESS = 8;
    private static final int NO_MORE = 43;

    private int startId = 0;

    private int REFRESH = 77;
    private int GET_MORE = 99;

    private MainActivity activity;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NO_MORE:
                    activity.showSimpleSnack("没有更多数据", activity);
                    break;
                case GET_JOBS_SUCCESS:
                    adapter.notifyDataSetChanged();
                    break;
            }

            refreshLayout.setRefreshing(false);
        }
    };


    public HotJobsFragment() {

    }


    public static HotJobsFragment newInstance(String param1, String param2) {
        HotJobsFragment fragment = new HotJobsFragment();
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

        View view = inflater.inflate(R.layout.fragment_hot_jobs, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        hotJobItemModels = new ArrayList<>();
        adapter = new JobsListAdapter(getContext(), hotJobItemModels, JobListType.HotJob, this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (hotJobItemModels == null || hotJobItemModels.size() == 0) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            requestHotJobs(REFRESH);
        }
    }

    public void requestHotJobs(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(4);
                m.put("type", "hot");
                if (type == GET_MORE) {
                    m.put("start_id", startId);
                }
                m.put("city_id", MyConfig.cityId);
                ResponseStatus responseStatus = new HttpClient().get(URL.ALL_JOBS, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    List<HotJobItemModel> models = HttpClient.getGson().fromJson(responseStatus.getData(),
                            new TypeToken<ArrayList<HotJobItemModel>>(){}.getType());
                    if (type == REFRESH) {
                        if (models != null) {
                            hotJobItemModels.clear();
                            for (HotJobItemModel hotJobItemModel: models) {
                                hotJobItemModels.add(hotJobItemModel);
                            }
                            startId = hotJobItemModels.size();
                            handler.sendEmptyMessage(GET_JOBS_SUCCESS);
                        } else {
                            handler.sendEmptyMessage(NO_MORE);
                        }
                    } else if (type == GET_MORE) {
                        if (models == null || models.size() == 0) {
                            handler.sendEmptyMessage(NO_MORE);
                        } else {
                            for (HotJobItemModel hotJobItemModel: models) {
                                hotJobItemModels.add(hotJobItemModel);
                            }
                            startId = hotJobItemModels.size();
                            handler.sendEmptyMessage(GET_JOBS_SUCCESS);
                        }
                    }
                }
            }
        }).start();
    }


    @Override
    public void onRefresh() {
        requestHotJobs(REFRESH);
    }

    @Override
    public void loadMore() {
        requestHotJobs(GET_MORE);
    }
}
