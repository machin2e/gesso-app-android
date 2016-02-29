package camp.computer.clay.sequencer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import camp.computer.clay.resource.NetworkResource;
import camp.computer.clay.system.SQLiteContentManager;
import camp.computer.clay.system.ViewManagerInterface;
import camp.computer.clay.system.Clay;
import camp.computer.clay.system.DatagramManager;
import camp.computer.clay.system.Unit;

public class ApplicationView extends FragmentActivity implements ActionBar.TabListener, ViewManagerInterface {

    private static final long MESSAGE_SEND_FREQUENCY = 250;

    private static Context context;

    private static ApplicationView applicationView;

    // <CLAY>

    private Clay clay;

    private DatagramManager datagramServer;

    private NetworkResource networkResource;

    // </CLAY>

    // <VIEW>

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private DeviceViewPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    DeviceViewPager mViewPager;

    private ActionBar actionBar;

    // Configure the interface settings
    private static final boolean HIDE_TITLE = true;
    private static final boolean HIDE_ACTION_BAR = false;
    private static final boolean HIDE_ACTION_BAR_ON_SCROLL = false;
    private static final boolean FULLSCREEN = true;

    // <VIEW>

    @Override
    public void setClay(Clay clay) {
        this.clay = clay;
    }

    @Override
    public Clay getClay() {
        return this.clay;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (datagramServer == null) {
            datagramServer = new DatagramManager("udp");
        }
        if (!datagramServer.isActive()) {
            datagramServer.startServer();
        }
    }

    public void refreshListViewFromData(Unit unit) {
//        Log.v("CM_Log", "refreshListViewFromData");
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            DeviceViewFragment unitViewFragment = (DeviceViewFragment) mSectionsPagerAdapter.getItem(i);
//            Log.v("CM_Log", "\tunitViewFragment = " + unitViewFragment);
//            Log.v("CM_Log", "\tunitViewFragment.unit = " + unitViewFragment.getUnit());
//            if (unitViewFragment.getUnit() == unit) {
//                unitViewFragment.refreshView();
//            }
//        }
    }

    /**
     * Create the activity. Sets up an {@link android.app.ActionBar} with tabs, and then configures the
     * {@link ViewPager} contained inside R.layout.activity_main.
     *
     * <p>A {@link DeviceViewPagerAdapter} will be instantiated to hold the different pages of
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

        ApplicationView.applicationView = this;

        Clay.setContext(getApplicationContext());

        clay = new Clay();

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
        mSectionsPagerAdapter = new DeviceViewPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.setClay(getClay());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (DeviceViewPager) findViewById(R.id.pager);
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
        // For each of the sections in the app, addUnit a tab to the action bar.
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            addUnitView(null);
//        }
        // END_INCLUDE (add_tabs)

        // Add the view provided by the host device.
        clay.addView(this);

        // TODO: Set up a server to listen for other views.

        if (datagramServer == null) {
            datagramServer = new DatagramManager("udp");
            clay.addManager(this.datagramServer);
            datagramServer.startServer();
        }

        if (networkResource == null) {
            networkResource = new NetworkResource();
            clay.addResource(this.networkResource);
        }

        // <HACK>
        SQLiteContentManager sqliteContentManager = new SQLiteContentManager(getClay(), "sqlite");
        getClay().addContentManager(sqliteContentManager);
        getClay().populateCache();
        // </HACK>

        // Start worker process
        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
    }

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
//            Log.d("Handlers", "Called on main thread");
            // <HACK>
            // Process the outgoing messages
            clay.cycle();
            // </HACK>
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, MESSAGE_SEND_FREQUENCY);
        }
    };

    public static Context getContext() {
        return ApplicationView.context;
    }

    public static ApplicationView getApplicationView () { return ApplicationView.applicationView; }

    public void addUnitView(Unit unit) {

        // TODO: (?) Add DeviceViewFragment to list here?

        // Increment the number of pages to be the same as the number of discovered units.
        mSectionsPagerAdapter.count++;
        mSectionsPagerAdapter.notifyDataSetChanged();

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
}
