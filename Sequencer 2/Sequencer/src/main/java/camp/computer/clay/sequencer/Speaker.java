package camp.computer.clay.sequencer;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Speaker implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    private boolean ready = false;

    private boolean allowed = false;

    public Speaker (Context context){
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
//////                voice.get
////            }
//
////            // Set the chosen voice's synthesis characteristics
////            voice.get
//        }
    }

    public boolean isAllowed(){
        return allowed;
    }

    public void allow(boolean allowed){
        this.allowed = allowed;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            // Change this to match your
            // locale
            tts.setLanguage(Locale.US);
            ready = true;
        }else{
            ready = false;
        }
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

    public void speak(String text){

        setSpeechCharacteristics ();

        // Speak only if the TTS is ready
        // and the user has allowed speech

        if(ready && allowed) {
            HashMap<String, String> hash = new HashMap<String,String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_NOTIFICATION));
            tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
        }
    }

    public void pause(int duration){
        tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }

    // Free up resources
    public void destroy(){
        tts.shutdown();
    }
}