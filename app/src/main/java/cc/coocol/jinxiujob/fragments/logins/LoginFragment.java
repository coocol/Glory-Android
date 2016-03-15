package cc.coocol.jinxiujob.fragments.logins;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.roundview.RoundFrameLayout;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.LoginActivity;
import cc.coocol.jinxiujob.activities.PasswordActivity;
import cc.coocol.jinxiujob.fragments.BaseFragment;

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private EditText phoneView;
    private EditText passwordView;
    private RoundFrameLayout goButton;
    private TextView registerView;
    private TextView lostPasswordView;

    private String phone;
    private String password;

    @Override
    public String getTile() {
        return null;
    }

    public LoginFragment() {
    }

    public static LoginFragment newInstance(String prePhone) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString("prePhone", prePhone);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        phoneView = (EditText) view.findViewById(R.id.phone);
        passwordView = (EditText) view.findViewById(R.id.password);
        goButton = (RoundFrameLayout) view.findViewById(R.id.pass);
        registerView = (TextView) view.findViewById(R.id.to_register);
        lostPasswordView = (TextView) view.findViewById(R.id.forget);
        lostPasswordView.setOnClickListener(this);
        goButton.setOnClickListener(this);
        registerView.setOnClickListener(this);
        String p = getArguments().getString("prePhone");
        if (p != null) {
            phoneView.setText(p);
            passwordView.requestFocus();
        }
        return view;
    }

    private boolean checkFormat() {
        phone = phoneView.getText().toString();
        if (phone == null || phone.length() < 11) {
            phoneView.setError("请填写电话号码");
            return false;
        }
        password = passwordView.getText().toString();
        if (password == null || password.length() < 6) {
            passwordView.setError("密码不能少于6个字符");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        LoginActivity loginActivity = (LoginActivity) getActivity();
        if (id == R.id.pass) {
            if (!checkFormat()) {
                return;
            }
            loginActivity.getToken(phone, password);
        } else if (id == R.id.to_register) {
            loginActivity.changeToRegister();
        }
        if (id == R.id.forget) {
            loginActivity.gotoActivity(PasswordActivity.class);
        }
    }


}
