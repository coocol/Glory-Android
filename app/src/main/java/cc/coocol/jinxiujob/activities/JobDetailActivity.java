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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.models.JobDetailModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;
import cc.coocol.jinxiujob.utils.StringUtil;

public class JobDetailActivity extends BaseActivity {

    @Bind(R.id.job_name)
    TextView jobNameView;
    @Bind(R.id.apply_button)
    Button applyButton;
    @Bind(R.id.collect_button)
    Button collectButton;
    @Bind(R.id.company)
    LinearLayout companyLayout;
    @Bind(R.id.company_name)
    TextView companyNameView;
    @Bind(R.id.logo)
    SimpleDraweeView logoView;
    @Bind(R.id.time)
    TextView timeTextView;
    @Bind(R.id.apply)
    TextView applyView;
    @Bind(R.id.collect)
    TextView collectView;
    @Bind(R.id.address)
    TextView addressView;
    @Bind(R.id.period)
    TextView periodView;
    @Bind(R.id.requirement)
    TextView requirementView;
    @Bind(R.id.other)
    TextView otherView;
    @Bind(R.id.content)
    TextView contentView;
    @Bind(R.id.salary)
    TextView salaryView;
    @Bind(R.id.apply_progress)
    ProgressBar applyPro;
    @Bind(R.id.collect_progress) ProgressBar collectPro;

    @OnClick(R.id.collect_button)
    void collectJob() {
        collect(jobId);
    }

    @OnClick(R.id.apply_button)
    void applyJob(){
        apply(jobId);
    }


    final int GET_JOB_SUCCESS = 45;
    final int APPLY_SUCCESS = 56;
    final int COLLECT_SUCCESS = 51;
    final int NEED_RESUME = 46;
    final int GET_STATUS_SUCCESS = 72;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case GET_JOB_SUCCESS:
                    refreshData();
                    requestStatus(jobId);
                    break;
                case GET_STATUS_SUCCESS:
                    applyPro.setVisibility(View.GONE);
                    collectPro.setVisibility(View.GONE);
                    if (jobStatus.apply) {
                        applyButton.setText("已申请");
                        applyButton.setEnabled(false);
                    }
                    if (jobStatus.collect) {
                        collectButton.setText("已收藏");
                        collectButton.setEnabled(false);
                    }
                    break;
                case APPLY_SUCCESS:
                    showSimpleSnack("申请成功", JobDetailActivity.this);
                    applyButton.setText("已申请");
                    applyButton.setEnabled(false);
                    break;
                case COLLECT_SUCCESS:
                    showSimpleSnack("收藏成功", JobDetailActivity.this);
                    collectButton.setText("已收藏");
                    collectButton.setEnabled(false);
                    break;
                case NEED_RESUME:
                    showSimpleSnack("请先完善简历", JobDetailActivity.this);
                    break;
                default:
                    showSimpleSnack("操作失败", JobDetailActivity.this);
                    break;
            }
        }
    };

    private JobDetailModel jobItemModel;
    private int jobId;
    private JobStatus jobStatus;

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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
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
        collectView.setText(jobItemModel.getCollect() + "人收藏");
        applyView.setText(jobItemModel.getApply() + "人申请");
        timeTextView.setText(jobItemModel.getTime());
        addressView.setText(jobItemModel.getAddress());
        companyNameView.setText(jobItemModel.getCompany());
        requirementView.setText(jobItemModel.getRequirement());
        otherView.setText(jobItemModel.getOther());
        salaryView.setText(jobItemModel.getSalary());
        contentView.setText(jobItemModel.getContent());
        periodView.setText(jobItemModel.getPeriod());
        Uri uri = Uri.parse("http://115.28.22.98:7652/api/v1.0/static/logo/" + jobItemModel.getCompanyId() + ".jpg");
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
                        handler.sendEmptyMessage(GET_JOB_SUCCESS);
                    }
                }
            }
        }).start();
    }

    public void requestStatus(final int jobId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>();
                map.put("job_id", jobId);
                map.put("token", MyConfig.token);
                ResponseStatus responseStatus = new HttpClient().get(URL.JOB_STATUS + MyConfig.uid, map, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    jobStatus = HttpClient.getGson().fromJson(responseStatus.getData(), JobStatus.class);
                    if (jobStatus != null) {
                        handler.sendEmptyMessage(GET_STATUS_SUCCESS);
                    }
                }
            }
        }).start();
    }

    public void apply(final int jobId) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>(4);
                map.put("user_id",MyConfig.uid);
                map.put("token", MyConfig.token);
                map.put("action", "apply");
                ResponseStatus responseStatus = new HttpClient().post(URL.JOB + jobId, map, false);
                if (responseStatus != null && responseStatus.getStatus() != null) {
                    if (responseStatus.getStatus().equals("success")) {
                        handler.sendEmptyMessage(APPLY_SUCCESS);
                    } else if (responseStatus.getStatus().equals("fail")) {
                        if (responseStatus.getMsg().equals("need resume")) {
                            handler.sendEmptyMessage(NEED_RESUME);
                        }
                    }
                }
            }
        }).start();
    }

    public void collect(final int jobId) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>(4);
                map.put("user_id",MyConfig.uid);
                map.put("token", MyConfig.token);
                map.put("action", "collect");
                ResponseStatus responseStatus = new HttpClient().post(URL.JOB + jobId, map, false);
                if (responseStatus != null && responseStatus.getStatus() != null) {
                    if (responseStatus.getStatus().equals("success")) {
                        handler.sendEmptyMessage(COLLECT_SUCCESS);
                    } else if (responseStatus.getStatus().equals("fail")) {
                        if (responseStatus.getMsg().equals("need resume")) {
                            handler.sendEmptyMessage(NEED_RESUME);
                        }
                    }
                }
            }
        }).start();
    }



    class JobStatus {
        public boolean apply;
        public boolean collect;
    }
}
