package camp.computer.clay.sequencer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.github.clans.fab.FloatingActionButton;
import com.mobeta.android.sequencer.R;

import camp.computer.clay.resource.NetworkResource;
import camp.computer.clay.system.Clay;
import camp.computer.clay.system.DatagramManager;
import camp.computer.clay.system.SQLiteContentManager;
import camp.computer.clay.system.Unit;
import camp.computer.clay.system.ViewManagerInterface;

public class ApplicationView extends FragmentActivity implements ActionBar.TabListener, ViewManagerInterface {

    private final int CHECK_CODE = 0x1;
    private final int LONG_DURATION = 5000;
    private final int SHORT_DURATION = 1200;
    private Speaker speaker;

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speaker = new Speaker(this);
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    public void speakPhrase(String phrase) {
        Log.v("Clay_Verbalizer", "speakPhrase: " + phrase);
//        if (speaker.isAllowed ())
        if (speaker != null) {
            speaker.allow (true);
            speaker.speak (phrase);
            speaker.allow (false);
        }
    }

    /**
     * Reference: http://stackoverflow.com/questions/2413426/playing-an-arbitrary-tone-with-android
     */
    public void playTone(double freqOfTone, double duration) {
        //double duration = 1000;                // seconds
        //   double freqOfTone = 1000;           // hz
        int sampleRate = 8000;              // a number

        double dnumSamples = duration * sampleRate;
        dnumSamples = Math.ceil(dnumSamples);
        int numSamples = (int) dnumSamples;
        double sample[] = new double[numSamples];
        byte generatedSnd[] = new byte[2 * numSamples];


        for (int i = 0; i < numSamples; ++i) {      // Fill the sample array
            sample[i] = Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        int i = 0 ;

        int ramp = numSamples / 20 ;                                    // Amplitude ramp as a percent of sample count


        for (i = 0; i< ramp; ++i) {                                     // Ramp amplitude up (to avoid clicks)
            double dVal = sample[i];
            // Ramp up to maximum
            final short val = (short) ((dVal * 32767 * i/ramp));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }


        for (i = i; i< numSamples - ramp; ++i) {                        // Max amplitude for most of the samples
            double dVal = sample[i];
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (i = i; i< numSamples; ++i) {                               // Ramp amplitude down
            double dVal = sample[i];
            // Ramp down to zero
            final short val = (short) ((dVal * 32767 * (numSamples-i)/ramp ));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        AudioTrack audioTrack = null;                                   // Get audio track
        try {
            int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();                                          // Play the track
            audioTrack.write(generatedSnd, 0, generatedSnd.length);     // Load the track
        }
        catch (Exception e){
        }

        int x =0;
        do{                                                     // Montior playback to find when done
            if (audioTrack != null)
                x = audioTrack.getPlaybackHeadPosition();
            else
                x = numSamples;
        }while (x<numSamples);

        if (audioTrack != null) audioTrack.release();           // Track play done. Release track.
    }

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
    private static final boolean HIDE_ACTION_BAR = true;
    private static final boolean HIDE_ACTION_BAR_ON_SCROLL = true;
    private static final boolean FULLSCREEN = true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationView.context = getApplicationContext();

        ApplicationView.applicationView = this;

        clay = new Clay();

        setContentView(R.layout.activity_main);

        // Hide the action buttons
        FloatingActionButton fab = (FloatingActionButton) ApplicationView.getApplicationView().findViewById(R.id.fab_create);
        fab.hide(false);

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



//        if (HIDE_ACTION_BAR_ON_SCROLL) {
////            actionBar.setHideOnContentScrollEnabled(true);
//
//            mViewPager.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    //                    float y = ((ScrollView)findViewById(R.id.parent)).getScrollY();
////                    if (y >= mActionBarHeight && mActionBar.isShowing()) {
////                        mActionBar.hide();
////                    } else if ( y==0 && !mActionBar.isShowing()) {
////                        mActionBar.show();
////                    }
//                    Log.v("Scroller", "hmm");
//
//                    if (actionBar.isShowing()) {
//                        actionBar.hide();
//                    }
//                }
//            });
//        }

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

        checkTTS();
    }

    public void showActionBar () {
        actionBar.show();
    }

    public void hideActionBar () {
        actionBar.hide();
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
    protected void onDestroy() {
        super.onDestroy();
        speaker.destroy();
    }

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

        // Create a tab with text corresponding to the page tag defined by the adapter. Also
        // specify this Activity object, which implements the TabListener interface, as the
        // callback (listener) for when this tab is selected.
        if (actionBar != null) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Unit") // .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));

            // Show action bar if it is hidden (when device count is greater than o or 1)
//            if (!actionBar.isShowing()) {
//                actionBar.show();
//            }
        }

    }

    @Override
    public void refreshListViewFromData(Unit unit) {
        // TODO: Update the view to reflect the latest state of the object model
    }

    public static ApplicationView getApplicationView () { return ApplicationView.applicationView; }
}
