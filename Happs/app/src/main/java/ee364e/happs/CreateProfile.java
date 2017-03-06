package ee364e.happs;

import android.content.Context;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
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
    private final String URL = "http://teamhapps.herokuapp.com/api/users";
    private Context context;
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
        context = this;
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
           newUser.setName(name.getText().toString());
           newUser.setUserName(username.getText().toString());
           // TODO: check to see if user name is uniquw

           if(isUniqueUserName(newUser.getUserName())) {
               Ion.with(context)
                       .load("POST", URL)
                       .setMultipartParameter("name", newUser.getName())
                       .setMultipartParameter("username", newUser.getUserName())
                       .setMultipartParameter("user_id", newUser.getUserID())
                       .setMultipartParameter("authentication_token", newUser.getAuthToken())
                       .setMultipartFile("datafile", "image/jpeg", null)
                       .asJsonObject()
                       .setCallback(new FutureCallback<JsonObject>() {
                           @Override
                           public void onCompleted(Exception e, JsonObject result) {

                               Intent intent = new Intent(context, MainActivity.class);
                               startActivity(intent);
                               finish();
                               // do stuff with the result or error
                           }
                       });


           }
    }

    public Boolean isUniqueUserName(String userName) {
        //// TODO: query database
        return true;
    }

    public void onClickTakePic(View view){
        //TODO take profile picture
        Toast.makeText(getApplicationContext(),"Click!",Toast.LENGTH_SHORT).show();
    }

}


