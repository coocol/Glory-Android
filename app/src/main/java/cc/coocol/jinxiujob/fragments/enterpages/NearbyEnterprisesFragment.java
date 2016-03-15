package cc.coocol.jinxiujob.fragments.enterpages;

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
import cc.coocol.jinxiujob.enums.EntersListType;
import cc.coocol.jinxiujob.models.AllEnterItemModel;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseEnterItemModel;
import cc.coocol.jinxiujob.models.HotEnterItemModel;
import cc.coocol.jinxiujob.models.NearbyEnterItemModel;

public class NearbyEnterprisesFragment extends Fragment implements EnterprisesListAdapter.OnLastItemVisibleListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private EnterprisesListAdapter adapter;

    private List<BaseEnterItemModel> enterItemModels;

    public NearbyEnterprisesFragment() {

        enterItemModels = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            enterItemModels.add(new NearbyEnterItemModel());
        }
    }

    public static NearbyEnterprisesFragment newInstance(String param1, String param2) {
        NearbyEnterprisesFragment fragment = new NearbyEnterprisesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby_enters, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).size(12).build());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        adapter = new EnterprisesListAdapter(getContext(), enterItemModels, EntersListType.NearbyEnters, this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void loadMore() {

    }
}
