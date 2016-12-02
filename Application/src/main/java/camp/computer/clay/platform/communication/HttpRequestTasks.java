package camp.computer.clay.platform.communication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Scriptable;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.Action;
import camp.computer.clay.model.Process;

public class HttpRequestTasks {

    public static final int DEFAULT_ASSET_SERVER_PORT = 8001;

    public static final String DEFAULT_ASSET_SERVER_BASE_URI = "http://192.168.1.2:" + String.valueOf(DEFAULT_ASSET_SERVER_PORT);

    public static final String DEFAULT_HTTP_GET_ACTIONS_URI = DEFAULT_ASSET_SERVER_BASE_URI + "/assets/actions";

    public static final String DEFAULT_HTTP_POST_PROCESS_URI = DEFAULT_ASSET_SERVER_BASE_URI + "/assets/process";

    public static class HttpRequestTask {
        public String uri = DEFAULT_HTTP_POST_PROCESS_URI;
        public Entity entity = null;
    }

    public static class HttpGetRequestTask extends AsyncTask<HttpRequestTask, String, String> {

        //        String serverUri = "http://192.168.1.2:8001/repository/actions";
        String response = "";

        @Override
        protected String doInBackground(HttpRequestTask... httpRequestTasks) {
            HttpRequestTask httpRequestTask = httpRequestTasks[0];

            Log.v("HTTPResponse", "HttpGetRequestTask");
            String responseString = null;
            try {
                URL url = new URL(httpRequestTask.uri);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                if (httpConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    // Do normal input or output stream reading
                    Log.v("HTTPResponse", "HTTP response: " + httpConnection.getResponseCode());

//                    BufferedReader br;
//                    if (200 <= httpConnection.getResponseCode() && httpConnection.getResponseCode() <= 299) {
//                        br = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));
//                    } else {
//                        br = new BufferedReader(new InputStreamReader((httpConnection.getErrorStream())));
//                    }
//
//                    int bytesRead = -1;
//                    char[] buffer = new char[1024];
//                    while ((bytesRead = br.read(buffer)) >= 0) {
//                        // process the buffer, "bytesRead" have been read, no more, no less
//                    }
//
//                    Log.v("HTTPResponse", "HTTP GET response: " + buffer.toString());

                    InputStream is = httpConnection.getInputStream();
                    int ch;
                    StringBuffer sb = new StringBuffer();
                    while ((ch = is.read()) != -1) {
                        sb.append((char) ch);
                    }
                    String jsonString = sb.toString();
                    Log.v("HTTPResponse", "HTTP GET response: " + jsonString);


                    // <RESPONSE_HANDLER>
                    // Create JSON object from file contents
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonString);

                        JSONArray actionsArray = jsonObject.getJSONArray("actions");
//                        String hostTitle = actionsArray.getString("title");

                        for (int i = 0; i < actionsArray.length(); i++) {
                            JSONObject actionObject = actionsArray.getJSONObject(i);
                            String type = actionObject.getString("type");
                            String actionTitle = actionObject.getString("title");
                            String actionScript = actionObject.getString("script");

                            Log.v("HTTPResponse", "^^^");
                            Log.v("HTTPResponse", "type: " + type);
                            Log.v("HTTPResponse", "action title: " + actionTitle);
                            Log.v("HTTPResponse", "action script: " + actionScript);
                            Log.v("HTTPResponse", "---");

                            // Cache Action and Script in Repository. Retrieve Actions and Scripts from Remote Server.
                            // TODO: Create event in global event queue to fetch and cache this data when Builder loads! It shouldn't be happenin' in UI codez!!
                            World.getWorld().repository.createTestAction(actionTitle, actionScript);
                        }

                        // HostEntity host = new HostEntity();

//                        Log.v("Configuration", "reading JSON name: " + hostTitle);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // </RESPONSE_HANDLER>

                } else {
                    response = "FAILED"; // See documentation for more info on response handling
                }
            } catch (Exception e) {
                //TODO Handle problems..
            }
            Log.v("HTTPResponse", "HTTP response: " + responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }

    // TODO: 11/21/2016 Add HttpPostRequestTask to global TCP/UDP communications queue to server.
    public static class HttpPostRequestTask extends AsyncTask<HttpRequestTask, String, String> {

        //        String serverUri = "http://192.168.1.2:8001/jsonPost";
        String response = "";

        @Override
        protected String doInBackground(HttpRequestTask... httpRequestTasks) {
            HttpRequestTask httpRequestTask = httpRequestTasks[0];
            String responseString = null;
            try {
                URL url = new URL(httpRequestTask.uri);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("POST");// type of request
                httpConnection.setRequestProperty("Content-Type", "application/json");//some header you want to add
//                httpConnection.setRequestProperty("Authorization", "key=" + AppConfig.API_KEY);//some header you want to add
                httpConnection.setDoOutput(true);

//                ObjectMapper mapper = new ObjectMapper();
//                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                DataOutputStream dataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
                //content is the object you want to send, use instead of NameValuesPair
//                mapper.writeValue(dataOutputStream, content);

                // <REFACTOR>
                Process process = httpRequestTask.entity.getComponent(Scriptable.class).process;

                String processActionScripts = "{ \"type\": \"process\", \"actions\": [";
                List<Action> actions = process.getActions();
                for (int i = 0; i < actions.size(); i++) {
                    processActionScripts += ""
                            + "{"
                            + "\"title\": \"" + actions.get(i).getTitle() + "\","
                            + "\"script\": \"" + actions.get(i).getScript().getCode() + "\""
                            + "}";

                    if (i < (actions.size() - 1)) {
                        processActionScripts += ", ";
                    }
                }
                processActionScripts += "] }";
                // </REFACTOR>

                //httpConnection.getOutputStream().write("{ \"type\": \"Action\", \"script_uuid\": \"08edbf0a-b020-11e6-80f5-76304dec7eb7\" }".getBytes());
                Log.v("HttpPostRequestTask", "HTTP POST: " + processActionScripts);
                httpConnection.getOutputStream().write(processActionScripts.getBytes());
                dataOutputStream.flush();
                dataOutputStream.close();

                if (httpConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    // Do normal input or output stream reading
                    Log.v("HTTPResponse", "HTTP response: " + httpConnection.getResponseCode());
                } else {
                    response = "FAILED"; // See documentation for more info on response handling
                }
            } catch (Exception e) {
                //TODO Handle problems..
            }
            Log.v("HTTPResponse", "HTTP response: " + responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }
}
