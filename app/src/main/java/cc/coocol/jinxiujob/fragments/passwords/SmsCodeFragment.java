package cc.coocol.jinxiujob.fragments.passwords;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.roundview.RoundFrameLayout;
import com.nispok.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.PasswordActivity;
import cc.coocol.jinxiujob.fragments.BaseFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;

public class SmsCodeFragment extends BaseFragment implements View.OnClickListener{

    private EditText phoneView;
    private EditText codeView;
    private RoundFrameLayout recodeButton;
    private TextView recodeTextView;
    private RoundFrameLayout goButton;

    private String phone;
    private String code;

    private int deadline = 60;

    private Timer timer;
    private Handler handler;

    MyTask<String, ResponseStatus, ResponseStatus> myTask;

    public SmsCodeFragment() {
    }


    public static SmsCodeFragment newInstance(String param1, String param2) {
        SmsCodeFragment fragment = new SmsCodeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms_code, container, false);
        phoneView = (EditText) view.findViewById(R.id.phone);
        codeView = (EditText) view.findViewById(R.id.code);
        recodeButton = (RoundFrameLayout) view.findViewById(R.id.recode);
        recodeTextView = (TextView) view.findViewById(R.id.recode_text);
        goButton = (RoundFrameLayout) view.findViewById(R.id.go);
        recodeButton.setOnClickListener(this);
        goButton.setOnClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (deadline == 0) {
                    recodeButton.setEnabled(true);
                    recodeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    recodeTextView.setText("发送验证码");
                    if (timer != null) {
                        timer.cancel();
                    }
                } else {
                    recodeTextView.setText("重发（" + deadline + "）");
                }
            }
        };

        return view;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.recode) {
            getPhoneCode();
        } else if (id == R.id.go) {
            code = codeView.getText().toString();
            if (code == null || code.length() < 6) {
                codeView.setError("填写验证码");
            } else {
                myTask = new MyTask<>(2);
                myTask.execute();
            }
        }
    }


    public void getPhoneCode() {
        phone = phoneView.getText().toString();
        if (phone == null || phone.length() < 11) {
            phoneView.setError("请填写电话号码");
            return;
        }
        phoneView.setEnabled(false);
        countTime();
        myTask = new MyTask<>(1);
        myTask.execute();
    }

    private void countTime() {
        deadline = 60;
        recodeButton.setEnabled(false);
        timer = new Timer("");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                deadline--;
                handler.sendEmptyMessage(deadline);
            }
        }, 0, 1000);
    }


    class MyTask<S, R, R1> extends AsyncTask<String, ResponseStatus, ResponseStatus> {

        private int flag;

        public MyTask(int flag) {
            this.flag = flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((PasswordActivity) getActivity()).showProgressDialog("正在获取");
        }

        @Override
        protected ResponseStatus doInBackground(String... s) {
            Map<String, Object> args = new HashMap<>(4);
            if (flag == 1) { //code
                args.put("phone", phone);
                return new HttpClient().get(URL.PASSWORD_CODE_GET, args, false);
            } else {  //register
                args.put("phone", phone);
                args.put("code", code);
                return new HttpClient().post(URL.PASSWORD_CODE_CONFIRM, args, false);
            }
        }

        @Override
        protected void onPostExecute(ResponseStatus resp) {
            PasswordActivity activity = (PasswordActivity) getActivity();
            activity.dismissProgressDialog();
            if (resp == null) {
                return;
            }
            if (resp.getStatus().equals("fail")) {
                Snackbar.with(getContext()).text(resp.getMsg()).show(activity);
            } else {
                if (flag == 1) { //code
                    ;
                } else if (flag == 2) {
                    //Snackbar.with(getContext()).text("修改成功").show(activity);
                    //activity.gotoActivity(LoginActivity.class);
                    activity.setCode(code);
                    activity.setPhone(phone);
                    activity.changeToPassword();
                }
            }
        }
    }

}
