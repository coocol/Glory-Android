package cc.coocol.jinxiujob.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.adapters.JobFragmentAdapter;


public class JobFragment extends BaseFragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private JobFragmentAdapter adapter;

    @Override
    public String getTile() {
        return "职位";
    }

    public JobFragment() {

    }

    public static JobFragment newInstance(String param1, String param2) {
        JobFragment fragment = new JobFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_job, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        final TabLayout.Tab allJobsTab = tabLayout.newTab().setText("全部");
        final TabLayout.Tab hotJobsTab = tabLayout.newTab().setText("热门");
        final TabLayout.Tab nearbyJobsTab = tabLayout.newTab().setText("附近");
        tabLayout.addTab(allJobsTab);
        tabLayout.addTab(hotJobsTab);
        tabLayout.addTab(nearbyJobsTab);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        adapter = new JobFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    allJobsTab.select();
                } else if (position == 2) {
                    nearbyJobsTab.select();
                } else if (position == 1) {
                    hotJobsTab.select();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
}
