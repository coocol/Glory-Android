package cc.coocol.jinxiujob.fragments.logins;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.roundview.RoundFrameLayout;
import com.nispok.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.LoginActivity;
import cc.coocol.jinxiujob.fragments.BaseFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;
import cc.coocol.jinxiujob.utils.StringUtil;

public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    private EditText phoneView;
    private EditText passView;
    private EditText codeView;
    private RoundFrameLayout goButton;
    private RoundFrameLayout recodeButton;
    private TextView recodeTextView;
    private TextView loginView;

    private String phone;
    private String password;
    private String code;
    private int deadline = 60;

    private Timer timer;
    private Handler handler;

    MyTask<String, ResponseStatus, ResponseStatus> myTask;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        phoneView = (EditText) view.findViewById(R.id.phone);
        phoneView.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        passView = (EditText) view.findViewById(R.id.pass);
        codeView = (EditText) view.findViewById(R.id.code);
        loginView = (TextView) view.findViewById(R.id.to_login);
        recodeTextView = (TextView) view.findViewById(R.id.recode_text);
        recodeButton = (RoundFrameLayout) view.findViewById(R.id.recode);
        goButton = (RoundFrameLayout) view.findViewById(R.id.go);
        recodeButton.setOnClickListener(this);
        goButton.setOnClickListener(this);
        loginView.setOnClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (deadline == 0) {
                    recodeButton.setEnabled(true);
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
        if (id == R.id.go) {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
            register();
        } else if (id == R.id.recode) {
            getPhoneCode();
        } else if (id == R.id.to_login) {
            if (timer != null) {
                timer.cancel();
            }
            ((LoginActivity) getActivity()).changeToLogin(null);
        }
    }

    private void register() {
        if (checkFormat()) {
            code = codeView.getText().toString();
            if (code != null && code.length() > 3) {
                myTask = new MyTask<>(2);
                myTask.execute();
            }
        }
    }

    private boolean checkFormat() {
        phone = phoneView.getText().toString();
        if (phone == null || phone.length() < 11) {
            phoneView.setError("请填写电话号码");
            return false;
        }
        password = passView.getText().toString();
        if (password == null || password.length() < 6) {
            passView.setError("密码不能少于6个字符");
            return false;
        }
        return true;
    }


    public void getPhoneCode() {
        if (checkFormat()) {
            setEnable(false);
            countTime();
            myTask = new MyTask<>(1);
            myTask.execute();
        }
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

    private void setEnable(boolean enable) {
        phoneView.setEnabled(enable);
    }

    class MyTask<S, R, R1> extends AsyncTask<String, ResponseStatus, ResponseStatus> {

        private int flag;

        public MyTask(int flag) {
            this.flag = flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String s = "正在注册";
            if (flag == 1) {
                s = "正在获取";
            }
            ((LoginActivity) getActivity()).showProgressDialog(s);
        }

        @Override
        protected ResponseStatus doInBackground(String... s) {
            Map<String, Object> args = new HashMap<>(4);
            if (flag == 1) { //code
                args.put("phone", phone);
                return new HttpClient().get(URL.PHONE_CODE, args, false);
            } else {  //register
                args.put("phone", phone);
                password = StringUtil.sha1String(password);
                args.put("password", password);
                args.put("code", code);
                return new HttpClient().post(URL.USER, args, false);
            }
        }

        @Override
        protected void onPostExecute(ResponseStatus resp) {
            LoginActivity activity = (LoginActivity) getActivity();
            activity.dismissProgressDialog();
            setEnable(true);
            if (resp == null) {
                return;
            }
            if (resp.getStatus().equals("fail")) {
                Snackbar.with(getContext()).text(resp.getMsg()).show(activity);
            } else {
                if (flag == 1) { //code
                    ;
                } else if (flag == 2) {  //register
                    Snackbar.with(getContext()).text("注册成功").show(activity);
                    activity.getToken(phone, StringUtil.sha1String(password));
                }
            }
        }
    }


}
