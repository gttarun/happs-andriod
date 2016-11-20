package ee364e.happs;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DefineEvent extends AppCompatActivity {
    String latitude;
    String longitude;
    String eventname;
    String time = "2016-09-19T18:30:00Z";
    private final String URL = "http://teamhapps.herokuapp.com/api/events/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_event);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();

    }

    public void submitEvent(View view) {
        EditText EeventName = (EditText) findViewById(R.id.editText2);
        EditText Elongitude = (EditText) findViewById(R.id.editText4);
        EditText Elatitude = (EditText) findViewById(R.id.editText3);
        eventname = EeventName.getText().toString();
        latitude = Elatitude.getText().toString();
        longitude = Elongitude.getText().toString();

        new LongRunningPostIO().execute();
    }


    private class LongRunningPostIO extends AsyncTask<Void, Void, String> {
        protected String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected String doInBackground(Void... params) {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = null;
                try {
                    url = new URL(URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);


                try {
                    conn.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json");
                // Starts the query
                try {
                    conn.connect();
                    JSONObject jsonParam = new JSONObject();
                    try {
                        jsonParam.put("event_name", eventname);
                        jsonParam.put("time", "2016-06-29T09:57:00Z");
                        jsonParam.put("longitude", longitude);
                        jsonParam.put("latitude", latitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream ());
                    os.writeBytes(jsonParam.toString());
                    os.flush ();
                    os.close ();
                    int response = conn.getResponseCode();
                    return "success";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Convert the InputStream into a string


                return "retrieval failed";

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }




        protected void onPostExecute(String results) {
            finish();
        }
    }


}
