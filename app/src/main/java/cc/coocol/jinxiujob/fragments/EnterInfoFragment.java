package cc.coocol.jinxiujob.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.adapters.EnterInfoFragmentAdapter;
import cc.coocol.jinxiujob.adapters.JobFragmentAdapter;


public class EnterInfoFragment extends BaseFragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EnterInfoFragmentAdapter adapter;

    @Override
    public String getTile() {
        return null;
    }

    public EnterInfoFragment() {

    }

    public static EnterInfoFragment newInstance(String param1, String param2) {
        EnterInfoFragment fragment = new EnterInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter_jobs, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        final TabLayout.Tab infoTab = tabLayout.newTab().setText("企业主页");
        final TabLayout.Tab jobsTab = tabLayout.newTab().setText("招聘职位");
        tabLayout.addTab(infoTab);
        tabLayout.addTab(jobsTab);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        adapter = new EnterInfoFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    infoTab.select();
                }else if (position == 1) {
                    jobsTab.select();
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
