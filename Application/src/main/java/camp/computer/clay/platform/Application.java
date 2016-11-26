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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import camp.computer.clay.Clay;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.util.ProjectLayoutStrategy;
import camp.computer.clay.engine.manager.Event;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.platform.communication.Internet;
import camp.computer.clay.platform.communication.UDPHost;
import camp.computer.clay.platform.graphics.PlatformRenderSurface;
import camp.computer.clay.platform.graphics.controls.PlatformUi;
import camp.computer.clay.platform.scripting.JavaScriptEngine;
import camp.computer.clay.platform.sound.SpeechSynthesisEngine;
import camp.computer.clay.platform.spatial.OrientationInput;
import camp.computer.clay.platform.util.ViewGroupHelper;

public class Application extends FragmentActivity implements PlatformInterface {

    // <SETTINGS>
    public static class Settings {
        public static final boolean ENABLE_SPEECH_OUTPUT = false;
        public static final boolean ENABLE_MOTION_INPUT = true;

        public static final long MESSAGE_SEND_FREQUENCY = 5000; // 500;

        /**
         * Hides the operating system's status and navigation bars. Setting this to false is helpful
         * during debugging.
         */
        public static final boolean ENABLE_FULLSCREEN = true;
        public static final int FULLSCREEN_SERVICE_PERIOD = 2000;

        public static boolean ENABLE_HARDWARE_ACCELERATION = true;

        public static boolean ENABLE_JAVASCRIPT_ENGINE = true;

        // Platform Adapter
        public static boolean ENABLE_MESSAGING_SERVICE = true;
    }
    // </SETTINGS>

    private static Context context;

    private static Application application;

    private PlatformUi platformUi;

    public PlatformRenderSurface platformRenderSurface;

    private SpeechSynthesisEngine speechSynthesisEngine;

    private OrientationInput orientationInput;

    private UDPHost UDPHost;

    private Internet networkResource;

    private JavaScriptEngine javaScriptEngine;

    private Clay clay;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Settings.ENABLE_SPEECH_OUTPUT) {
            if (requestCode == SpeechSynthesisEngine.CHECK_CODE) {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    speechSynthesisEngine = new SpeechSynthesisEngine(this);
                } else {
                    Intent install = new Intent();
                    install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(install);
                }
            }
        }
    }

    public static int applicationViewId;

    /**
     * Called when the activity is getFirstEvent created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // <PLATFORM>

        // Store reference to the application.
        Application.application = this;

        // "Return the context of the single, global Application object of the current process.
        // This generally should only be used if you need a Context whose lifecycle is separate
        // from the current context, that is tied to the lifetime of the process rather than the
        // current component." (Android Documentation)
        Application.context = getApplicationContext();

        // Set up Platform Helpers
        ViewGroupHelper.setContext(getApplicationContext());

        // Generate Application View ID
        applicationViewId = PlatformUi.generateViewId();

        // Create Application Layout View
        FrameLayout applicationView = new FrameLayout(getApplicationContext());
        applicationView.setId(applicationViewId);
        setContentView(applicationView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Lock screen orientation to vertical orientation.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Settings.ENABLE_FULLSCREEN) {
            startFullscreenService();
        }

        // Prevent on-screen keyboard from pushing up content. Instead it will overlay content.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // Sensor Interface
        if (Settings.ENABLE_MOTION_INPUT) {
            orientationInput = new OrientationInput(getApplicationContext());
        }

        // Check availability of speech synthesis engine on Android host device.
        if (Settings.ENABLE_SPEECH_OUTPUT) {
            SpeechSynthesisEngine.checkAvailability(this);
        }

        // <HARDWARE_ACCELERATION>
        if (Settings.ENABLE_HARDWARE_ACCELERATION) {
            View view = findViewById(applicationViewId);
            boolean isHardwareAccelerated = view.isHardwareAccelerated();
            Log.v("HardwareAcceleration", "isHardwareAccelerated: " + isHardwareAccelerated);
        }
        // </HARDWARE_ACCELERATION>

        // </PLATFORM>

        // <PLATFORM_ADAPTER>
        platformUi = new PlatformUi(getApplicationContext());

        /*
        for (int i = 0; i < 100; i++) {
            String outgoingMessage = "announce device " + UUID.randomUUID();
            CRC16 CRC16 = new CRC16();
            int seed = 0;
            byte[] outgoingMessageBytes = outgoingMessage.getBytes();
            int check = CRC16.calculate(outgoingMessageBytes, seed);
            String outmsg =
                    "\f" +
                            String.valueOf(outgoingMessage.length()) + "\t" +
                            String.valueOf(check) + "\t" +
                            "text" + "\t" +
                            outgoingMessage;
            Log.v("CRC_Demo", "" + outmsg);
        }
        */

        // Create Platform Rendering Surface and add it to the application view.
        platformRenderSurface = new PlatformRenderSurface(getContext());
        FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(applicationViewId);
        frameLayout.addView(platformRenderSurface, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        platformRenderSurface.onResume();

        // based on... try it! better performance? https://www.javacodegeeks.com/2011/07/android-game-development-basic-game_05.html
        //setContentView(visualizationSurface);

        // UDP Datagram Server
        if (UDPHost == null) {
            UDPHost = new UDPHost("udp");
            UDPHost.startServer(); // TODO: Move into BootstrapComponent
        }

        // Internet Network Interface
        if (networkResource == null) {
            networkResource = new Internet();
        }

        // Start Messaging Thread
        if (Settings.ENABLE_MESSAGING_SERVICE) {
            startMessagingService();
            // TODO: Move MessengingService.startService() into BootstrapComponent
        }
        // </PLATFORM_ADAPTER>

        // <ENGINE>
        // Clay
        clay = new Clay();
        clay.addPlatform(this); // Add the view provided by the host device.

        if (UDPHost == null) {
            clay.addHost(this.UDPHost);
        }

        if (networkResource == null) {
            clay.addResource(this.networkResource);
        }
        // </ENGINE>

        // <REDIS>
        /*
        new JedisConnectToDatabaseTask().execute("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com:14268");

        while (this.jedis == null) {
            // Waiting for connection...
        }

        new Thread(
                new RedisSubThread(this.jedis)
        ).start();
        */
        // </REDIS>


        // <REDIS>
        /*
        RedisDBThread redisDB = new RedisDBThread();
        redisDB.start();
        */
        // </REDIS>

        // <JAVASCRIPT_ENGINE>
        if (Settings.ENABLE_JAVASCRIPT_ENGINE) {
            javaScriptEngine = new JavaScriptEngine();
        }
        // </JAVASCRIPT_ENGINE>

        /*
        // <SHOW_MAIN_MENU>
        platformUi.openMainMenu();
        // </SHOW_MAIN_MENU>
        */
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Rendering Surface
        platformRenderSurface.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // UDP Client/Server
        if (UDPHost == null) {
            UDPHost = new UDPHost("udp");
        }
        if (!UDPHost.isActive()) {
            UDPHost.startServer();
        }

        // Rendering Surface
        platformRenderSurface.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Speech Synthesis Engine. Stop.
        if (speechSynthesisEngine != null) {
            speechSynthesisEngine.destroy();
        }
    }

    /**
     * Configure platform keyboard input handler.
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // TODO: Queue key events in inputSystem

        switch (keyCode) {
            case KeyEvent.KEYCODE_S: {
                platformUi.openSettings();
                //your Action code
                return true;
            }

            case KeyEvent.KEYCODE_R: {
                World.getWorld().getSystem(PortableLayoutSystem.class).adjustLayout(new ProjectLayoutStrategy());
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
    public static Application getInstance() {
        return Application.application;
    }

    public PlatformUi getPlatformUi() {
        return this.platformUi;
    }

    //----------------------------------------------------------------------------------------------

    // <FULLSCREEN_SERVICE>
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
                    fullscreenServiceHandler.postDelayed(this, Settings.FULLSCREEN_SERVICE_PERIOD);
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
    public void startMessagingService() {
        // Start the initial worker thread (runnable task) by posting through the messagingThreadHandler
        final Handler messagingThreadHandler = new Handler();
        messagingThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // Action the outgoing messages
                clay.update();

                // Repeat this the same runnable code block again another 2 seconds
                messagingThreadHandler.postDelayed(this, Settings.MESSAGE_SEND_FREQUENCY);
            }
        });
    }
    // </MESSAGING_THREAD>

    // <PLATFORM_THREAD_ADAPTER>
    /*
    public abstract class PlatformThread {
        long delay = 1000L;

        public abstract void execute();
    }

    private List<PlatformThread> platformThreads = new ArrayList<>(); // TODO: Add to manager.

    public void addPlatformThread(final PlatformThread platformThread) {
        // Start the initial worker thread (runnable task) by posting through the messagingThreadHandler
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Execute thread code
                platformThread.execute();

                // Repeat this the same runnable code block again another 2 seconds
                handler.postDelayed(this, platformThread.delay);
            }
        });
    }
    */
    // </PLATFORM_THREAD_ADAPTER>

    public PlatformRenderSurface getPlatformRenderSurface() {
        return this.platformRenderSurface;
    }

    // <TODO: DELETE>
    @Override
    public void setClay(Clay clay) {
        this.clay = clay;
    }

    @Override
    public Clay getClay() {
        return this.clay;
    }
    // </TODO: DELETE>
}
