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
import cc.coocol.jinxiujob.adapters.EnterpriseFragmentAdapter;
import cc.coocol.jinxiujob.adapters.JobFragmentAdapter;


public class EnterpriseFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EnterpriseFragmentAdapter adapter;

    public EnterpriseFragment() {
    }

    public static EnterpriseFragment newInstance(String param1, String param2) {
        EnterpriseFragment fragment = new EnterpriseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enterprise, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        final TabLayout.Tab allEntersTab = tabLayout.newTab().setText("全部");
        final TabLayout.Tab hotEntersTab = tabLayout.newTab().setText("附近");
        final TabLayout.Tab nearbyEntersTab = tabLayout.newTab().setText("热门");
        tabLayout.addTab(allEntersTab);
        tabLayout.addTab(nearbyEntersTab);
        tabLayout.addTab(hotEntersTab);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        adapter = new EnterpriseFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    allEntersTab.select();
                } else if (position == 1) {
                    nearbyEntersTab.select();
                } else if (position == 2) {
                    hotEntersTab.select();
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
