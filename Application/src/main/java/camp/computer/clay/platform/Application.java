package camp.computer.clay.platform;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.text.DecimalFormat;

import camp.computer.clay.Clay;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.util.NewProjectLayoutStrategy;
import camp.computer.clay.engine.manager.Event;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.platform.communication.Internet;
import camp.computer.clay.platform.communication.UDPHost;
import camp.computer.clay.platform.graphics.PlatformRenderSurface;
import camp.computer.clay.platform.graphics.controls.NativeUi;
import camp.computer.clay.platform.sound.SpeechOutput;
import camp.computer.clay.platform.sound.ToneOutput;
import camp.computer.clay.platform.spatial.OrientationInput;
import camp.computer.clay.platform.util.ViewGroupHelper;

public class Application extends FragmentActivity implements PlatformInterface {

    // <SETTINGS>
    private static final boolean ENABLE_TONE_OUTPUT = false;
    private static final boolean ENABLE_SPEECH_OUTPUT = false;
    private static final boolean ENABLE_MOTION_INPUT = true;

    private static final long MESSAGE_SEND_FREQUENCY = 5000; // 500;

    /**
     * Hides the operating system's status and navigation bars. Setting this to false is helpful
     * during debugging.
     */
    private static final boolean ENABLE_FULLSCREEN = true;
    // </SETTINGS>

    public PlatformRenderSurface platformRenderSurface;

    private SpeechOutput speechOutput;

    private ToneOutput toneOutput;

    private OrientationInput orientationInput;

    private static Context context;

    private static Application application;

    private Clay clay;

    private UDPHost UDPHost;

    private Internet networkResource;

    NativeUi platformUi;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SpeechOutput.CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                speechOutput = new SpeechOutput(this);
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    /**
     * Called when the activity is getFirstEvent created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // "Return the context of the single, global Application object of the current process.
        // This generally should only be used if you need a Context whose lifecycle is separate
        // from the current context, that is tied to the lifetime of the process rather than the
        // current component." (Android Documentation)
        Application.context = getApplicationContext();

        // Set up Platform Helpers
        ViewGroupHelper.setContext(getApplicationContext());

        // Lock screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Sensor Interface
        if (ENABLE_MOTION_INPUT) {
            orientationInput = new OrientationInput(getApplicationContext());
        }

        if (ENABLE_FULLSCREEN) {
            startFullscreenService();
        }

        // Display Interface
        Application.application = this;

        platformUi = new NativeUi(getApplicationContext());

//        for (int i = 0; i < 100; i++) {
//            String outgoingMessage = "announce device " + UUID.randomUUID();
//            CRC16 CRC16 = new CRC16();
//            int seed = 0;
//            byte[] outgoingMessageBytes = outgoingMessage.getBytes();
//            int check = CRC16.calculate(outgoingMessageBytes, seed);
//            String outmsg =
//                    "\f" +
//                            String.valueOf(outgoingMessage.length()) + "\t" +
//                            String.valueOf(check) + "\t" +
//                            "text" + "\t" +
//                            outgoingMessage;
//            Log.v("CRC_Demo", "" + outmsg);
//        }

        setContentView(R.layout.activity_main);

        // Create Platform Rendering Surface and add it to the application view.
        platformRenderSurface = new PlatformRenderSurface(getContext());
        FrameLayout frameLayout = (FrameLayout) Application.getApplication_().findViewById(R.id.application_view);
        frameLayout.addView(platformRenderSurface, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        platformRenderSurface.onResume();

        // based on... try it! better performance? https://www.javacodegeeks.com/2011/07/android-game-development-basic-game_05.html
        //setContentView(visualizationSurface);

        /*
        // PathEntity Editor
        final RelativeLayout pathEditor = (RelativeLayout) findViewById(R.id.action_editor_view);
        pathEditor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pathEditor.setVisibility(View.GONE);
                return true;
            }
        });

        final Button pathEditorAddActionButton = (Button) findViewById(R.id.path_editor_add_action);
        pathEditorAddActionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                int pointerId = motionEvent.getPointerId(pointerIndex);
                //int touchAction = (motionEvent.getEvent () & MotionEvent.ACTION_MASK);
                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
                int pointCount = motionEvent.getPointerCount();

                // Update the state of the touched object based on the current pointerCoordinates interaction state.
                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    addPathExtensionAction();

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });
        */

        // Clay
        clay = new Clay();

        clay.addPlatform(this); // Add the view provided by the host device.

        // UDP Datagram Server
        if (UDPHost == null) {
            UDPHost = new UDPHost("udp");
            clay.addHost(this.UDPHost);
            UDPHost.startServer();
        }

        // Internet Network Interface
        if (networkResource == null) {
            networkResource = new Internet();
            clay.addResource(this.networkResource);
        }

        // Prevent on-screen keyboard from pushing up content
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // Start the initial worker thread (runnable task) by posting through the messagingThreadHandler
        messagingThreadHandler.post(messagingThread);

        // Check availability of speech synthesis engine on Android host device.
        if (ENABLE_SPEECH_OUTPUT) {
            SpeechOutput.checkAvailability(this);
        }

        if (ENABLE_TONE_OUTPUT) {
            toneOutput = new ToneOutput();
        }

        // <REDIS>
//        new JedisConnectToDatabaseTask().execute("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com:14268");

//        while (this.jedis == null) {
//            // Waiting for connection...
//        }

//        new Thread(
//                new RedisSubThread(this.jedis)
//        ).start();
        // </REDIS>


        // <REDIS>
//        RedisDBThread redisDB = new RedisDBThread();
//        redisDB.start();
        // </REDIS>

//        openFile("Host.json");

        // <JAVASCRIPT_ENGINE>
        // Reference: https://github.com/ericwlange/AndroidJSCore
        JSContext context = new JSContext();

        // Test 1
        context.property("a", 5);
        JSValue aValue = context.property("a");
        double a = aValue.toNumber();
        DecimalFormat df = new DecimalFormat(".#");
        Log.v("AndroidJSCore", (df.format(a))); // 5.0

        // Test 2
        context.evaluateScript("a = 10");
        JSValue newAValue = context.property("a");
        Log.v("AndroidJSCore", df.format(newAValue.toNumber())); // 10.0
        String script =
                "function factorial(x) { var f = 1; for(; x > 1; x--) f *= x; return f; }\n" +
                        "var fact_a = factorial(a);\n";
        context.evaluateScript(script);
        JSValue fact_a = context.property("fact_a");
        Log.v("AndroidJSCore", df.format(fact_a.toNumber())); // 3628800.0
        // </JAVASCRIPT_ENGINE>


        // <CHECK_HARDWARE_ACCELERATION>
        View view = findViewById(R.id.application_view);
        boolean isHardwareAccelerated = view.isHardwareAccelerated();
        Log.v("HardwareAcceleration", "isHardwareAccelerated: " + isHardwareAccelerated);
        // </CHECK_HARDWARE_ACCELERATION>

        // <SHOW_MAIN_MENU>
        platformUi.openMainMenu();
        // </SHOW_MAIN_MENU>
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // <VISUALIZATION>
        platformRenderSurface.onPause();
        // </VISUALIZATION>
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UDPHost == null) {
            UDPHost = new UDPHost("udp");
        }
        if (!UDPHost.isActive()) {
            UDPHost.startServer();
        }

        // <VISUALIZATION>
        platformRenderSurface.onResume();
        // </VISUALIZATION>
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop speech generator
        if (speechOutput != null) {
            speechOutput.destroy();
        }
    }

    /**
     * Configure platform keyboard input handler.
     *
     * @param keyCode
     * @param event
     * @return
     */
    // TODO: Queue key events in inputSystem
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_S: {
                platformUi.openSettings();
                //your Action code
                return true;
            }

            case KeyEvent.KEYCODE_R: {
                World.getWorld().getSystem(PortableLayoutSystem.class).adjustLayout(new NewProjectLayoutStrategy());
                return true;
            }

            case KeyEvent.KEYCODE_M: {
                platformUi.openMainMenu();
                return true;
            }

            case KeyEvent.KEYCODE_L: {
                // TODO: log
                return true;
            }

            case KeyEvent.KEYCODE_O: {
                // Monitor
                if (World.ENABLE_DRAW_OVERLAY) {
                    World.ENABLE_DRAW_OVERLAY = false;
                } else {
                    World.ENABLE_DRAW_OVERLAY = true;
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
//        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//            Log.v("Application", "ENTER");
//            // TODO: Open "hidden" settings options!
//            return true;
//        }
        return super.dispatchKeyEvent(keyEvent);
    }

    //----------------------------------------------------------------------------------------------

    public static Context getContext() {
        return Application.context;
    }

    // TODO: Rename to something else and make a getPlatform() function specific to the
    // TODO: (cont'd) display interface.
    public static Application getApplication_() {
        return Application.application;
    }

    public NativeUi getPlatformUi() {
        return this.platformUi;
    }

    //----------------------------------------------------------------------------------------------


    // <FULLSCREEN_SERVICE>
    public static final int FULLSCREEN_SERVICE_PERIOD = 2000;

    private boolean enableFullscreenService = false;

    private void startFullscreenService() {
        enableFullscreenService = true;
        final Handler fullscreenServiceHandler = new Handler();
        fullscreenServiceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do what you need to do.
                // e.g., foobar();
                hidePlatformUi();

                // Uncomment this for periodic callback
                if (enableFullscreenService) {
                    fullscreenServiceHandler.postDelayed(this, FULLSCREEN_SERVICE_PERIOD);
                }
            }
        }, Event.MINIMUM_HOLD_DURATION);
    }

    public void stopFullscreenService() {
        enableFullscreenService = false;
    }

    /**
     * References:
     * - http://stackoverflow.com/questions/9926767/is-there-a-way-to-hide-the-system-navigation-bar-in-android-ics
     */
    private void hidePlatformUi() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    // </FULLSCREEN_SERVICE>


    // <MESSAGING_THREAD>
    // Create the Handler object. This will be run on the main thread by default.
    private Handler messagingThreadHandler = new Handler();

    // Define the code block to be executed
    private Runnable messagingThread = new Runnable() {
        @Override
        public void run() {
            // Action the outgoing messages
            clay.update();

            // Repeat this the same runnable code block again another 2 seconds
            messagingThreadHandler.postDelayed(messagingThread, MESSAGE_SEND_FREQUENCY);
        }
    };
    // </MESSAGING_THREAD>

    @Override
    public void setClay(Clay clay) {
        this.clay = clay;
    }

    @Override
    public Clay getClay() {
        return this.clay;
    }

    public PlatformRenderSurface getPlatformRenderSurface() {
        return this.platformRenderSurface;
    }

    // <DELETE>
    /*
    public double getFramesPerSecond() {
        return getPlatformRenderSurface().getPlatformRenderer().getFramesPerSecond();
    }

    public SpeechOutput getSpeechOutput() {
        return this.speechOutput;
    }

    public ToneOutput getToneOutput() {
        return this.toneOutput;
    }

    public OrientationInput getOrientationInput() {
        return this.orientationInput;
    }
    */
    // </DELETE>
}
