package ee364e.happs;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class CreateProfile extends AppCompatActivity {
    private Profile newUser;
    private EditText name;
    private EditText username;
    private EditText description;
    private ImageView profilepic;
    private Button submitButton;
    int id = -1;
    File finalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        submitButton =(Button) findViewById(R.id.button7);
        name = (EditText) findViewById(R.id.editText4);
        username = (EditText) findViewById(R.id.editText5);
        description = (EditText) findViewById(R.id.editText6);
        profilepic = (ImageView) findViewById(R.id.imageView4);
        newUser = new Profile();
        Intent intent = getIntent();
        newUser.setUserID(intent.getStringExtra("user_id"));
        newUser.setAuthToken(intent.getStringExtra("authToken"));
        newUser.setProfilePic(Uri.parse("http://digitalstrategies.tuck.dartmouth.edu/wp-content/uploads/2016/09/missing.jpg"));
    }

       public void onClickSubmit(View view) /*throws Exception*/{
           //Toast.makeText(this, "this button works", Toast.LENGTH_SHORT).show();
           /*
           newUser.setName(name.getText().toString());
           newUser.setUserName(username.getText().toString());
           newUser.setDescription(description.getText().toString());
           Toast.makeText(this, newUser.getName() + " " + newUser.getUserName(), Toast.LENGTH_LONG);
            */
           //check with back end if username is unique
          // GetUsersJSONArray getUsersJSONArray = new GetUsersJSONArray();
          // JSONArray userJArray = getUsersJSONArray.doInBackground();
           //Boolean isUnique;

          // isUnique = isUniqueUserName(userJArray, username.getText().toString());
          // finalFile = new File(newUser.getProfilePic().getPath());

           /*
           if(isUnique){
               //TODO send info to backend
               new LongRunningPostIO().execute();
           }
           else{
               Toast.makeText(this, "Sorry Username is taken", Toast.LENGTH_SHORT);
           }
            */

           //TODO Image stuff for class and sending

    }

    public Boolean isUniqueUserName(JSONArray userJsonArray, String userName)  throws Exception{
        int len = userJsonArray.length();
        for(int i = 0; i < len; i++){
            JSONObject userJSON =(JSONObject) userJsonArray.get(i);
            if(userName.equals(userJSON.get("username"))){
                return false;
            }
        }
        return true;
    }

    public void onClickTakePic(View view){
        //TODO take profile picture
        Toast.makeText(getApplicationContext(),"Click!",Toast.LENGTH_SHORT).show();
    }

    private class GetUsersJSONArray extends AsyncTask<String, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... params) {
            String userName = params[0];
            JSONArray usersJSONArray = null;
            usersJSONArray = Database.doGETRequest(Database.allUsersURLString);
            return usersJSONArray;

        }
    }

    private class LongRunningPostIO extends AsyncTask<Void, Void, String> {
        private final String URL = "http://teamhapps.herokuapp.com/api/users/";
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
                        jsonParam.put("name", newUser.getName());
                        jsonParam.put("username", newUser.getUserName());
                        jsonParam.put("user_id", newUser.getUserID());
                        jsonParam.put("authentication_token", newUser.getAuthToken());
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
                        id = Integer.valueOf(success.getString("id")); //todo ask chan about this line
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
            new CreateProfile.LongRunningPostPicture().execute();
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
                conn.setRequestProperty("uploaded_file", newUser.getUserName());
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
                    os.writeBytes("Content-Disposition: form-data; name=" +  "datafile"  + ";filename=" + newUser.getName() + id  + ".jpg" +  lineEnd);
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


