package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.*;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private LoginButton loginButton;
   // private TextView info;
    private CallbackManager callbackManager;
    private String userID;
    private String name;
    private String firstName;
    private String lastName;
    private AccessToken accessToken;
    private Context context;
    private Boolean firstTimeLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        // info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);


        if(isLoggedIn()) {
            userID = com.facebook.Profile.getCurrentProfile().getId();
            accessToken = AccessToken.getCurrentAccessToken();
            checkUserProceed();
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               /* info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()

                ); */


                userID = loginResult.getAccessToken().getUserId();
                accessToken = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    name = object.getString("name");
                                    firstName = object.getString("first_name");
                                    lastName = object.getString("last_name");

                                } catch (JSONException e) {
                                    name = "Error";
                                    lastName = "Error";
                                    firstName = "Error";
                                }

                               // info.append("\n" + name + "\n" + firstName + "\n" + lastName);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,last_name,first_name");
                request.setParameters(parameters);
                request.executeAsync();

                /*
                boolean alreadyUser = true;
                try{
                    alreadyUser = Database.isUser(userID);
                }catch (Exception e){
                    Toast.makeText(context,"Error determaining user", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                */





                    // TODO: Take user to profile creation page, then take them to map (the code above)
                checkUserProceed();

            }



            @Override
            public void onCancel() {
                //info.setText("Login attempt canceled");
            }

            @Override
            public void onError(FacebookException e) {

                //info.setText("Login attempt failed");
                Intent intent = new Intent(null, MainActivity.class);
                startActivity(intent);

            }


        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    void checkUserProceed() {
        String searchURL = "https://uthapps-backend.herokuapp.com/api/users/?user_id=" + userID;
        Ion.with(context)
                .load(searchURL)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray results) {
                        if(results.size() == 0) {
                            Intent intent = new Intent(context, CreateProfile.class);
                            intent.putExtra("user_id", userID);
                            intent.putExtra("authToken", accessToken.getToken());
                            startActivity(intent);
                            finish();
                        } else {
                            JsonObject result = results.get(0).getAsJsonObject();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user_id", result.get("user_id").getAsString());
                            editor.putString("authentication_token", result.get("authentication_token").getAsString());
                            editor.putString("picture", result.get("picture").getAsString());
                            editor.putString("username", result.get("username").getAsString());
                            editor.putString("name", result.get("name").getAsString());
                            editor.apply();
                            Intent intent = new Intent(context, EventLayoutActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }



}
