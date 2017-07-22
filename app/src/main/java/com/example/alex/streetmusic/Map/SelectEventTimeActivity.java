package com.example.alex.streetmusic.Map;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SelectEventTimeActivity extends AppCompatActivity {

    private String locationKey;
    private ArrayList<String> listOfStartTimeSlots;
    private ArrayList<String> listOfEndTimeSlots;
    private String currentDateString;
    private DatabaseReference locationReference;
    private ValueEventListener locationTimeListener;
    private static final String LOG_TAG = SelectEventTimeActivity.class.getSimpleName();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("hhmm");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm ");
    private boolean[] isSelectedTimeFromDatabase = {true, false, false, false, false};
    private boolean[] isSelectedTimeByUser = {false, false, false, false, false};
    private ColorStateList defaultTextViewColor;
    private int countTheTimeSelected = 0;
    private TextView howMuchTimeSelected;
    private String startTime;
    private String endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event_time);
        locationKey = getIntent().getStringExtra(Constants.INTENT_KEY_LOCATION_PUSH_ID);
        final LinearLayout linearLayoutForTimeSlots = (LinearLayout) findViewById(R.id.linearlayout_for_timeslots);
        LinearLayout linearLayoutForTtimePickers = (LinearLayout) findViewById(R.id.linearlayout_for_timepickers);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_select_event_time);

        howMuchTimeSelected = (TextView) findViewById(R.id.textView_how_much_time_selected);

        defaultTextViewColor = new TextView(this).getTextColors();

        //get the current date
        currentDateString = DateFormat.getDateInstance().format(new Date());
        Toast.makeText(this, currentDateString, Toast.LENGTH_SHORT).show();

        listOfStartTimeSlots = new ArrayList<String>();
        listOfStartTimeSlots.add("0800");
        listOfStartTimeSlots.add("0830");
        listOfStartTimeSlots.add("0900");
        listOfStartTimeSlots.add("0930");
        listOfStartTimeSlots.add("1000");

        listOfEndTimeSlots = new ArrayList<String>();
        listOfEndTimeSlots.add("0830");
        listOfEndTimeSlots.add("0900");
        listOfEndTimeSlots.add("0930");
        listOfEndTimeSlots.add("1000");
        listOfEndTimeSlots.add("1030");

        TimePicker startPicker = new TimePicker(new ContextThemeWrapper(this,
                android.R.style.Theme_Holo_Light_DarkActionBar));
        //Register a listener on start time picker so it can save the selected start in a global variable
        startPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                startTime = getBackFormatedSelectedTime(timePicker.getHour(), timePicker.getMinute());
            }
        });
        showRequiredMinuteIntervals(startPicker);
        startPicker.setCurrentHour(8);
        startPicker.setCurrentMinute(0);
        startPicker.setIs24HourView(true);


        TimePicker endPicker = new TimePicker(new ContextThemeWrapper(this,
                android.R.style.Theme_Holo_Light_DarkActionBar));
        endPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                endTime = getBackFormatedSelectedTime(timePicker.getHour(), timePicker.getMinute());
            }
        });
        showRequiredMinuteIntervals(endPicker);
        endPicker.setCurrentHour(10);
        endPicker.setCurrentMinute(30);
        endPicker.setIs24HourView(true);

        linearLayoutForTtimePickers.addView(startPicker);
        linearLayoutForTtimePickers.addView(endPicker);

        locationReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_LOCATIONS)
                .child(locationKey).child(currentDateString);

        locationTimeListener = locationReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Log.d("Igothere", "true");
                    //here we need to keep in mind that the two users can be selecting times at the same time
                    //which means that when the first user elects time and saves the event, the other user needs
                    //to see updated information when he is looking at the time picking window


                }else{
                    Log.d("Igothere", "false");

                    for (int i=0; i<listOfStartTimeSlots.size(); i++){
                        addTextViews(linearLayoutForTimeSlots, i);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void ChooseSelectedTime (View view) throws ParseException {
        checkIfTimeIsFree(startTime, endTime);
    }

    private String getBackFormatedSelectedTime(int hour, int minute) {
        Log.d(LOG_TAG, "Hour " + String.valueOf(hour) + "Min" + String.valueOf(minute));
        String hourMark = String.valueOf(hour).length() == 1 ? "0" + String.valueOf(hour) : String.valueOf(hour);
        String minuteMark = String.valueOf(minute).equals("1") ? "30" : "00";
        String formatedTime = hourMark + minuteMark;

        return formatedTime;

    }

    //So that timepickers show required 30 min. time intervals for minute spinner
    private void showRequiredMinuteIntervals(TimePicker picker) {
        try {
            NumberPicker minuteSpinner = (NumberPicker) picker.findViewById(Class.
                    forName("com.android.internal.R$id").getField("minute").getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / 30) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += 30) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues
                    .toArray(new String[displayedValues.size()]));

        } catch (Exception e) {

        }


    }

    //method to add textView programatically, which will be shown as time slots user can pick for the event
    private void addTextViews(LinearLayout linearLayout, int i) {

        final TextView textView = new TextView(this);
        String startTime = listOfStartTimeSlots.get(i);
        Log.d(LOG_TAG, "addTextViews: start time" + startTime);
        String endTime = listOfEndTimeSlots.get(i);
        Log.d(LOG_TAG, "addTextViews: end time" + startTime);
        String startPlusEndTime = null;
        try {
            Date startDate = dateFormat.parse(startTime);
            startTime = dateFormat2.format(startDate);
            Date endDate = dateFormat.parse(endTime);
            endTime = dateFormat2.format(endDate);
            startPlusEndTime = startTime + " - " + endTime;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        textView.setText(startPlusEndTime);
        textView.setId(i);
        //if time is reserved, set textview color to RED
        if (isSelectedTimeFromDatabase[i]){
            textView.setTextColor(Color.RED);
        }else{
            //else set it to green
            textView.setTextColor(Color.GREEN);
        }
        linearLayout.addView(textView);
    }

    private void checkIfTimeIsFree(String selectedStartTimeSlot, String selectedEndTimeSlot) throws ParseException {

        for (int i = 0; i < isSelectedTimeFromDatabase.length; i++){
            if (isSelectedTimeFromDatabase[i]){
                String reservedStartTimeSlot = listOfStartTimeSlots.get(i);
                String reservedEndTimeSlot = listOfEndTimeSlots.get(i);
                Log.d(LOG_TAG, reservedStartTimeSlot + " - " + selectedStartTimeSlot);
                Log.d(LOG_TAG, reservedEndTimeSlot + " - " + selectedEndTimeSlot);
                if (reservedStartTimeSlot.equals(selectedStartTimeSlot)){

                    Toast.makeText(this, "This time is Reserved", Toast.LENGTH_SHORT).show();
                }else if(reservedEndTimeSlot.equals(selectedEndTimeSlot)){

                    Toast.makeText(this, "This time is Reserved", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Time is Free Like a Bird", Toast.LENGTH_SHORT).show();
                }


//                Date initialTimeDate = dateFormat.parse(listOfTimeSlots.get(i));
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(initialTimeDate);
//                long t = calendar.getTimeInMillis();
//                Date afterAdding30Min = new Date(t + (30 * 60000));

            }
        }
    }


}
