package cc.coocol.jinxiujob.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cc.coocol.jinxiujob.fragments.enterpages.AllEnterprisesFragment;
import cc.coocol.jinxiujob.fragments.jobpages.AllJobsFragment;
import cc.coocol.jinxiujob.fragments.jobpages.HotJobsFragment;
import cc.coocol.jinxiujob.fragments.jobpages.NearbyJobsFragment;
import cc.coocol.jinxiujob.fragments.searchpages.SearchEntersFragment;
import cc.coocol.jinxiujob.fragments.searchpages.SearchJobsFragment;

/**
 * Created by raymond on 16-2-28.
 */
public class SearchFragmentAdapter extends FragmentPagerAdapter {

    private SearchJobsFragment jobsFragment;
    private SearchEntersFragment enterprisesFragment;

    public SearchFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (jobsFragment == null) {
                jobsFragment = new SearchJobsFragment();
            }
            return jobsFragment;
        } else if (position == 1) {
            if (enterprisesFragment == null) {
                enterprisesFragment = new SearchEntersFragment();
            }
            return enterprisesFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
