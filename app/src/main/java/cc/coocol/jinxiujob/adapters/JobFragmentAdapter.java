package cc.coocol.jinxiujob.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cc.coocol.jinxiujob.fragments.jobpages.AllJobsFragment;
import cc.coocol.jinxiujob.fragments.jobpages.HotJobsFragment;
import cc.coocol.jinxiujob.fragments.jobpages.NearbyJobsFragment;

/**
 * Created by raymond on 16-2-28.
 */
public class JobFragmentAdapter extends FragmentPagerAdapter {

    private AllJobsFragment allJobsFragment;
    private HotJobsFragment hotJobsFragment;
    private NearbyJobsFragment nearbyJobsFragment;

    public JobFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (allJobsFragment == null) {
                allJobsFragment = new AllJobsFragment();
            }
            return allJobsFragment;
        } else if (position == 2) {
            if (nearbyJobsFragment == null) {
                nearbyJobsFragment = new NearbyJobsFragment();
            }
            return nearbyJobsFragment;
        } else if (position == 1) {
            if (hotJobsFragment == null ){
                hotJobsFragment = new HotJobsFragment();
            }
            return hotJobsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
