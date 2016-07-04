package camp.computer.clay.designer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobeta.android.sequencer.R;

import java.util.ArrayList;

import camp.computer.clay.resource.NetworkResource;
import camp.computer.clay.visualization.util.Animation;
import camp.computer.clay.system.Clay;
import camp.computer.clay.system.DatagramManager;
import camp.computer.clay.system.Device;
import camp.computer.clay.system.SQLiteContentManager;
import camp.computer.clay.system.ViewManagerInterface;

public class ApplicationView extends FragmentActivity implements ActionBar.TabListener, ViewManagerInterface {

    private MapView mapView;

    private SpeechGenerator speechGenerator;

    private SensorAdapter sensorAdapter;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speechGenerator = new SpeechGenerator(this);
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    // <Settings>
    private static final boolean ENABLE_TONE_GENERATOR = false;
    private static final boolean ENABLE_SPEECH_GENERATOR = false;
    private static final long MESSAGE_SEND_FREQUENCY = 10;
    // </Settings>

    // <Settings/Speech>
    private final int CHECK_CODE = 0x1;
    private final int LONG_DURATION = 5000;
    private final int SHORT_DURATION = 1200;
    // <Settings/Speech>

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

    public TimelineView getTimelineView () {
        return mViewPager.getTimelineView();
    }

    // <HACK>
    ArrayList<TimelineView> timelineViews = new ArrayList<TimelineView>();
    public TimelineView getTimelineView (Device device) {
        Log.v("Device_Timeline", "ApplicationView.getTimelineView");
        //TODO: return mViewPager.getTimelineView(device);

        for (TimelineView timelineView : timelineViews) {
            if (timelineView.getDevice().getUuid().toString().equals(device.getUuid().toString())) {
                return timelineView;
            }
        }

        return null;
    }
    // </HACK>

    public void setTimelineView (Device device) {
        mViewPager.setTimelineView(device);
    }

    private CursorView cursorView;

    public CursorView getCursorView() {
        return cursorView;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationView.context = getApplicationContext();

        // <SENSORS>
        sensorAdapter = new SensorAdapter(getApplicationContext());
        // </SENSORS>

        // <DISPLAY>
        ApplicationView.applicationView = this;
        // </DISPLAY>

        clay = new Clay();

        setContentView(R.layout.activity_main);

        // Hide the action buttons
        cursorView = new CursorView();
        cursorView.hide(false);

        // <MAP>
        mapView = (MapView) findViewById (R.id.app_surface_view);

        mapView.MapView_OnResume ();
        // </MAP>

        // Set up the action bar. The navigation mode is set to NAVIGATION_MODE_TABS, which will
        // cause the ActionBar to render a set of tabs. Note that these tabs are *not* rendered
        // by the ViewPager; additional logic is lower in this file to synchronize the ViewPager
        // state with the tab state. (See mViewPager.setOnPageChangeListener() and onTabSelected().)
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

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new DeviceViewPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.setClay(getClay());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (DeviceViewPager) findViewById(R.id.pager);
        mViewPager.setPagingEnabled(true); // Disable horizontal paging by swiping left and right
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab. We can also use
        // ActionBar.Tab#select() to do this if we have a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (!HIDE_ACTION_BAR) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        });

        // Add the view provided by the host device.
        clay.addView(this);

        // Start UDP server
        if (datagramServer == null) {
            datagramServer = new DatagramManager ("udp");
            clay.addManager (this.datagramServer);
            datagramServer.startServer ();
        }

        // Create network profile
        if (networkResource == null) {
            networkResource = new NetworkResource();
            clay.addResource(this.networkResource);
        }

        // Create content store
        SQLiteContentManager sqliteContentManager = new SQLiteContentManager(getClay(), "sqlite");
        getClay().setStore(sqliteContentManager);

        // Initialize content store
        getClay().getStore().erase();
        getClay().getCache().populate(); // alt. syntax: useClay().useCache().toPopulate();
        getClay().getStore().generate();
        getClay().getCache().populate();
        // getClay().simulateSession(true, 10, false);

        // Prevent on-screen keyboard from pushing up content
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // <CHAT_AND_CONTEXT_SCOPE>
        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final HorizontalScrollView messageContentLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_content_layout_perspective);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final TextView messageContentHint = (TextView) findViewById(R.id.message_content_hint);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById (R.id.context_button);
        // </CHAT_AND_CONTEXT_SCOPE>

        // <CHAT>

        messageContentHint.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                messageContentHint.setVisibility(View.GONE);
                showMessageKeyboard();
                return false;
            }
        });

        // Hide scrollbars in keyboard
        messageKeyboardLayoutPerspective.setVerticalScrollBarEnabled(false);
        messageKeyboardLayoutPerspective.setHorizontalScrollBarEnabled(false);

        // Hide scrollbars in message content
        messageContentLayoutPerspective.setVerticalScrollBarEnabled(false);
        messageContentLayoutPerspective.setHorizontalScrollBarEnabled(false);

        generateKeyboard();

        // Set up interactivity
        messageContentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int pointerIndex = ((motionEvent.getAction () & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                int pointerId = motionEvent.getPointerId (pointerIndex);
                //int touchAction = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
                int touchActionType = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
                int pointCount = motionEvent.getPointerCount ();

                // Update the state of the touched object based on the current touch interaction state.
                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                    showMessageKeyboard();
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }

//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int inType = timelineButton.getInputType(); // backup the input type
//                timelineButton.setInputType(InputType.TYPE_NULL); // disable soft input
//                timelineButton.onTouchEvent(event); // call native handler
//                timelineButton.setInputType(inType); // restore input type
//                return true; // consume touch even
//            }
        });

        messageContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        // </CHAT>

        // </CONTEXT_SCOPE>
        contextScope.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    // Get button holder
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.context_button_holder);

                    // Get screen width and height of the device
                    DisplayMetrics metrics = new DisplayMetrics();
                    ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int screenWidth = metrics.widthPixels;
                    int screenHeight = metrics.heightPixels;

                    // Get button width and height
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
                    int buttonWidth = relativeLayout.getWidth();
                    int buttonHeight = relativeLayout.getHeight();

                    // Reposition button
                    params.rightMargin = screenWidth - (int) event.getRawX() - (int) (buttonWidth / 2.0f);
                    params.bottomMargin = screenHeight - (int) event.getRawY() - (int) (buttonHeight / 2.0f);

                    relativeLayout.requestLayout();
                    relativeLayout.invalidate();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    // Get button holder
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.context_button_holder);

                    // TODO: Compute relative to dependant sprite position
                    Point originPoint = new Point(959, 1655);

                    Animation animation = new Animation();
                    animation.moveToPoint(relativeLayout, originPoint, 300);

                    // Reset the message envelope
                    messageContent.removeAllViews();
                    messageKeyboardLayout.setVisibility(View.GONE);

                    // Replace the hint
                    messageContent.addView(messageContentHint);
                    messageContentHint.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });
        // </CONTEXT_SCOPE>

        // <TIMELINE>
//        final RelativeLayout timelineView = (RelativeLayout) findViewById(R.id.timeline_view);
//        final RelativeLayout oldTimelineView = (RelativeLayout) findViewById(R.id.old_timeline_view);
//        timelineButton.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (oldTimelineView.isVisible() == View.GONE) {
//                    oldTimelineView.setVisibility(View.VISIBLE);
//                    timelineButton.setText("Map");
//                    cursorView.show(true);
//                } else {
//                    oldTimelineView.setVisibility(View.GONE);
//                    timelineButton.setText("Timeline");
//                    cursorView.hide(true);
//                }
//                return true;
//            }
//        });
        // ^^^ Timeline Button ^^^
        // </TIMELINE>

        // Start the initial worker thread (runnable task) by posting through the handler
        handler.post(runnableCode);

        checkTTS();

        hideChat();
    }

    public void hideChat() {
        // <CHAT_AND_CONTEXT_SCOPE>
        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final HorizontalScrollView messageContentLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_content_layout_perspective);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final TextView messageContentHint = (TextView) findViewById(R.id.message_content_hint);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById (R.id.context_button);
        // </CHAT_AND_CONTEXT_SCOPE>

        messageContentLayout.setVisibility(View.GONE);
    }

    private void showMessageKeyboard() {

        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById (R.id.context_button);

        ViewGroup.MarginLayoutParams chatLayoutParams = (ViewGroup.MarginLayoutParams) messageContentLayout.getLayoutParams();

        ViewGroup.MarginLayoutParams chatKeyboardLayoutParams = (ViewGroup.MarginLayoutParams) messageKeyboardLayout.getLayoutParams();

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics());
        //float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 14, r.getDisplayMetrics());

        // Reposition chat layout
        chatKeyboardLayoutParams.bottomMargin = chatLayoutParams.bottomMargin + messageContentLayout.getLayoutParams().height + 20; //h - (int) event.getRawY() - (int) (buttonHeight / 2.0f);

        if (messageKeyboardLayout.getVisibility() == View.GONE) {
            messageKeyboardLayout.setVisibility(View.VISIBLE);
        } else if (messageKeyboardLayout.getVisibility() == View.VISIBLE) {
            messageKeyboardLayout.setVisibility(View.GONE);
        }

        messageKeyboardLayout.requestLayout();
        messageKeyboardLayout.invalidate();
    }

    private void generateKeyboard() {
        generateKeys();
    }

    private void generateKeys() {

        //final EditText messageContent = (EditText) findViewById(R.id.message_content);

        generateKey("settings");
        generateKey("\uD83D\uDD0D");
//        generateKey("zoom/in");
//        generateKey("zoom/out");
        generateKey("camera");
        generateKey("vibrate");
        generateKey("timeline");
        generateKey("help");
        generateKey("chat");
    }

    private void generateKey(String settings) {

        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById (R.id.context_button);

        // <CHAT>

        // Add keys to keyboard
        final Button messageKey = new Button(getContext());
        messageKey.setText(settings);
        messageKey.setTextSize(12.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 0, 5, 0);
        messageKey.setPadding(0, 0, 0, 0);
        messageKey.setLayoutParams(layoutParams);
        messageKey.getLayoutParams().height = 100;
        messageKey.setBackgroundResource(R.drawable.chat_message_key);

        messageKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // final EditText chatEntry = (EditText) findViewById(R.id.chat_entry);
                appendToMessage(messageKey.getText().toString());

                validateMessage();
            }
        });

        messageKeyboard.addView(messageKey);
    }

    private void validateMessage() {

        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById (R.id.context_button);

        contextScope.setText("✓");
    }

    private void appendToMessage(String text) {
        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final HorizontalScrollView messageContentLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_content_layout_perspective);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById (R.id.context_button);
        // </CHAT_AND_CONTEXT_SCOPE>

        // <CHAT>

        // Add keys to keyboard
        final Button messageWord = new Button(getContext());
        messageWord.setText(text);
        messageWord.setTextSize(12.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 0, 10, 0);
        messageWord.setPadding(0, 0, 0, 0);
        messageWord.setLayoutParams(layoutParams);
        messageWord.getLayoutParams().height = 100;
        messageWord.setBackgroundResource(R.drawable.chat_message_key);

        messageWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageContent.removeView(messageWord);

                // Unicode arrow symbols: https://en.wikipedia.org/wiki/Template:Unicode_chart_Arrows

                // final EditText chatEntry = (EditText) findViewById(R.id.chat_entry);
//                messageContent.addView(messageKey);
//                contextScope.setText("✓");
//                contextScope.setText("☉");
                //contextScope.setText("☌"); // When dragging to connect path
                //contextScope.setText("☍"); // Just after connected path
                // ☉ // Just after tapping a node
                // ☐ // Just after tapping machine
                // ☊
                // ☋
                // ☝
                // ☜
                // ☞
                // ☟
                // ☺ // Smile
                // ☹ // Frown
            }
        });

        messageContent.addView(messageWord);

        messageContentLayoutPerspective.postDelayed(new Runnable() {
            public void run() {
                messageContentLayoutPerspective.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // <MAP>
        mapView.MapView_OnPause();
        // </MAP>
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

        // <MAP>
        mapView.MapView_OnResume ();
        // </MAP>
    }

    // Create the Handler object. This will be run on the main thread by default.
    Handler handler = new Handler();

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Process the outgoing messages
            clay.step();

            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, MESSAGE_SEND_FREQUENCY);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechGenerator.destroy();
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
    public void addDeviceView(Device device) {

        // TODO: (?) Add DeviceViewFragment to list here?

        // Increment the number of pages to be the same as the number of discovered units.
//        mSectionsPagerAdapter.count++;
        mSectionsPagerAdapter.notifyDataSetChanged();

        // Create a tab with text corresponding to the page tag defined by the adapter. Also
        // specify this Activity object, which implements the TabListener interface, as the
        // callback (listener) for when this tab is selected.
        if (actionBar != null) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Device") // .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));

            // Show action bar if it is hidden (when device count is greater than o or 1)
            /*
            if (!actionBar.isShowing()) {
                actionBar.show();
            }
            */
        }

//        // Show the action button
//        ApplicationView.getApplicationView().getCursorView().init();
//        ApplicationView.getApplicationView().getCursorView().updatePosition();
//        ApplicationView.getApplicationView().getCursorView().show(true);

    }

    @Override
    public void refreshListViewFromData(Device device) {
        // TODO: Update the view to reflect the latest state of the object model
    }

    public static ApplicationView getApplicationView () { return ApplicationView.applicationView; }

    // <SPEECH>
    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    public void speakPhrase(String phrase) {
        if (ENABLE_SPEECH_GENERATOR) {
            if (speechGenerator != null) {
                speechGenerator.allow(true);
                speechGenerator.speak(phrase);
                speechGenerator.allow(false);
            }
        }
    }
    // </SPEECH>

    // <SOUND>
    /**
     * Reference: http://stackoverflow.com/questions/2413426/playing-an-arbitrary-tone-with-android
     */
    public void playTone(double freqOfTone, double duration) {
        if (ENABLE_TONE_GENERATOR) {
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
            int i = 0;

            int ramp = numSamples / 20;                                    // Amplitude ramp as a percent of sample count


            for (i = 0; i < ramp; ++i) {                                     // Ramp amplitude up (to avoid clicks)
                double dVal = sample[i];
                // Ramp up to maximum
                final short val = (short) ((dVal * 32767 * i / ramp));
                // in 16 bit wav PCM, first byte is the low order byte
                generatedSnd[idx++] = (byte) (val & 0x00ff);
                generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            }


            for (i = i; i < numSamples - ramp; ++i) {                        // Max amplitude for most of the samples
                double dVal = sample[i];
                // scale to maximum amplitude
                final short val = (short) ((dVal * 32767));
                // in 16 bit wav PCM, first byte is the low order byte
                generatedSnd[idx++] = (byte) (val & 0x00ff);
                generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            }

            for (i = i; i < numSamples; ++i) {                               // Ramp amplitude down
                double dVal = sample[i];
                // Ramp down to zero
                final short val = (short) ((dVal * 32767 * (numSamples - i) / ramp));
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
            } catch (Exception e) {
            }

            int x = 0;
            do {                                                     // Montior playback to find when done
                if (audioTrack != null)
                    x = audioTrack.getPlaybackHeadPosition();
                else
                    x = numSamples;
            } while (x < numSamples);

            if (audioTrack != null)
                audioTrack.release();           // Track play done. Release track.
        }
    }
    // </SOUND>

    // <SENSORS>
    public SensorAdapter getSensorAdapter() {
        return this.sensorAdapter;
    }
    // </SENSORS>
}
