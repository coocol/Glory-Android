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
import cc.coocol.jinxiujob.adapters.SearchFragmentAdapter;


public class SearchFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SearchFragmentAdapter adapter;

    public SearchFragment() {

    }
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        final TabLayout.Tab jobTab = tabLayout.newTab().setText(getResources().getString(R.string.tab_title_job));
        final TabLayout.Tab enterTab = tabLayout.newTab().setText(getResources().getString(R.string.tab_title_enters));
        tabLayout.addTab(jobTab);
        tabLayout.addTab(enterTab);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        adapter = new SearchFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    jobTab.select();
                } else if (position == 1) {
                    enterTab.select();
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
