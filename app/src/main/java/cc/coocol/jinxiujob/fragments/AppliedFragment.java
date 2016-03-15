package cc.coocol.jinxiujob.fragments;

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

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.adapters.AppliedListAdapter;
import cc.coocol.jinxiujob.adapters.JobsListAdapter;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.models.AppliedJobItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;

public class AppliedFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private AppliedListAdapter adapter;

    private List<BaseJobItemModel> jobItemModels;

    public AppliedFragment() {

        jobItemModels = new ArrayList<>();
        for (int i = 0; i< 10; i++) {
            jobItemModels.add(new AppliedJobItemModel());
        }
    }


    public static AppliedFragment newInstance(String param1, String param2) {
        AppliedFragment fragment = new AppliedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applied, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        adapter = new AppliedListAdapter(getContext(), jobItemModels);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
