package cc.coocol.jinxiujob.fragments.collectpages;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.adapters.EnterprisesListAdapter;
import cc.coocol.jinxiujob.adapters.JobsListAdapter;
import cc.coocol.jinxiujob.enums.EntersListType;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.fragments.BaseFragment;
import cc.coocol.jinxiujob.models.BaseEnterItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.models.HotJobItemModel;


public class CollectedJobsFragment extends BaseFragment implements JobsListAdapter.OnLastItemVisibleListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private JobsListAdapter adapter;

    private List<BaseJobItemModel> jobItemModels;

    @Override
    public String getTile() {
        return null;
    }

    public CollectedJobsFragment() {

        jobItemModels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            jobItemModels.add(new HotJobItemModel());
        }

    }


    public static CollectedJobsFragment newInstance(String param1, String param2) {
        CollectedJobsFragment fragment = new CollectedJobsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collected_all_jobs, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        adapter = new JobsListAdapter(getContext(), jobItemModels, JobListType.HotJob, this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void loadMore() {

    }
}
