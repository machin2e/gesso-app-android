package camp.computer.clay.platform.io;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class SpeechSynthesizer implements TextToSpeech.OnInitListener {

    // <SETTINGS>
    public static final int CHECK_CODE = 0x1;
    public static final int LONG_DURATION = 5000;
    public static final int SHORT_DURATION = 1200;
    // </SETTINGS>

    // <GENERATOR>
    private TextToSpeech tts;
    // </GENERATOR>

    // <STATE>
    private boolean isReady = false;
    private boolean isAllowed = false;
    // </STATE>

    public SpeechSynthesizer(Context context) {
        tts = new TextToSpeech(context, this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Voice voice = tts.getVoice();
        }

//        Random random = new Random ();
//        float speechRate = random.nextFloat();
//        tts.setSpeechRate(speechRate);
//
//        float pitch = random.nextFloat();
//        tts.setPitch(pitch);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            // Get available voices
//            Set<Voice> voices = tts.getVoices();
//
//            Log.v("Clay", "voices.size() = " + voices.size());
//
////            for (Voice voice : voices) {
//////                // Set the chosen voice's synthesis characteristics
//////                voice.getEvent
////            }
//
////            // Set the chosen voice's synthesis characteristics
////            voice.getEvent
//        }
    }

    public static void checkAvailability(Activity activity){
        Intent check = new Intent();

        // The ACTION_CHECK_TTS_DATA activity action starts the activity from the platform
        // TextToSpeech engine to verify the proper installation and availability of the resource
        // files on the system.
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        // Launch an activity for which you would like a result when it finished. When this
        // activity exits, your onActivityResult() method will be called with the given requestCode.
        activity.startActivityForResult(check, CHECK_CODE);
    }

    public void setSpeechCharacteristics () {

        Random random = new Random ();

        // Set speech rate
        float speechRate = random.nextFloat();
        tts.setSpeechRate(speechRate);

        // Set pitch
        float pitch = random.nextFloat();
        tts.setPitch(pitch);

        // Set language and locale
        int language = random.nextInt(3);
        if (language == 0) {
            tts.setLanguage(Locale.UK);
        } else if (language == 1) {
            tts.setLanguage(Locale.US);
        } else if (language == 2) {
            tts.setLanguage(Locale.CANADA);
        }
    }

    public void speakPhrase(String phrase) {
        this.allow(true);
        this.speak(phrase);
        this.allow(false);
    }

    private void speak (String text) {

        // setSpeechCharacteristics ();

        if (tts.isSpeaking()) {
            tts.stop();
        }

        // Speak only if the TTS is isReady
        // and the user has isAllowed speech

        if(isReady && isAllowed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ttsGreater21(text);
            } else {
                ttsUnder20(text);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public boolean isAllowed(){
        return isAllowed;
    }

    public void allow(boolean allowed){
        this.isAllowed = allowed;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            // Change this to match your locale
            tts.setLanguage(Locale.US);
            isReady = true;
        }else{
            isReady = false;
        }
    }
    public void stop () {
        if (isReady && isAllowed) {
            tts.stop();
        }
    }

    public void pause (int duration){
        tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }

    // Free up resources
    public void destroy(){
        tts.shutdown();
    }
}