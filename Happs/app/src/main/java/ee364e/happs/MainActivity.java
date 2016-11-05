package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double longitude;
    private double latitude;
    private String result;
    Context context;
    private final String URL = "http://teamhapps.herokuapp.com/api/events/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(AppIndex.API).build();
        }
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        new LongRunningGetIO().execute();

    }

    //this function is what executes when button is pressed
    /*public void toMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("data", result);
        intent.putExtra("long", longitude);
        intent.putExtra("lat", latitude);
        startActivity(intent);
       //new GetAddress().execute();
    }*/

    /*public void toList(View view) {
        Intent intent = new Intent(this, EventLayout.class);
        intent.putExtra("data", result);
        intent.putExtra("long", longitude);
        intent.putExtra("lat", latitude);
        startActivity(intent);
        //new GetAddress().execute();
    }*/

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        new LongRunningGetIO().execute();
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

    @Override
    protected void onResume() {
        super.onResume();
        new LongRunningGetIO().execute();
    }


// Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.


    // Reads an InputStream and converts it to a String.


    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
        protected String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
           /* char[] buffer = new char[len];
            reader.read(buffer);*/
            BufferedReader r = new BufferedReader(reader);
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 10000;

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
            /*if (results != null) {
                result = results;
                TextView textView = (TextView) findViewById(R.id.textView2);
                textView.setText(result);
            }


            Button b = (Button)findViewById(R.id.button2);


            b.setClickable(true);*/
            Intent intent = new Intent(context, EventLayout.class);
            result = results;
            intent.putExtra("data", result);
            startActivity(intent);
        }
    }




   /* private class GetAddress extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> list = null;
            String currentAddress = null;
            try {
                list = geocoder.getFromLocation(latitude, longitude, 1);
                if(list != null && list.size() > 0) {
                    Address address = list.get(0);
                    // sending back first address line and locality
                    currentAddress = address.getAddressLine(0) + ", " + address.getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    StringBuilder address = new StringBuilder();
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                String place = placeLikelihood.getPlace().getName().toString();
                                String place2 = placeLikelihood.getPlace().getAddress().toString();
                                String like = Float.toString(placeLikelihood.getLikelihood());
                        address.append(place + " " + like + " " + place2 +  "\n");
                    }
                    likelyPlaces.release();
                    String result = address.toString();
                    TextView textView = (TextView) findViewById(R.id.textView2);
                    textView.setText(result);
                }
            });
            return currentAddress;
        }



        protected void onPostExecute(String currentAddress) {
            if (currentAddress != null) {
                result = currentAddress;
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(result);
            }


        }
    }*/








}
