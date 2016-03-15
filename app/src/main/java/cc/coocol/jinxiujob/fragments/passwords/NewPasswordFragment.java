package cc.coocol.jinxiujob.fragments.passwords;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.flyco.roundview.RoundFrameLayout;
import com.nispok.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.LoginActivity;
import cc.coocol.jinxiujob.activities.PasswordActivity;
import cc.coocol.jinxiujob.fragments.BaseFragment;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;
import cc.coocol.jinxiujob.utils.StringUtil;


public class NewPasswordFragment extends BaseFragment implements View.OnClickListener {

    private EditText passwordView;
    private RoundFrameLayout goButton;

    private String password;

    public NewPasswordFragment() {
    }


    public static NewPasswordFragment newInstance(String param1, String param2) {
        NewPasswordFragment fragment = new NewPasswordFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_password, container, false);
        passwordView = (EditText) view.findViewById(R.id.password);
        goButton = (RoundFrameLayout) view.findViewById(R.id.go_password);
        passwordView.setOnClickListener(this);
        goButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.go_password) {
            password = passwordView.getText().toString();
            if (password == null || password.length() < 6) {
                passwordView.setError("不能少于6个字符");
            } else {
                PasswordActivity activity = (PasswordActivity) getActivity();
                new MyTask<>(activity.getPhone(), activity.getCode()).execute();
            }
        }
    }

    class MyTask<S, R, R1> extends AsyncTask<String, ResponseStatus, ResponseStatus> {

        public MyTask(String phone, String code) {
            this.phone = phone;
            this.code = code;
        }

        private String phone;
        private String code;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((PasswordActivity) getActivity()).showProgressDialog("");
        }

        @Override
        protected ResponseStatus doInBackground(String... s) {
            Map<String, Object> args = new HashMap<>(4);
            args.put("password", StringUtil.sha1String(password));
            args.put("phone", phone);
            args.put("code", code);
            return new HttpClient().post(URL.PASSWORD_NEW, args, false);

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
                Snackbar.with(getContext()).text("修改成功").show(activity);
                activity.gotoActivity(LoginActivity.class, "modify_password", phone);
            }
        }
    }
}
