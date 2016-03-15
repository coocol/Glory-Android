package cc.coocol.jinxiujob.fragments.logins;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.roundview.RoundFrameLayout;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.LoginActivity;
import cc.coocol.jinxiujob.fragments.BaseFragment;


public class EntryFragment extends BaseFragment implements View.OnClickListener {

    private RoundFrameLayout registerLayout;
    private RoundFrameLayout loginLayout;

    public EntryFragment() {
    }


    public static EntryFragment newInstance() {
        return new EntryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        registerLayout = (RoundFrameLayout) view.findViewById(R.id.register);
        loginLayout = (RoundFrameLayout) view.findViewById(R.id.login);
        registerLayout.setOnClickListener(this);
        loginLayout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        LoginActivity activity = (LoginActivity)getActivity();
        if (v.getId() == R.id.register) {
            activity.changeToRegister();
        } else if (v.getId() == R.id.login) {
            activity.changeToLogin(null);
        }
    }

}
