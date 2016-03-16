package cc.coocol.jinxiujob.fragments.jobpages;

import android.content.Intent;
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
import cc.coocol.jinxiujob.activities.CompanyDetailActivity;
import cc.coocol.jinxiujob.activities.JobDetailActivity;
import cc.coocol.jinxiujob.activities.MainActivity;
import cc.coocol.jinxiujob.adapters.JobsListAdapter;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.fragments.BaseFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;


public class AllJobsFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        JobsListAdapter.OnLastItemVisibleListener {


    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private JobsListAdapter adapter;

    private static final int GET_JOBS_SUCCESS = 8;
    private static final int NO_MORE = 43;

    private int startJobId = 0;

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

    private List<BaseJobItemModel> jobItemModels = new ArrayList<>();

    @Override
    public String getTile() {
        return null;
    }


    public AllJobsFragment() {
    }

    public static AllJobsFragment newInstance(String param1, String param2) {
        AllJobsFragment fragment = new AllJobsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_jobs, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(this);
        adapter = new JobsListAdapter(getContext(), jobItemModels, JobListType.AllJob, this);
        adapter.setOnItemClickListener(new JobsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                if (v.getId() == R.id.company) {
                    Intent intent = new Intent(getContext(), CompanyDetailActivity.class);
                    intent.putExtra("company_id", (int)v.getTag());
                    startActivity(intent);
                } else if (v.getId() == R.id.container) {
                    Intent intent = new Intent(getContext(), JobDetailActivity.class);
                    intent.putExtra("job_id", (int)v.getTag());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (jobItemModels == null || jobItemModels.size() == 0) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            requestAllJobs(REFRESH);
        }
    }

    @Override
    public void onRefresh() {
        requestAllJobs(REFRESH);
    }

    public void requestAllJobs(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(4);
                m.put("type", "all");
                if (type == GET_MORE) {
                    m.put("start_id", startJobId);
                }
                m.put("city_id", MyConfig.cityId);
                ResponseStatus responseStatus = new HttpClient().get(URL.ALL_JOBS, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    List<AllJobItemModel> models = HttpClient.getGson().fromJson(responseStatus.getData(),
                            new TypeToken<ArrayList<AllJobItemModel>>(){}.getType());
                    if (type == REFRESH) {
                        if (models != null) {
                            jobItemModels.clear();
                            for (AllJobItemModel allJobItemModel: models) {
                                jobItemModels.add(allJobItemModel);
                            }
                            if (jobItemModels.size() > 0) {
                                startJobId = jobItemModels.get(jobItemModels.size() - 1).getId();
                            }
                            handler.sendEmptyMessage(GET_JOBS_SUCCESS);
                        }
                    } else if (type == GET_MORE) {
                        if (models == null || models.size() == 0) {
                            handler.sendEmptyMessage(NO_MORE);
                        } else {
                            for (AllJobItemModel allJobItemModel: models) {
                                jobItemModels.add(allJobItemModel);
                            }
                            if (jobItemModels.size() > 0) {
                                startJobId = jobItemModels.get(jobItemModels.size() - 1).getId();
                            }
                            handler.sendEmptyMessage(GET_JOBS_SUCCESS);
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void loadMore() {
        requestAllJobs(GET_MORE);
    }
}
