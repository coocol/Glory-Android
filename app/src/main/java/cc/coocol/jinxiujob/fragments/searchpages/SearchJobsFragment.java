package cc.coocol.jinxiujob.fragments.searchpages;

import android.app.Activity;
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
import cc.coocol.jinxiujob.activities.JobDetailActivity;
import cc.coocol.jinxiujob.activities.MainActivity;
import cc.coocol.jinxiujob.adapters.JobsListAdapter;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.fragments.BaseFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.models.JobItemModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;


public class SearchJobsFragment extends BaseFragment implements
        JobsListAdapter.OnLastItemVisibleListener,
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private JobsListAdapter adapter;

    private List<BaseJobItemModel> jobItemModels;

    private String query;
    private static final int GET_JOBS_SUCCESS = 8;
    private static final int NO_MORE = 43;

    private int startJobId = 0;

    private int REFRESH = 77;
    private int GET_MORE = 99;

    private MainActivity activity;

    public void setQuery(String query) {
        if (this.query != null && !this.query.equals(query)) {
            this.query = query;
            searchAllJobs(REFRESH);
        }
    }

    private Handler handler = new Handler() {
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


    @Override
    public String getTile() {
        return null;
    }

    public SearchJobsFragment() {

        jobItemModels = new ArrayList<>();

    }


    public static SearchJobsFragment newInstance(String param1, String param2) {
        SearchJobsFragment fragment = new SearchJobsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (query != null && jobItemModels.size() == 0) {
//            refreshLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    refreshLayout.setRefreshing(true);
//                }
//            });
//            searchAllJobs(REFRESH);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_all_jobs, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
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
    public void loadMore() {
        searchAllJobs(GET_MORE);
    }

    public void search(String query) {
        if (query != null) {
            this.query = query;
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            searchAllJobs(REFRESH);
        }
    }

    public void searchAllJobs(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(4);
                m.put("type", "search");
                if (type == GET_MORE) {
                    m.put("start_id", startJobId);
                }
                m.put("query", query);
                m.put("city_id", MyConfig.cityId);
                ResponseStatus responseStatus = new HttpClient().get(URL.ALL_JOBS, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    List<AllJobItemModel> models = HttpClient.getGson().fromJson(responseStatus.getData(),
                            new TypeToken<ArrayList<AllJobItemModel>>() {
                            }.getType());
                    if (type == REFRESH) {
                        if (models != null) {
                            jobItemModels.clear();
                            for (AllJobItemModel allJobItemModel : models) {
                                jobItemModels.add(allJobItemModel);
                            }
                            if (jobItemModels.size() > 0) {
                                startJobId = jobItemModels.size();
                            }
                            handler.sendEmptyMessage(GET_JOBS_SUCCESS);
                        }
                    } else if (type == GET_MORE) {
                        if (models == null || models.size() == 0) {
                            handler.sendEmptyMessage(NO_MORE);
                        } else {
                            for (AllJobItemModel allJobItemModel : models) {
                                jobItemModels.add(allJobItemModel);
                            }
                            if (jobItemModels.size() > 0) {
                                startJobId = jobItemModels.size();
                            }
                            handler.sendEmptyMessage(GET_JOBS_SUCCESS);
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        searchAllJobs(REFRESH);
    }
}
