package cc.coocol.jinxiujob.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cc.coocol.jinxiujob.fragments.enterinfos.EnterHomeFragment;
import cc.coocol.jinxiujob.fragments.enterinfos.EnterJobsFragment;

/**
 * Created by raymond on 16-2-28.
 */
public class EnterInfoFragmentAdapter extends FragmentPagerAdapter {

    private EnterJobsFragment allJobsFragment;
    private EnterHomeFragment enterHomeFragment;

    public EnterInfoFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            if (allJobsFragment == null) {
                allJobsFragment = new EnterJobsFragment();
            }
            return allJobsFragment;
        } else if (position == 0) {
            if (enterHomeFragment == null ){
                enterHomeFragment = new EnterHomeFragment();
            }
            return enterHomeFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
