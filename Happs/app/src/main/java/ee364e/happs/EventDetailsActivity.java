package ee364e.happs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

public class EventDetailsActivity extends AppCompatActivity {
    EditText details;
    EditText name;
    EditText date;
    EditText startTime;
    EditText endTime;
    Switch publicEvent;
    CheckBox enableInvites;
    Event event;
    int startYear;
    int startMonth;
    int startDay;
    int startHour;
    int startMinute;
    int endHour;
    int endMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        event = EventBus.getDefault().removeStickyEvent(Event.class);
        Calendar c = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        startDay = c.get(Calendar.DAY_OF_MONTH);
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMinute = c.get(Calendar.MINUTE);
        endHour = startHour;
        endMinute = startMinute;
        name = (EditText) findViewById(R.id.eventName);
        details = (EditText) findViewById(R.id.event_details);
        date = (EditText) findViewById(R.id.EventDate);
        startTime = (EditText) findViewById(R.id.EventStartTime);
        endTime = (EditText) findViewById(R.id.EventEndTime);
        publicEvent = (Switch) findViewById(R.id.private_or_public);
        enableInvites = (CheckBox) findViewById(R.id.invites);

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
            Intent intent = new Intent(this, OverviewActivity.class);
            event.setName(name.getText().toString());
            event.setdetails(details.getText().toString());
            event.setDate(startYear, startMonth, startDay, startHour, startMinute, endHour, endMinute);
            event.setPublic(publicEvent.isChecked());
            event.setInvites(enableInvites.isChecked());
            EventBus.getDefault().postSticky(event);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }


    public void showStartDateDialog(View v) {
        DialogFragment dialogFragment = new StartDatePicker();
        dialogFragment.show( getFragmentManager() , "start_date_picker");
    }

    public void showStartTimeDialog(View v) {
        DialogFragment dialogFragment = new StartTimePicker();
        dialogFragment.show( getFragmentManager() , "start_time_picker");
    }


    public void showEndTimeDialog(View v) {
        DialogFragment dialogFragment = new EndTimePicker();
        dialogFragment.show( getFragmentManager() , "end_time_picker");
    }

    public void privateToggle(View v) {
        if(publicEvent.isChecked()){
            publicEvent.setText("Public");
        } else {
            publicEvent.setText("Private");
        }
    }


     public class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(EventDetailsActivity.this, this, startYear, startMonth, startDay);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startYear = year;
            startMonth = monthOfYear;
            startDay = dayOfMonth;
            updateStartDateDisplay();
        }
        public void updateStartDateDisplay() {
            date.setText(Integer.toString(startYear) + "--" + Integer.toString(startMonth + 1) + "--" + Integer.toString(startDay));
        }
    }

    public class StartTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new TimePickerDialog(EventDetailsActivity.this, this, startHour, startMinute, false);
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            startHour = hour;
            startMinute = minute;
            endHour = startHour;
            endMinute = startMinute;
            updateStartTimeDisplay();
        }
        public void updateStartTimeDisplay() {
            String time = "AM";
            int showHour = startHour;
            if(startHour >= 12) {
                time = "PM";
            }
            if(startHour > 12) {
                showHour = startHour % 12;
            }
            if(startHour == 0) {
                showHour = 12;
            }
            startTime.setText(Integer.toString(showHour) + " : " + Integer.toString(startMinute) + " " + time);
        }
    }

    public class EndTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return  new TimePickerDialog(EventDetailsActivity.this, this, endHour, endMinute, false);
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            endHour = hour;
            endMinute = minute;
            updateEndTimeDisplay();
        }
        public void updateEndTimeDisplay() {
            String time = "AM";
            int showHour = endHour;
            if(endHour >= 12) {
                time = "PM";
            }
            if(endHour > 12) {
                showHour = endHour % 12;
            }
            if(endHour == 0) {
                showHour = 12;
            }
            endTime.setText(Integer.toString(showHour) + " : " + Integer.toString(endMinute) + " " + time );
        }
    }


}
