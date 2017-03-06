package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private LoginButton loginButton;
    private TextView info;
    private CallbackManager callbackManager;
    private String userID;
    private String name;
    private String firstName;
    private String lastName;
    private AccessToken accessToken;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()

                );


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

                                info.append("\n" + name + "\n" + firstName + "\n" + lastName);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,last_name,first_name");
                request.setParameters(parameters);
                request.executeAsync();

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled");
            }

            @Override
            public void onError(FacebookException e) {

                info.setText("Login attempt failed");
                Intent intent = new Intent(null, MainActivity.class);
                startActivity(intent);

            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onClickSkipLogin(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
