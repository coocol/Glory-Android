package cc.coocol.jinxiujob.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.fragments.passwords.NewPasswordFragment;
import cc.coocol.jinxiujob.fragments.passwords.SmsCodeFragment;

public class PasswordActivity extends BaseActivity {

    private SmsCodeFragment smsCodeFragment;
    private NewPasswordFragment passwordFragment;


    private String code;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_clear_mtrl_alpha);
        getSupportActionBar().setTitle("找回密码");

        changeToSmsCode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void changeToSmsCode() {
        if (smsCodeFragment == null) {
            smsCodeFragment = SmsCodeFragment.newInstance("", "");
        }
        showFragment(smsCodeFragment);
    }

    public void changeToPassword() {
        if (passwordFragment == null) {
            passwordFragment = NewPasswordFragment.newInstance("", "");
        }
        showFragment(passwordFragment);
    }
}
