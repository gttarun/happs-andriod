package ee364e.happs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainUserProfileActivity extends AppCompatActivity {

    TextView nameView;
    TextView userNameView;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preferences.getString("username", "");
        String name = preferences.getString("name", "");
        String picture = preferences.getString("picture", "");
        nameView = (TextView) findViewById(R.id.name);
        userNameView = (TextView) findViewById(R.id.details);
        image = (ImageView) findViewById(R.id.picture);
        nameView.setText(name);
        userNameView.setText(username);
        Glide.with(this).load(picture).centerCrop().placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).crossFade().into(image);
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

}
