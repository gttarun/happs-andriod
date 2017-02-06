package ee364e.happs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

public class EventActivity extends AppCompatActivity {

    Event event;
    TextView eventName;
    TextView userName;
    ImageView eventImage;
    ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        event = EventBus.getDefault().removeStickyEvent(Event.class);
        eventName = (TextView) findViewById(R.id.eventName);
        userName = (TextView) findViewById(R.id.userName);
        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventName.setText(event.getName());
        userName.setText("Created by " + event.getUsername());
        imageLoader.displayImage("https://scontent-dft4-2.xx.fbcdn.net/v/t1.0-1/p240x240/16406708_221242205005925_2427821695388626742_n.jpg?oh=246493437d8a886c0e94673456c5cf6e&oe=5902F043", eventImage);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
