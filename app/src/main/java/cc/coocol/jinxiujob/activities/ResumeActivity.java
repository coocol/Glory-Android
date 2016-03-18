package cc.coocol.jinxiujob.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.fragments.ResumeFragment;

public class ResumeActivity extends BaseActivity {

    private ResumeFragment resumeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的简历");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);


        resumeFragment = new ResumeFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, resumeFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
