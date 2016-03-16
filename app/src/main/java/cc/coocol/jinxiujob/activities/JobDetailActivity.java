package cc.coocol.jinxiujob.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.models.JobDetailModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;

public class JobDetailActivity extends BaseActivity {

    @Bind(R.id.job_name)  TextView jobNameView;
    @Bind(R.id.status)  Button statusView;
    @Bind(R.id.company)  LinearLayout companyLayout;
    @Bind(R.id.company_name)  TextView companyNameView;
    @Bind(R.id.logo)  SimpleDraweeView logoView;
    @Bind(R.id.time)  TextView timeTextView;
    @Bind(R.id.apply)  TextView applyView;
    @Bind(R.id.collect)  TextView collectView;
    @Bind(R.id.address)  TextView addressView;
    @Bind(R.id.period)  TextView periodView;
    @Bind(R.id.requirement)  TextView requirementView;
    @Bind(R.id.other)  TextView otherView;
    @Bind(R.id.content)  TextView contentView;
    @Bind(R.id.salary)  TextView salaryView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            if (msg.what == 88) {
                refreshData();
            } else {
                showSimpleSnack("获取失败，请重试", JobDetailActivity.this);
            }
        }
    };

    private JobDetailModel jobItemModel;
    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        ButterKnife.bind(this);

        initView();

        jobId = getIntent().getIntExtra("job_id", 0);

        showProgressDialog("正在获取");

        requestJob(jobId);
    }

    public void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_clear_mtrl_alpha);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("职位详情");
        }
        companyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobItemModel != null) {
                    Intent intent = new Intent(JobDetailActivity.this, CompanyDetailActivity.class);
                    intent.putExtra("company_id", jobItemModel.getCompanyId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void refreshData() {
        jobNameView.setText(jobItemModel.getName());
        collectView.setText(jobItemModel.getCollect()+"人收藏");
        applyView.setText(jobItemModel.getApply() + "人申请");
        timeTextView.setText(jobItemModel.getTime());
        addressView.setText(jobItemModel.getAddress());
        companyNameView.setText(jobItemModel.getCompany());
        requirementView.setText(jobItemModel.getRequirement());
        otherView.setText(jobItemModel.getOther());
        salaryView.setText(jobItemModel.getSalary());
        contentView.setText(jobItemModel.getContent());
        periodView.setText(jobItemModel.getPeriod());
        Uri uri = Uri.parse("http://115.28.22.98/api/v1.0/static/logo/" + jobItemModel.getCompanyId() + ".jpg");
        logoView.setImageURI(uri);
    }


    public void requestJob(final int jobId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseStatus responseStatus = new HttpClient().get(URL.JOB + jobId, null, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    jobItemModel = HttpClient.getGson().fromJson(responseStatus.getData(), JobDetailModel.class);
                    if (jobItemModel != null) {
                        handler.sendEmptyMessage(88);
                    } else {
                        handler.sendEmptyMessage(44);
                    }
                }
            }
        }).start();
    }
}
