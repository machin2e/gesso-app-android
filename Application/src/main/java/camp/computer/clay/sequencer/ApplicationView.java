package camp.computer.clay.sequencer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import camp.computer.clay.system.ViewManagerInterface;
import camp.computer.clay.system.Clay;
import camp.computer.clay.system.DatagramManager;
import camp.computer.clay.system.Unit;

public class ApplicationView extends FragmentActivity implements ActionBar.TabListener, ViewManagerInterface {

    private static Context context;

    private DatagramManager datagramServer;

    private Clay clay;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    UnitViewPager mViewPager;

    private ActionBar actionBar;

    // Configure the interface settings
    private static final boolean HIDE_TITLE = true;
    private static final boolean HIDE_ACTION_BAR = false;
    private static final boolean HIDE_ACTION_BAR_ON_SCROLL = false;
    private static final boolean FULLSCREEN = true;

    @Override
    protected void onResume() {
        super.onResume();

        if (datagramServer == null) {
            datagramServer = new DatagramManager("udp");
        }
        datagramServer.startServer();
    }

    /**
     * Create the activity. Sets up an {@link android.app.ActionBar} with tabs, and then configures the
     * {@link ViewPager} contained inside R.layout.activity_main.
     *
     * <p>A {@link SectionsPagerAdapter} will be instantiated to hold the different pages of
     * fragments that are to be displayed. A
     * {@link android.support.v4.view.ViewPager.SimpleOnPageChangeListener} will also be configured
     * to receive callbacks when the user swipes between pages in the ViewPager.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationView.context = getApplicationContext();

        // Load the UI from res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        // Set up the action bar. The navigation mode is set to NAVIGATION_MODE_TABS, which will
        // cause the ActionBar to render a set of tabs. Note that these tabs are *not* rendered
        // by the ViewPager; additional logic is lower in this file to synchronize the ViewPager
        // state with the tab state. (See mViewPager.setOnPageChangeListener() and onTabSelected().)
        // BEGIN_INCLUDE (set_navigation_mode)
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        if (HIDE_ACTION_BAR) {
            actionBar.hide();
        }

        if (HIDE_TITLE) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        if (HIDE_ACTION_BAR_ON_SCROLL) {
            actionBar.setHideOnContentScrollEnabled(true);
        }
        // END_INCLUDE (set_navigation_mode)

        if (FULLSCREEN) {
            // Remove notification bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // BEGIN_INCLUDE (setup_view_pager)
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (UnitViewPager) findViewById(R.id.pager);
        mViewPager.setPagingEnabled(true); // Disable horizontal paging by swiping left and right
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // END_INCLUDE (setup_view_pager)

        // When swiping between different sections, select the corresponding tab. We can also use
        // ActionBar.Tab#select() to do this if we have a reference to the Tab.
        // BEGIN_INCLUDE (page_change_listener)
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (!HIDE_ACTION_BAR) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        });
        // END_INCLUDE (page_change_listener)

        // BEGIN_INCLUDE (add_tabs)
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            addUnitView(null);
        }
        // END_INCLUDE (add_tabs)

        Clay.setContext(getApplicationContext());

        clay = new Clay();
        clay.addView(this);

        if (datagramServer == null) {
            datagramServer = new DatagramManager("udp");
        }
//        datagramServer.startDatagramServer();
        clay.addMessageManager(this.datagramServer);
    }

    public static Context getContext() {
        return ApplicationView.context;
    }

    public void addUnitView(Unit unit) {

        // Create a tab with text corresponding to the page title defined by the adapter. Also
        // specify this Activity object, which implements the TabListener interface, as the
        // callback (listener) for when this tab is selected.
        if (actionBar != null) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Unit") // .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    /**
     * Update {@link ViewPager} after a tab has been selected in the ActionBar.
     *
     * @param tab Tab that was selected.
     * @param fragmentTransaction A {@link android.app.FragmentTransaction} for queuing fragment operations to
     *                            execute once this method returns. This FragmentTransaction does
     *                            not support being added to the back stack.
     */
    // BEGIN_INCLUDE (on_tab_selected)
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, tell the ViewPager to switch to the corresponding page.
        mViewPager.setCurrentItem(tab.getPosition());
    }
    // END_INCLUDE (on_tab_selected)

    /**
     * Unused. Required for {@link android.app.ActionBar.TabListener}.
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * Unused. Required for {@link android.app.ActionBar.TabListener}.
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void setClay(Clay clay) {
        this.clay = clay;
    }

    @Override
    public Clay getClay() {
        return this.clay;
    }

    // BEGIN_INCLUDE (fragment_pager_adapter)
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages. This provides the data for the {@link ViewPager}.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    // END_INCLUDE (fragment_pager_adapter)

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // BEGIN_INCLUDE (fragment_pager_adapter_getitem)
        /**
         * Get fragment corresponding to a specific position. This will be used to populate the
         * contents of the {@link ViewPager}.
         *
         * @param position Position to fetch fragment for.
         * @return Fragment for specified position.
         */
        @Override
        public Fragment getItem(int position) {

            // Get the unit in the specified position
            Unit unit = null;
            if (position < getClay().getUnits().size()) {
                unit = getClay().getUnits().get(position);
            }

            // getItem is called to instantiate the fragment for the given page.
            // Return a UnitViewFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            UnitViewFragment fragment = new UnitViewFragment();
            Bundle args = new Bundle();
            args.putInt(UnitViewFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            fragment.setUnit (unit);
            return (Fragment) fragment;
        }
        // END_INCLUDE (fragment_pager_adapter_getitem)

        // BEGIN_INCLUDE (fragment_pager_adapter_getcount)
        /**
         * Get number of pages the {@link ViewPager} should render.
         *
         * @return Number of fragments to be rendered as pages.
         */
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }
        // END_INCLUDE (fragment_pager_adapter_getcount)

        // BEGIN_INCLUDE (fragment_pager_adapter_getpagetitle)
        /**
         * Get title for each of the pages. This will be displayed on each of the tabs.
         *
         * @param position Page to fetch title for.
         * @return Title for specified page.
         */
        @Override
        public CharSequence getPageTitle(int position) {
//            Locale l = Locale.getDefault();
//            switch (position) {
//                case 0:
//                    return getString(R.string.title_section1).toUpperCase(l);
//                case 1:
//                    return getString(R.string.title_section2).toUpperCase(l);
//                case 2:
//                    return getString(R.string.title_section3).toUpperCase(l);
//            }
//            return null;
            return "UNIT " + (position + 1);
        }
        // END_INCLUDE (fragment_pager_adapter_getpagetitle)
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     * This would be replaced with your application's content.
     */
    public static class UnitViewFragment extends Fragment {

        // The Clay unit associated with this fragment.
        private Unit unit;

        private TimelineListView listView;

        // Configure the interface settings
        boolean disableScrollbarFading = true;
        boolean disableScrollbars = true;
        boolean disableOverscrollEffect = true;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

//        private ArrayList<String> behaviorEvents = new ArrayList<String>();
//        ArrayAdapter<String> listAdapter;

        // TODO: UnitViewFragment(Unit unit)
        public UnitViewFragment() {

//            behaviorEvents.add("hello a");
//            behaviorEvents.add("hello b");
//            behaviorEvents.add("hello c");
//            behaviorEvents.add("hello d");
//            behaviorEvents.add("hello e");
//            behaviorEvents.add("hello f");
//            behaviorEvents.add("hello g");
        }

        public void setUnit (Unit unit) {
            this.unit = unit;
        }

        public Unit getUnit () {
            return this.unit;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
//            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
//            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

            // Define the adapter (adapts the data to the actual rendered view)
//            listAdapter = new ArrayAdapter<String>(
//                    getActivity(), // The current context (this fragment's parent activity).
//                    R.layout.list_item_behavior_event, // ID of list item layout
//                    R.id.list_item_behavior_event_label, // ID of textview to populate (using the specified list item layout)
//                    behaviorEvents // The list containing the behaviors to show on the timeline.
//            );

            // Define the view (get a reference to it and pass it an adapter)
            listView = (TimelineListView) rootView.findViewById(R.id.listview_timeline);
            listView.setTag(getArguments().getInt(ARG_SECTION_NUMBER));
//            listView.setAdapter(listAdapter);

            if (disableScrollbarFading) {
                listView.setScrollbarFadingEnabled(false);
            }

            // Disable the scrollbars.
            if (disableScrollbars) {
                listView.setVerticalScrollBarEnabled(false);
                listView.setHorizontalScrollBarEnabled(false);
            }

            // Disable overscroll effect.
            if (disableOverscrollEffect) {
                listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }

            return rootView;
        }
    }

}
