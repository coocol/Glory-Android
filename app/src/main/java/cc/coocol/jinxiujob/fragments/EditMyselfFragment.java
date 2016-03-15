package cc.coocol.jinxiujob.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.coocol.jinxiujob.R;

public class EditMyselfFragment extends BaseFragment{


    public static EditMyselfFragment newInstance(String param1, String param2) {
        EditMyselfFragment fragment = new EditMyselfFragment();
        return fragment;
    }

    @Override
    public String getTile() {
        return "编辑资料";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_edit_myself, container, false);

        return view;
    }

}
