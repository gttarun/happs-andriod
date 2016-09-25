package ee364e.happs;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double longitude;
    private double latitude;
    private String result;
    private final String URL = "http://teamhapps.herokuapp.com/api/events/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        super.onCreate(savedInstanceState);
        new LongRunningGetIO().execute();
        setContentView(R.layout.activity_main);
    }

    //this function is what executes when button is pressed
    public void toMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("data", result);
        intent.putExtra("long", longitude);
        intent.putExtra("lat", latitude);
        startActivity(intent);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude =mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        return;
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        return;
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.


    // Reads an InputStream and converts it to a String.


    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
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
                    conn.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setDoInput(true);
                // Starts the query
                try {
                    conn.connect();
                    int response = conn.getResponseCode();
                    is = conn.getInputStream();
                    String contentAsString = readIt(is, len);
                    return contentAsString;
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
            if (results != null) {
                result = results;
                TextView textView = (TextView) findViewById(R.id.textView2);
                textView.setText(result);

            }


            Button b = (Button)findViewById(R.id.button2);


            b.setClickable(true);
        }
    }


}
