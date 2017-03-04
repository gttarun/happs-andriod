package ee364e.happs;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class OverviewActivity extends AppCompatActivity {
    TextView name;
    TextView placeName;
    TextView address;
    TextView details;
    ImageView picture;
    TextView endTime;
    TextView startTime;
    TextView startDate;
    TextView privateOrPublic;
    TextView invitesEnabled;
    Event event;
    int id = -1;


    File finalFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        name = (TextView) findViewById(R.id.nameOverView);
        placeName = (TextView) findViewById(R.id.placenameOverView);
        address = (TextView) findViewById(R.id.addressOverView);
        details = (TextView) findViewById(R.id.detailsOverView);
        picture = (ImageView) findViewById(R.id.imageViewOverView);
        endTime = (TextView) findViewById(R.id.endTimeOverView);
        startTime = (TextView) findViewById(R.id.startTimeOverView);
        startDate = (TextView) findViewById(R.id.dateOverView);
        privateOrPublic = (TextView) findViewById(R.id.privateOverView);
        invitesEnabled = (TextView) findViewById(R.id.invitesEnableOverView);
        event = EventBus.getDefault().removeStickyEvent(Event.class);

        String names = event.getName();
        name.setText(names);
        placeName.setText(event.getPlaceName());
        address.setText(event.getAddress());
        details.setText(event.getdetails());
        startDate.setText(Integer.toString(event.getStartYear()) + "--" + Integer.toString(event.getStartMonth() + 1) + "--" + Integer.toString(event.getStartDay()));
        if(event.isInvites()) {
            invitesEnabled.setText("Invites enabled");
        } else {
            invitesEnabled.setText("Invites disabled");
        }
        if(event.isPublicEvent()) {
            privateOrPublic.setText("Public event");
        } else {
            privateOrPublic.setText("Private event");
        }
        picture.setImageURI(event.getURI());
        String time = "AM";
        int showHour = event.getStartHour();
        if(showHour >= 12) {
            time = "PM";
        }
        if(showHour > 12) {
            showHour = showHour % 12;
        } else if(showHour == 0) {
            showHour = 12;
        }
        startTime.setText(Integer.toString(showHour) + " : " + Integer.toString(event.getStartMinute()) + " " + time);
        time = "AM";
        showHour = event.getEndHour();
        if(showHour >= 12) {
            time = "PM";
        }
        if(showHour > 12) {
            showHour = showHour % 12;
        } else if(showHour == 0) {
            showHour = 12;
        }
        endTime.setText(Integer.toString(showHour) + " : " +Integer.toString(event.getEndMinute()) + " " + time);
        finalFile = new File(event.getURI().getPath());
    }







    public void EventSubmit(View view) {
        new LongRunningPostIO().execute();
        Intent intent = new Intent(getApplicationContext(), EventLayoutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }


    private class LongRunningPostIO extends AsyncTask<Void, Void, String> {
        private final String URL = "http://teamhapps.herokuapp.com/api/events/";
        String time = "2016-09-19T18:30:00Z";


        protected String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected String doInBackground(Void... params) {
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;
            InputStream is = null;

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
                        jsonParam.put("event_name", event.getName());
                        jsonParam.put("time", "2016-06-29T09:57:00Z");
                        jsonParam.put("longitude", event.getLongitude());
                        jsonParam.put("latitude", event.getLatitude());
                        jsonParam.put("username", "young");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream ());
                    os.writeBytes(jsonParam.toString());
                    os.flush ();
                    os.close ();
                    is = conn.getInputStream();
                    int response = conn.getResponseCode();
                    String message = conn.getResponseMessage();
                    String input=  readIt(is, len);
                    try {
                        JSONObject success = new JSONObject(input);
                        id = Integer.valueOf(success.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
            new LongRunningPostPicture().execute();
        }
    }









    private class LongRunningPostPicture extends AsyncTask<Void, Void, String> {

        private final String URL = "http://teamhapps.herokuapp.com/api/images/";
        private final String boundary = "-------------xxxxx";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        protected String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected String doInBackground(Void... params) {
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;
            try {
                java.net.URL url = null;
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
                    conn.setRequestProperty("Connection", "Keep-Alive");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", event.getName() + id);
                try {
                    conn.connect();
                    FileInputStream fileInputStream = new FileInputStream(finalFile);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream ());
                    os.writeBytes(twoHyphens + boundary + lineEnd);
                    os.writeBytes("Content-Disposition: form-data; name=\"" +  "username" + "\"" + lineEnd);
                    os.writeBytes(lineEnd);
                    os.writeBytes("young" + lineEnd);
                    os.writeBytes(twoHyphens + boundary + lineEnd);
                    os.writeBytes("Content-Disposition: form-data; name=\"" +  "event" + "\"" + lineEnd);
                    os.writeBytes(lineEnd);
                    os.writeBytes("http://teamhapps.herokuapp.com/api/events/" + id +"/" + lineEnd);
                    os.writeBytes(twoHyphens + boundary + lineEnd);
                    os.writeBytes("Content-Disposition: form-data; name=" +  "datafile"  + ";filename=" + event.getName() + id  + ".jpg" +  lineEnd);
                    os.writeBytes("Content-Type: image/jpeg" + lineEnd);
                    os.writeBytes(lineEnd);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0){
                        os.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    os.writeBytes(lineEnd);
                    os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    fileInputStream.close();
                    os.flush ();
                    os.close ();
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
                        String contentAsString = readIt(is, len);
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        protected void onPostExecute(String results) {

        }
    }

















}
