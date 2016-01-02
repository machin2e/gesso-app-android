package computer.clay.protocolizer;

import android.util.Log;

import java.util.UUID;

public class Behavior {

    public static int BEHAVIOR_COUNT = 0; // TODO: Replace this with UUID!

    private UUID uuid;

    private String title;
    private String description;
    private String transform;

    private BehaviorConstruct behaviorConstruct;

//    private Loop loop = null;

    Behavior (String title) {
        this.behaviorConstruct = null;

        this.uuid = UUID.randomUUID();

        this.title = title;
        this.description = "";
        this.transform = "";

//        this.loop = new Loop ();
    }

    // TODO: Remove this!
    Behavior (BehaviorConstruct behaviorConstruct) {
        this.behaviorConstruct = behaviorConstruct;

        this.uuid = UUID.randomUUID();

        this.title = title;
        this.description = "";
        this.transform = "";
    }

    public BehaviorConstruct getBehaviorConstruct () {
        return this.behaviorConstruct;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getTitle () {
        return this.title;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getDescription () {
        return this.description;
    }

    public void setTransform (String transform) {
        this.transform = transform;
    }

    public String getTransform () {
        return this.transform;
    }

    // TODO: setAction : Specifiy the URI of the action for which to download a script (to relay to Clay).
    // TODO: defineAction
    // TODO: clearAction

    public void perform () {
        // TODO: Perform the action, whatever it is!

        Log.v("Clay", "Performing behavior " + this.getTitle() + ".");
    }

//    public class HttpRequestTask extends AsyncTask<String, Void, String[]> { // Extend AsyncTask and use void generics (for now)
//
//        private final String LOG_TAG = "Clay"; // HttpRequestTask.class.getSimpleName();
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            /* Get weather data from an Internet source. */
//
//            if (params.length == 0) {
//                return null;
//            }
//
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String responseJsonStr = null;
//            String[] responseData;
//
////            String format = "json";
////            String units = "metric";
////            int numDays = 7;
//
//            try {
//                // Construct the URL for the HTTP request
//
//                final String CLAY_UNIT_BASE_URL = "http://192.168.0.113/message?";
//                final String CONTENT_PARAM = "content";
//
//                // This approach enables the user to set the zip code from the settings activity.
////                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()); // Get preferences for this activity
////                String remoteHostUri = prefs.getString(getString(R.string.pref_remote_host_key), // If there's a value stored for the location key in preferences, use it...
////                        getString(R.string.pref_remote_host_default));
//
//                // TODO: Replace the " " space character with "%20" string.
//
//                Uri builtUri = Uri.parse(CLAY_UNIT_BASE_URL).buildUpon()
//                        .appendQueryParameter(CONTENT_PARAM, params[0])
//                        .build();
//
//                URL url = new URL(builtUri.toString());
//
//                Log.v(LOG_TAG, "SENDING REQUEST TO: " + url.toString());
//
//                // Create the request to send to the server and open the connection.
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                responseJsonStr = buffer.toString();
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attemping
//                // to parse it.
//                return null;
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            // All this just for: return getDataFromJson(forecastJsonStr, numDays);
//            try {
//                return getDataFromJson(responseJsonStr, numDays);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//
//            // This only happens if there was an error getting or parsing the forecast.
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String[] result) {
//            if (result != null) {
//                httpRequestAdapter.clear();
//                for (String dayForecastStr : result) {
//                    httpRequestAdapter.add(dayForecastStr);
//                }
//                // New day is back from the server at this point!
//
//                // NOTE: Array adapter internally calls: adapter.notifyDataSetChanged()
//            }
//        }
//
//        /* The date/time conversion code is going to be moved outside the asynctask later,
//         * so for convenience we're breaking it out into its own method now.
//         */
//        private String getReadableDateString(long time){
//            // Because the API returns a unix timestamp (measured in seconds),
//            // it must be converted to milliseconds in order to be converted to valid date.
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(time);
//        }
//
//        /**
//         * Prepare the weather high/lows for presentation.
//         */
//        private String formatHighLows(double high, double low) {
//            // For presentation, assume the user doesn't care about tenths of a degree.
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return highLowStr;
//        }
//
//        /**
//         * Take the String representing the complete serialized object in JSON format and
//         * pull out the data we need to construct the Strings needed to construct the object
//         * locally.
//         *
//         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//         * into an Object hierarchy for us.
//         */
//        private String[] getDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            String[] resultStrs = new String[numDays];
//            for(int i = 0; i < weatherArray.length(); i++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // The date/time is returned as a long.  We need to convert that
//                // into something human-readable, since most people won't read "1400356800" as
//                // "this saturday".
//                long dateTime;
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay+i);
//                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                double high = temperatureObject.getDouble(OWM_MAX);
//                double low = temperatureObject.getDouble(OWM_MIN);
//
//                highAndLow = formatHighLows(high, low);
//                resultStrs[i] = day + " - " + description + " - " + highAndLow;
//            }
//
//            return resultStrs;
//
//        }
//    }
}
