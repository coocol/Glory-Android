package cc.coocol.jinxiujob.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cc.coocol.jinxiujob.fragments.enterpages.AllEnterprisesFragment;
import cc.coocol.jinxiujob.fragments.enterpages.HotEnterprisesFragment;
import cc.coocol.jinxiujob.fragments.enterpages.NearbyEnterprisesFragment;
import cc.coocol.jinxiujob.fragments.jobpages.AllJobsFragment;
import cc.coocol.jinxiujob.fragments.jobpages.HotJobsFragment;
import cc.coocol.jinxiujob.fragments.jobpages.NearbyJobsFragment;

/**
 * Created by raymond on 16-2-28.
 */
public class EnterpriseFragmentAdapter extends FragmentPagerAdapter {

    private AllEnterprisesFragment allEnterprisesFragment;
    private HotEnterprisesFragment hotEnterprisesFragment;
    private NearbyEnterprisesFragment nearbyEnterprisesFragment;

    public EnterpriseFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (allEnterprisesFragment == null) {
                allEnterprisesFragment = new AllEnterprisesFragment();
            }
            return allEnterprisesFragment;
        } else if (position == 2) {
            if (nearbyEnterprisesFragment == null) {
                nearbyEnterprisesFragment = new NearbyEnterprisesFragment();
            }
            return nearbyEnterprisesFragment;
        } else if (position == 1) {
            if (hotEnterprisesFragment == null ){
                hotEnterprisesFragment = new HotEnterprisesFragment();
            }
            return hotEnterprisesFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
