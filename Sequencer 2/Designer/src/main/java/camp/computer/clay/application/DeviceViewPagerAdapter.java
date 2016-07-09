package camp.computer.clay.application;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.Device;

public class DeviceViewPagerAdapter extends FragmentStatePagerAdapter {

    private Clay clay;

    public DeviceViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    /**
     * Get fragment corresponding to a specific position. This will be used to populate the
     * contents of the {@link ViewPager}.
     *
     * @param position Position to fetch fragment for.
     * @return Fragment for specified position.
     */
    @Override
    public Fragment getItem(int position) {

        // Get the device in the specified position
        Device device = null;
        if (position < getClay().getDevices().size()) {
            device = getClay().getDevices().get(position);
        }

        // getItem is called to instantiate the fragment for the given page.
        // Return a DeviceViewFragment (defined as a static inner class
        // below) with the page number as its lone argument.
        DeviceViewFragment fragment = new DeviceViewFragment();
        // TODO: ^ store reference to these fragments in the ApplicationView, so they can be directly called to update the Views after data model changes.
        Bundle args = new Bundle();
        args.putInt(DeviceViewFragment.ARG_SECTION_NUMBER, position + 0); // was "position + 1"
        fragment.setArguments(args);
        fragment.setDevice(device);
        Log.v("Device_Timeline", "Adding timeline for device " + device.getUuid().toString() + ": " + fragment.getTimelineView());
        return (Fragment) fragment;
    }

    /**
     * Get number of pages the {@link ViewPager} should render.
     *
     * @return Number of fragments to be rendered as pages.
     */
    @Override
    public int getCount() {
        if (getClay() != null) {
            return getClay().getDevices().size();
        } else {
            return 0;
        }
    }

    /**
     * Get tag for each of the pages. This will be displayed on each of the tabs.
     *
     * @param position Page to fetch tag for.
     * @return Title for specified page.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return "UNIT " + (position + 1);
    }

    /**
     * Sets a reference to the Clay object to use when creating fragments corresponding to each
     * unit's timeline.
     * @param clay The Clay object to reference.
     */
    public void setClay (Clay clay) {
        this.clay = clay;
    }

    /**
     * Returns the Clay object being used by the PagerAdapter to create fragments.
     * @return The Clay object.
     */
    public Clay getClay () {
        return this.clay;
    }
}
