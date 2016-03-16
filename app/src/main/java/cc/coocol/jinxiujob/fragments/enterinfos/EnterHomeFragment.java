package cc.coocol.jinxiujob.fragments.enterinfos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.models.BaseEnterItemModel;
import cc.coocol.jinxiujob.models.DetailEnterModel;

public class EnterHomeFragment extends Fragment{


    @Bind(R.id.job_count)
    TextView JobCountView;

    @Bind(R.id.collect_count) TextView collectView;
    @Bind(R.id.apply_count) TextView applyView;
    @Bind(R.id.address) TextView addressView;
    @Bind(R.id.intro) TextView introView;


    public EnterHomeFragment() {
    }

    public static EnterHomeFragment newInstance(String param1, String param2) {
        EnterHomeFragment fragment = new EnterHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void refreshData(DetailEnterModel itemModel) {
        JobCountView.setText(itemModel.getJobs() + "");
        applyView.setText(itemModel.getApply() + "");
        collectView.setText(itemModel.getCollect() + "");
        addressView.setText(itemModel.getAddress());
        introView.setText(itemModel.getIntroduction());
    }

}
