package cc.coocol.jinxiujob.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cc.coocol.jinxiujob.R;

public class PersonHomeActivity extends AppCompatActivity {

    private FloatingActionButton editButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editButton = (FloatingActionButton) findViewById(R.id.edit);

    }


}
