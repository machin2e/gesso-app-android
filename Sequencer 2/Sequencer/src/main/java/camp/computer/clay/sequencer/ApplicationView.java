package camp.computer.clay.sequencer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.mobeta.android.sequencer.R;

import camp.computer.clay.resource.NetworkResource;
import camp.computer.clay.system.Clay;
import camp.computer.clay.system.DatagramManager;
import camp.computer.clay.system.SQLiteContentManager;
import camp.computer.clay.system.Unit;
import camp.computer.clay.system.ViewManagerInterface;

public class ApplicationView extends FragmentActivity implements ActionBar.TabListener, ViewManagerInterface {

    private static final long MESSAGE_SEND_FREQUENCY = 10;

    private static Context context;

    private static ApplicationView applicationView;

    private Clay clay;

    private DatagramManager datagramServer;

    private NetworkResource networkResource;

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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationView.context = getApplicationContext();

        ApplicationView.applicationView = this;

        clay = new Clay();

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

        // Add the view provided by the host device.
        clay.addView(this);

        // TODO: Set up a server to listen for other views.

        if (datagramServer == null) {
            datagramServer = new DatagramManager ("udp");
            clay.addManager (this.datagramServer);
            datagramServer.startServer ();
        }

        if (networkResource == null) {
            networkResource = new NetworkResource();
            clay.addResource(this.networkResource);
        }

        // <HACK>
        SQLiteContentManager sqliteContentManager = new SQLiteContentManager(getClay(), "sqlite");
        getClay().addContentManager(sqliteContentManager);
        // </HACK>

//        getClay().getStore().resetDatabase();
//        getClay().generateStore();
        getClay().populateCache();
//        getClay().simulateSession(true, 10, false);

        // Start worker process
        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public static Context getContext() {
        return ApplicationView.context;
    }

    @Override
    public void setClay(Clay clay) {
        this.clay = clay;
    }

    @Override
    public Clay getClay() {
        return this.clay;
    }

    @Override
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

    @Override
    public void refreshListViewFromData(Unit unit) {
        // TODO: Update the view to reflect the latest state of the object model
    }

    public static ApplicationView getApplicationView () { return ApplicationView.applicationView; }
}
