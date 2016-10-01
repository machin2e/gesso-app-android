package camp.computer.clay.application;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import camp.computer.clay.Clay;
import camp.computer.clay.application.communication.UDPHost;
import camp.computer.clay.application.communication.redis.RedisSubThread;
import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.application.sound.SpeechOutput;
import camp.computer.clay.application.sound.ToneOutput;
import camp.computer.clay.application.spatial.OrientationInput;
import camp.computer.clay.host.DisplayHostInterface;
import camp.computer.clay.host.Internet;
import camp.computer.clay.model.Extension;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.old_model.PhoneHost;
import redis.clients.jedis.Jedis;

public class Application extends FragmentActivity implements DisplayHostInterface { // was Application
    // rename Application to Setup? to provide analog to "setup" functions in classes?

    // <Settings>
    private static final boolean ENABLE_TONE_OUTPUT = false;
    private static final boolean ENABLE_SPEECH_OUTPUT = false;
    private static final boolean ENABLE_MOTION_INPUT = true;

    private static final long MESSAGE_SEND_FREQUENCY = 500;

    public static boolean ENABLE_GEOMETRY_LABELS = false;

    /**
     * Hides the operating system's status and navigation bars. Setting this to false is helpful
     * during debugging.
     */
    private static final boolean ENABLE_FULLSCREEN = true;
    // </Settings>

    public Display display;

    private SpeechOutput speechOutput;

    private ToneOutput toneOutput;

    private OrientationInput orientationInput;

    private static Context context;

    private static Application applicationView;

    private Clay clay;

    private camp.computer.clay.application.communication.UDPHost UDPHost;

    private Internet networkResource;

    Prompt ui;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

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

    public Prompt getActionPrompts() {
        return this.ui;
    }

    public void openActionEditor(Extension extension) {
        final RelativeLayout pathEditor = (RelativeLayout) findViewById(R.id.path_editor_view);
        pathEditor.setVisibility(View.VISIBLE);
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

        // Sensor Interface
        if (ENABLE_MOTION_INPUT) {
            orientationInput = new OrientationInput(getApplicationContext());
        }

        if (ENABLE_FULLSCREEN) {
            startFullscreenService();
        }

        // Display Interface
        Application.applicationView = this;

        ui = new Prompt(this);

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

        // Space Surface
        display = (Display) findViewById(R.id.app_surface_view);
        display.onResume();

        // based on... try it! better performance? https://www.javacodegeeks.com/2011/07/android-game-development-basic-game_05.html
        //setContentView(visualizationSurface);

        // Path Editor
        final RelativeLayout pathEditor = (RelativeLayout) findViewById(R.id.path_editor_view);
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

                    addPathPatchAction();

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

        // <Cache>
        // </Cache>

        // Read default form profiles
        String jsonString = null;
        try

        {
            InputStream inputStream = getContext().getAssets().open("Hosts.json");
            int fileSize = inputStream.available();
            byte[] fileBuffer = new byte[fileSize];
            inputStream.read(fileBuffer);
            inputStream.close();
            jsonString = new String(fileBuffer, "UTF-8");

        } catch (
                IOException e
                )

        {
            e.printStackTrace();
        }

        // Create JSON object
        JSONObject jsonObject = null;
        try

        {
            jsonObject = new JSONObject(jsonString);

            JSONObject formObject = jsonObject.getJSONObject("form");

            String formName = formObject.getString("name");

            Log.v("Configuration", "reading JSON name: " + formName);

        } catch (
                JSONException e
                )

        {
            e.printStackTrace();
        }


        // Clay
        clay = new

                Clay();

        clay.addDisplay(this); // Add the view provided by the host device.

        // UDP Datagram Server
        if (UDPHost == null)

        {
            UDPHost = new UDPHost("udp");
            clay.addHost(this.UDPHost);
            UDPHost.startServer();
        }

        // Internet Network Interface
        if (networkResource == null)

        {
            networkResource = new Internet();
            clay.addResource(this.networkResource);
        }

        /*
        // Descriptor Database
        SQLiteStoreHost sqliteStoreHost = new SQLiteStoreHost(getClay(), "sqlite");
        getClay().setStore(sqliteStoreHost);

        // Initialize content store
        getClay().getStore().erase();
        getClay().getCache().populate(); // alt. syntax: useClay().useCache().toPopulate();
        getClay().getStore().generate();
        getClay().getCache().populate();
        // getClay().simulateSession(true, 10, false);
        */

        // Prevent on-screen keyboard from pushing up content
        getWindow()

                .

                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // <CHAT_AND_CONTEXT_SCOPE>
        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final HorizontalScrollView messageContentLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_content_layout_perspective);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final TextView messageContentHint = (TextView) findViewById(R.id.message_content_hint);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById(R.id.context_button);
        // </CHAT_AND_CONTEXT_SCOPE>

        // <CHAT>

        messageContentHint.setOnTouchListener(new View.OnTouchListener()

                                              {

                                                  @Override
                                                  public boolean onTouch(View v, MotionEvent event) {
                                                      messageContentHint.setVisibility(View.GONE);
                                                      showMessageKeyboard();
                                                      return false;
                                                  }
                                              }

        );

        // Hide scrollbars in keyboard
        messageKeyboardLayoutPerspective.setVerticalScrollBarEnabled(false);
        messageKeyboardLayoutPerspective.setHorizontalScrollBarEnabled(false);

        // Hide scrollbars in message content
        messageContentLayoutPerspective.setVerticalScrollBarEnabled(false);
        messageContentLayoutPerspective.setHorizontalScrollBarEnabled(false);

        generateKeyboard();

        // Set up interactivity
        messageContentLayout.setOnTouchListener(new View.OnTouchListener()

                                                {
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
//            public boolean onTouch(Camera v, MotionEvent event) {
//                int inType = timelineButton.getInputType(); // backup the input type
//                timelineButton.setInputType(InputType.TYPE_NULL); // disable soft input
//                timelineButton.onTouchEvent(event); // call native handler
//                timelineButton.setInputType(inType); // restore input type
//                return true; // consume pointerCoordinates even
//            }
                                                }

        );

        messageContentLayout.setOnClickListener(new View.OnClickListener()

                                                {
                                                    @Override
                                                    public void onClick(View v) {


                                                    }
                                                }

        );
        // </CHAT>

        // </CONTEXT_SCOPE>
        contextScope.setOnTouchListener(new View.OnTouchListener()

                                        {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {

                                                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                                                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                                                    // Get button holder
                                                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.context_button_holder);

                                                    // Get screen width and height of the device
                                                    DisplayMetrics metrics = new DisplayMetrics();
                                                    Application.getView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
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

//                    Animation animation = new Animation();
//                    animation.moveToPoint(relativeLayout, originPoint, 300);

                                                    // Reset the message envelope
                                                    messageContent.removeAllViews();
                                                    messageKeyboardLayout.setVisibility(View.GONE);

                                                    // Replace the hint
                                                    messageContent.addView(messageContentHint);
                                                    messageContentHint.setVisibility(View.VISIBLE);
                                                }

                                                return false;
                                            }
                                        }

        );
        // </CONTEXT_SCOPE>

        // Start the initial worker thread (runnable task) by posting through the handler
        handler.post(runnableCode);

        // Check availability of speech synthesis engine on Android host device.
        if (ENABLE_SPEECH_OUTPUT)

        {
            SpeechOutput.checkAvailability(this);
        }

        if (ENABLE_TONE_OUTPUT)

        {
            toneOutput = new ToneOutput();
        }

        hideChat();

        // <REDIS>
//        new JedisConnectToDatabaseTask().execute("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com:14268");

//        while (this.jedis == null) {
//            // Waiting for connection...
//        }

//        new Thread(
//                new RedisSubThread(this.jedis)
//        ).start();
        // </REDIS>
    }

    // <REDIS>
    private Jedis jedis;

    public class JedisConnectToDatabaseTask extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... args) {
            try {
                String uri = args[0].split(":")[0];
                int port = Integer.parseInt(args[0].split(":")[1]);
                Log.v("Redis", "Jedis Task");
                //jedis = new Jedis("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com", 14268);
                jedis = new Jedis(uri, port);
                jedis.auth("testdb");
                jedis.set("foo", "bar");
                String value = jedis.get("foo");
                Log.v("Redis", "foo: " + value);

//                jedis.publish("events", args[0]);
//                Log.v("Redis", "called publish");

                new Thread(new RedisSubThread(jedis)).start();

                //return theRSSHandler.getFeed();
                return null;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    public void publish(String message) {
        new JedisPublishTask().execute(message);
    }

    class JedisPublishTask extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... urls) {
            try {
//                Log.v("Jedis", "Jedis Task");
//                Jedis jedis = new Jedis("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com", 14268);
//                jedis.auth("testdb");
//                jedis.setValue("foo", "bar");
//                String value = jedis.get("foo");
//                Log.v("Jedis", "foo: " + value);

                jedis.publish("events", urls[0]);
                Log.v("Redis", "called publish");

                //return theRSSHandler.getFeed();
                return null;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }

    }
    // </REDIS>

    public void hideChat() {
        // <CHAT_AND_CONTEXT_SCOPE>
        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final HorizontalScrollView messageContentLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_content_layout_perspective);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final TextView messageContentHint = (TextView) findViewById(R.id.message_content_hint);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById(R.id.context_button);
        // </CHAT_AND_CONTEXT_SCOPE>

        messageContentLayout.setVisibility(View.GONE);
    }

    private void showMessageKeyboard() {

        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById(R.id.context_button);

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

    public camp.computer.clay.util.geometry.Point convertToVisiblePosition(Point point) {
        camp.computer.clay.util.geometry.Point visiblePosition = new camp.computer.clay.util.geometry.Point();
        return visiblePosition;
    }

    public float convertDipToPx(float dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return px;
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
        final Button contextScope = (Button) findViewById(R.id.context_button);

        // <CHAT>

        // Add keys to keyboard
        final Button messageKey = new Button(getContext());
        messageKey.setText(settings);
        messageKey.setTextSize(12.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 0, 5, 0);
        messageKey.setPadding(0, 0, 0, 0);
        messageKey.setLayoutParams(layoutParams);
        messageKey.getLayoutParams().height = 100;
        messageKey.setBackgroundResource(R.drawable.chat_message_key);

        messageKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // final EditText chatEntry = (EditText) findViewById(R.id.chat_entry);
                appendToChatMessage(messageKey.getText().toString());

                validateChatMessage();
            }
        });

        messageKeyboard.addView(messageKey);
    }

    private void validateChatMessage() {

        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById(R.id.context_button);

        contextScope.setText("✓");
    }

    private void appendToChatMessage(String text) {
        final RelativeLayout messageContentLayout = (RelativeLayout) findViewById(R.id.message_content_layout);
        final HorizontalScrollView messageContentLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_content_layout_perspective);
        final LinearLayout messageContent = (LinearLayout) findViewById(R.id.message_content);
        final RelativeLayout messageKeyboardLayout = (RelativeLayout) findViewById(R.id.message_keyboard_layout);
        final HorizontalScrollView messageKeyboardLayoutPerspective = (HorizontalScrollView) findViewById(R.id.message_keyboard_layout_perspective);
        final LinearLayout messageKeyboard = (LinearLayout) findViewById(R.id.message_keyboard);
        final Button contextScope = (Button) findViewById(R.id.context_button);
        // </CHAT_AND_CONTEXT_SCOPE>

        // <CHAT>

        // Add keys to keyboard
        final Button messageWord = new Button(getContext());
        messageWord.setText(text);
        messageWord.setTextSize(12.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
//                messageContent.addDisplay(messageKey);
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

    private void addPathPatchAction() {

        final TextView actionConstruct = new TextView(getContext());
        actionConstruct.setText("Event (<Port> <Port> ... <Port>)\nExpose: <Port> <Port> ... <Port>");
        int horizontalPadding = (int) convertDipToPx(20);
        int verticalPadding = (int) convertDipToPx(10);
        actionConstruct.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        actionConstruct.setBackgroundColor(Color.parseColor("#44000000"));

        final LinearLayout pathPatchActionList = (LinearLayout) findViewById(R.id.path_editor_action_list);

        actionConstruct.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    pathPatchActionList.removeView(actionConstruct);

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

        pathPatchActionList.addView(actionConstruct);
    }

    // <FULLSCREEN_SERVICE>
    public static final int FULLSCREEN_SERVICE_PERIOD = 2000;

    private boolean enableFullscreenService = false;

    private Handler fullscreenServiceHandler = new Handler();
    private Runnable fullscreenServiceRunnable = new Runnable() {
        @Override
        public void run() {
            // Do what you need to do.
            // e.g., foobar();
            hideSystemUI();

            // Uncomment this for periodic callback
            if (enableFullscreenService) {
                fullscreenServiceHandler.postDelayed(this, FULLSCREEN_SERVICE_PERIOD);
            }
        }
    };

    private void startFullscreenService() {
        enableFullscreenService = true;
        fullscreenServiceHandler.postDelayed(fullscreenServiceRunnable, Event.MINIMUM_HOLD_DURATION);
    }

    public void stopFullscreenService() {
        enableFullscreenService = false;
    }

    /**
     * References:
     * - http://stackoverflow.com/questions/9926767/is-there-a-way-to-hide-the-system-navigation-bar-in-android-ics
     */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    // </FULLSCREEN_SERVICE>

    @Override
    protected void onPause() {
        super.onPause();

        // <VISUALIZATION>
        display.onPause();
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
        display.onResume();
        // </VISUALIZATION>
    }

    // Create the Handler object. This will be run on the main thread by default.
    Handler handler = new Handler();

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Action the outgoing messages
            clay.update();

            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, MESSAGE_SEND_FREQUENCY);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop speech generator
        if (speechOutput != null) {
            speechOutput.destroy();
        }
    }

    public static Context getContext() {
        return Application.context;
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
    public void addDeviceView(PhoneHost phoneHost) {

    }

    @Override
    public void refreshListViewFromData(PhoneHost phoneHost) {
        // TODO: Update the view to reflect the latest state of the object entity
    }

    // TODO: Rename to something else and make a getView() function specific to the
    // TODO: (cont'd) display interface.
    public static Application getView() {
        return Application.applicationView;
    }

    public Display getDisplay() {
        return this.display;
    }

    public double getFramesPerSecond() {
        return getDisplay().getDisplayOutput().getFramesPerSecond();
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
}
