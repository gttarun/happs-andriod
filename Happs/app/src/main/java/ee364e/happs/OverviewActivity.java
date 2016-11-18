package ee364e.happs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

public class OverviewActivity extends AppCompatActivity {
    TextView name;
    TextView placeName;
    TextView address;
    TextView details;
    ImageView picture;
    Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        name = (TextView) findViewById(R.id.nameOverView);
        placeName = (TextView) findViewById(R.id.placenameOverView);
        address = (TextView) findViewById(R.id.addressOverView);
        details = (TextView) findViewById(R.id.detailsOverView);
        picture = (ImageView) findViewById(R.id.imageViewOverView);
        event = EventBus.getDefault().removeStickyEvent(Event.class);

        String names = event.getName();
        name.setText(names);
        placeName.setText(event.getPlaceName());
        address.setText(event.getAddress());
        details.setText(event.getdetails());
        picture.setImageURI(event.getURI());


    }
}
