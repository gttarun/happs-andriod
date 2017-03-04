package ee364e.happs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateProfile extends AppCompatActivity {
    private Profile newUser;
    private EditText name;
    private EditText username;
    private EditText description;
    private ImageView profilepic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        name = (EditText) findViewById(R.id.editText4);
        username = (EditText) findViewById(R.id.editText5);
        description = (EditText) findViewById(R.id.editText6);
        //profilepic = (ImageView) findViewById(R.id.imageView4);
    }

       public void onClickSubmit(View view){
           newUser.setName(name.getText().toString());
           newUser.setUserName(username.getText().toString());
           newUser.setDescription(description.getText().toString());
           //TODO need to check with back end if username is unique
           //TODO send info to backend
           //TODO Image stuff for class and sending

    }

    public void onClickTakePic(View view){
        Toast.makeText(getApplicationContext(),"Click!",Toast.LENGTH_SHORT).show();
    }
}
