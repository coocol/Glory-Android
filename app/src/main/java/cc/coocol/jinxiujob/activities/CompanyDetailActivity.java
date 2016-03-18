package cc.coocol.jinxiujob.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.adapters.EnterInfoFragmentAdapter;
import cc.coocol.jinxiujob.fragments.enterinfos.EnterHomeFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseEnterItemModel;
import cc.coocol.jinxiujob.models.DetailEnterModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;

public class CompanyDetailActivity extends BaseActivity {

    @Bind(R.id.tabLayout) TabLayout tabLayout;
    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.name) TextView companyNameView;
    @Bind(R.id.c_logo) SimpleDraweeView logoView;
    @Bind(R.id.nick) TextView companyNickVIew;

    private EnterInfoFragmentAdapter adapter;

    private DetailEnterModel enterItemModel;

    private int enterpriseId;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            if (msg.what == 44) {
                showSimpleSnack("获取失败", CompanyDetailActivity.this);
            } else if (msg.what == 88) {
                refreshData();
            }
        }
    };

    private void refreshData() {
        if (enterItemModel.getNick() == null) {
            companyNickVIew.setVisibility(View.GONE);
        } else {
            companyNickVIew.setText(enterItemModel.getNick());
        }
        companyNameView.setText(enterItemModel.getName());
        ((EnterHomeFragment)adapter.getItem(0)).refreshData(enterItemModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);
        ButterKnife.bind(this);

        enterpriseId = getIntent().getIntExtra("company_id", 0);

        initView();

        showProgressDialog("正在获取");
        requestEnterInfos();
    }

    public void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("");

        final TabLayout.Tab infoTab = tabLayout.newTab().setText("企业主页");
        final TabLayout.Tab jobsTab = tabLayout.newTab().setText("招聘职位");
        tabLayout.addTab(infoTab);
        tabLayout.addTab(jobsTab);
        adapter = new EnterInfoFragmentAdapter(fragmentManager);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    infoTab.select();
                } else if (position == 1) {
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
        Uri uri = Uri.parse("http://115.28.22.98:7652/api/v1.0/static/logo/" + enterpriseId + ".jpg");
        logoView.setImageURI(uri);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void requestEnterInfos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseStatus responseStatus = new HttpClient().get(URL.ENTERPRISE + enterpriseId, null, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    enterItemModel = HttpClient.getGson().fromJson(responseStatus.getData(),
                            DetailEnterModel.class);
                    if (enterItemModel != null) {
                        handler.sendEmptyMessage(88);
                    } else {
                        handler.sendEmptyMessage(44);
                    }
                } else  {
                    handler.sendEmptyMessage(44);
                }
            }
        }).start();
    }
}
