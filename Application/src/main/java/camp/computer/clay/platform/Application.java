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
import android.widget.FrameLayout;

import camp.computer.clay.engine.Engine;
import camp.computer.clay.engine.Platform;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.util.HostLayoutStrategy;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.platform.compute.JavaScriptEngine;
import camp.computer.clay.platform.graphics.RenderSurface;
import camp.computer.clay.platform.graphics.controls.Widgets;
import camp.computer.clay.platform.io.OrientationInput;
import camp.computer.clay.platform.io.SpeechSynthesizer;
import camp.computer.clay.platform.util.DeviceDimensionsHelper;
import camp.computer.clay.platform.util.ViewGroupHelper;
import camp.computer.clay.util.Random;

public class Application extends FragmentActivity {

    // <SETTINGS>
    public static class Settings {
        public static final boolean ENABLE_SPEECH_OUTPUT = false;
        public static final boolean ENABLE_MOTION_INPUT = true;

        /**
         * Hides the operating system's status and navigation bars. Setting this to false is helpful
         * during debugging.
         */
        public static final boolean ENABLE_FULLSCREEN = true;
        public static final int FULLSCREEN_SERVICE_PERIOD = 2000;

        public static boolean ENABLE_HARDWARE_ACCELERATION = false;

        public static boolean ENABLE_JAVASCRIPT_ENGINE = true;
    }
    // </SETTINGS>

    private static Context context;

    private static Application application;

    private Widgets widgets;

    public RenderSurface renderSurface;

    private SpeechSynthesizer speechSynthesizer;

    private OrientationInput orientationInput;

    private JavaScriptEngine javaScriptEngine;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Settings.ENABLE_SPEECH_OUTPUT) {
            if (requestCode == SpeechSynthesizer.CHECK_CODE) {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    speechSynthesizer = new SpeechSynthesizer(this);
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

        if (Settings.ENABLE_FULLSCREEN) {
            startFullscreenService();
        }

        // Set up Platform Helpers
        ViewGroupHelper.setContext(getApplicationContext());

        // Generate Application View ID
        applicationViewId = Widgets.generateViewId();

        // Create Application Layout View
        FrameLayout applicationView = new FrameLayout(getApplicationContext());
        applicationView.setId(applicationViewId);
        setContentView(applicationView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Lock screen orientation to vertical orientation.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Prevent on-screen keyboard from pushing up content. Instead it will overlay content.
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // Sensor Interface
        if (Settings.ENABLE_MOTION_INPUT) {
            orientationInput = new OrientationInput(getApplicationContext());
        }

        // Check availability of speech synthesis engine on Android host device.
        if (Settings.ENABLE_SPEECH_OUTPUT) {
            SpeechSynthesizer.checkAvailability(this);
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
        widgets = new Widgets(getApplicationContext());

        // Create Platform Rendering Surface and add it to the application view.
        renderSurface = new RenderSurface(getContext());
        FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(applicationViewId);
        frameLayout.setPadding(0, 0, 0, 0);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(
                DeviceDimensionsHelper.getDisplayWidth(context), // FrameLayout.LayoutParams.MATCH_PARENT
                DeviceDimensionsHelper.getDisplayHeight(context) // FrameLayout.LayoutParams.MATCH_PARENT
        );
        frameLayoutParams.setMargins(0, 0, 0, 0);
        frameLayout.addView(renderSurface, frameLayoutParams);

        renderSurface.onResume();
        // </PLATFORM_ADAPTER>

        // <JAVASCRIPT_ENGINE>
        if (Settings.ENABLE_JAVASCRIPT_ENGINE) {
            javaScriptEngine = new JavaScriptEngine();
        }
        // </JAVASCRIPT_ENGINE>

        /*
        // <SHOW_MAIN_MENU>
        widgets.openMainMenu();
        // </SHOW_MAIN_MENU>
        */

        // <TIMER_THREAD>
        //final Engine engine = new Engine(new Platform());
        new Engine(new Platform());
        // </TIMER_THREAD>
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Rendering Surface
        renderSurface.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Rendering Surface
        renderSurface.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Speech Synthesis Engine. Stop.
        if (speechSynthesizer != null) {
            speechSynthesizer.destroy();
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
            case KeyEvent.KEYCODE_A: {
                Entity host = World.getWorld().createEntity(Host.class);
                World.getWorld().getSystem(PortableLayoutSystem.class).updateWorldLayout(new HostLayoutStrategy());

                // Automatically focus on the first Host that appears in the workspace/world.
                if (World.getWorld().entities.get().size() == 1) {
                    Entity camera = World.getWorld().entities.get().filterWithComponent(Camera.class).get(0);
                    camera.getComponent(Camera.class).focus = host;
                    camera.getComponent(Camera.class).mode = Camera.Mode.FOCUS;
                } else {
                    Entity camera = World.getWorld().entities.get().filterWithComponent(Camera.class).get(0);
                    camera.getComponent(Camera.class).focus = null;
                    camera.getComponent(Camera.class).mode = Camera.Mode.FOCUS;
                }


                return true;
            }

            case KeyEvent.KEYCODE_D: {
                Group<Entity> hosts = World.getWorld().entities.get().filterWithComponent(Host.class);
                if (hosts.size() > 0) {
                    Entity randomHost = hosts.get(Random.generateRandomInteger(0, hosts.size()));
                    World.getWorld().entities.remove(randomHost);
                }

                World.getWorld().getSystem(PortableLayoutSystem.class).updateWorldLayout(new HostLayoutStrategy());
                return true;
            }

            case KeyEvent.KEYCODE_S: {
                widgets.openSettings();
                //your Action code
                return true;
            }

            case KeyEvent.KEYCODE_R: {
                World.getWorld().getSystem(PortableLayoutSystem.class).updateWorldLayout(new HostLayoutStrategy());
                return true;
            }

            case KeyEvent.KEYCODE_M: {
                widgets.openMainMenu();
                return true;
            }

            case KeyEvent.KEYCODE_L: {
                // TODO: log
                return true;
            }

            case KeyEvent.KEYCODE_C: {
                // Monitor
                if (World.ENABLE_OVERLAY) {
                    World.ENABLE_OVERLAY = false;
                } else {
                    World.ENABLE_OVERLAY = true;
                }
                return true;
            }

            case KeyEvent.KEYCODE_G: {
                // Monitor
                if (World.ENABLE_GEOMETRY_OVERLAY) {
                    World.ENABLE_GEOMETRY_OVERLAY = false;
                    World.ENABLE_GEOMETRY_ANNOTATIONS = false;
                } else {
                    World.ENABLE_GEOMETRY_OVERLAY = true;
                    World.ENABLE_GEOMETRY_ANNOTATIONS = true;
                }
                return true;
            }

            case KeyEvent.KEYCODE_1: {
                // Monitor
                Entity camera = World.getWorld().entities.get().filterWithComponent(Camera.class).get(0);
                if (camera != null) {
                    camera.getComponent(Physics.class).targetTransform.scale -= 0.10;
                }
                return true;
            }

            case KeyEvent.KEYCODE_2: {
                // Monitor
                Entity camera = World.getWorld().entities.get().filterWithComponent(Camera.class).get(0);
                if (camera != null) {
                    camera.getComponent(Physics.class).targetTransform.scale = 1.0;
                }
                return true;
            }

            case KeyEvent.KEYCODE_3: {
                // Monitor
                Entity camera = World.getWorld().entities.get().filterWithComponent(Camera.class).get(0);
                if (camera != null) {
                    camera.getComponent(Physics.class).targetTransform.scale += 0.10;
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

    public Widgets getWidgets() {
        return this.widgets;
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
        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_IMMERSIVE);
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    // </FULLSCREEN_SERVICE>


//    // <MESSAGING_THREAD>
//    // Create the Handler object. This will be run on the main thread by default.
//    public void startMessagingService() {
//        // Start the initial worker thread (runnable task) by posting through the messagingThreadHandler
//        final Handler messagingThreadHandler = new Handler();
//        messagingThreadHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                // Action the outgoing messages
//                clay.update();
//
//                // Repeat this the same runnable code block again another 2 seconds
//                messagingThreadHandler.postDelayed(this, Settings.MESSAGE_SEND_FREQUENCY);
//            }
//        });
//    }
//    // </MESSAGING_THREAD>

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
}
