package cc.coocol.jinxiujob.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cc.coocol.jinxiujob.fragments.collectpages.CollectedEnterprisesFragment;
import cc.coocol.jinxiujob.fragments.collectpages.CollectedJobsFragment;

/**
 * Created by raymond on 16-2-28.
 */
public class CollectededFragmentAdapter extends FragmentPagerAdapter {

    private CollectedEnterprisesFragment collectedEnterprisesFragment;
    private CollectedJobsFragment collectedJobsFragment;

    public CollectededFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (collectedJobsFragment == null) {
                collectedJobsFragment = new CollectedJobsFragment();
            }
            return collectedJobsFragment;
        } else if (position == 1) {
            if (collectedEnterprisesFragment == null) {
                collectedEnterprisesFragment = new CollectedEnterprisesFragment();
            }
            return collectedEnterprisesFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
