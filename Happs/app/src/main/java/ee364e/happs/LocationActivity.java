package ee364e.happs;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    TextView chosenLocation;
    Event event;
    Context context;
    private double longitude;
    private double latitude;
    ArrayList<MyGooglePlaces> placesResult;

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
        placesResult = new ArrayList<MyGooglePlaces>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        event = EventBus.getDefault().removeStickyEvent(Event.class);
        chosenLocation = (TextView) findViewById(R.id.locationResult);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                chosenLocation.setText(place.getName());
                event.setPlace(new MyGooglePlaces(place));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
        new GetAddress().execute();
    }



    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.next) {
                Intent intent = new Intent(this, EventDetailsActivity.class);
                EventBus.getDefault().postSticky(event);
                startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }






    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
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
    }



    private class GetAddress extends AsyncTask<Void, Void, String> {

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
                    for(int i = 0; i < 5 || i > likelyPlaces.getCount() ; i++) {
                        placesResult.add(new MyGooglePlaces(likelyPlaces.get(i).getPlace()));
                    }
                    MyCustomAdapter dataAdapter = new MyCustomAdapter(context,
                            R.layout.place_info, placesResult);
                    ListView listView = (ListView) findViewById(R.id.placesList);
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    listView.setAdapter(dataAdapter);
                    likelyPlaces.release();
                    }
            });
            return currentAddress;
        }
    }


    private class MyCustomAdapter extends ArrayAdapter<MyGooglePlaces> {

        private ArrayList<MyGooglePlaces> placeList;
        private RadioButton mSelectedRB;
        private int mSelectedPosition = -1;


        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<MyGooglePlaces> placeList) {
            super(context, textViewResourceId, placeList);
            this.placeList = new ArrayList<MyGooglePlaces>();
            this.placeList.addAll(placeList);
        }

        private class ViewHolder {
            RadioButton name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.place_info, null);

                holder = new ViewHolder();
                holder.name = (RadioButton) convertView.findViewById(R.id.radio1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        if(position != mSelectedPosition && mSelectedRB != null){
                            mSelectedRB.setChecked(false);
                        }

                        mSelectedPosition = position;
                        mSelectedRB = (RadioButton)v;
                        MyGooglePlaces place = placeList.get(position);
                        chosenLocation.setText(place.getName());
                        event.setPlace(place);
                    }
                });
            }

            if(holder == null) {
                return convertView;
            }

            if(mSelectedPosition != position){
                holder.name.setChecked(false);

            }else{
                holder.name.setChecked(true);
                if(mSelectedRB != null && holder.name != mSelectedRB){
                    mSelectedRB = holder.name;

                }
            }


            MyGooglePlaces place = placeList.get(position);
            holder.name.setText(place.getName());
            return convertView;

        }

    }

}
