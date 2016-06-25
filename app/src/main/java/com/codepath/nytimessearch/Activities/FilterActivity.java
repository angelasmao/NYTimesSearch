package com.codepath.nytimessearch.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.SearchFilters;

import org.parceler.Parcels;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    SearchFilters filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        filters = Parcels.unwrap(getIntent().getParcelableExtra("filters"));


        if (filters.getBeginDate() != 0) {
            String begin = "" + filters.getBeginDate();
            Button startbtn = (Button) findViewById(R.id.btnStart);
            startbtn.setText(begin.substring(4, 6) + "/" + begin.substring(6, 8) + "/" + begin.substring(0, 4));

        }

        if (filters.getEndDate() != 0) {
            String end = "" + filters.getEndDate();
            Button endbtn = (Button) findViewById(R.id.btnEnd);
            endbtn.setText(end.substring(4, 6) + "/" + end.substring(6, 8) + "/" + end.substring(0, 4));
        }

        ArrayList<String> news = filters.getNewsDesks();
        for (int i = 0; i < news.size(); i++) {
            if (news.get(i).equalsIgnoreCase("\"Food\"")) {
                CheckBox check = ((CheckBox) findViewById(R.id.cbFood));
                check.setChecked(true);
            }
            else if (news.get(i).equalsIgnoreCase("\"Arts\"")) {
                CheckBox check = ((CheckBox) findViewById(R.id.cbArts));
                check.setChecked(true);
            }
            else if (news.get(i).equalsIgnoreCase("\"Sports\"")) {
                CheckBox check = ((CheckBox) findViewById(R.id.cbSports));
                check.setChecked(true);
            }
            else if (news.get(i).equalsIgnoreCase("\"Fashion\"")) {
                CheckBox check = ((CheckBox) findViewById(R.id.cbFashion));
                check.setChecked(true);
            }
            else if (news.get(i).equalsIgnoreCase("\"Science\"")) {
                CheckBox check = ((CheckBox) findViewById(R.id.cbScience));
                check.setChecked(true);
            }
            else if (news.get(i).equalsIgnoreCase("\"Magazine\"")) {
                CheckBox check = ((CheckBox) findViewById(R.id.cbMagazine));
                check.setChecked(true);
            }
            else {

            }

        }

        Spinner sortSpinner = (Spinner) findViewById(R.id.sSort);
        if (filters.getSort().equalsIgnoreCase("none")) {
            sortSpinner.setSelection(0);
        }
        else if (filters.getSort().equalsIgnoreCase("oldest")) {
            sortSpinner.setSelection((1));
        }
        else if (filters.getSort().equalsIgnoreCase("newest")) {
            sortSpinner.setSelection(2);
        }
        else {
            sortSpinner.setSelection(0);
        }


    }

    public FilterActivity() {
    }

    public void onSave(View view) {
        getSort(); // update spinner
        checkCheckboxes(); //update news desk
        filters.setUpdated(true); //update updated

        Intent data = new Intent();
        data.putExtra("filters", Parcels.wrap(filters)); //pass it back to the search activity
        setResult(RESULT_OK, data);
        finish();
    }

    public void onClickStart(View view) {

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setListener(new DatePickerDialog.OnDateSetListener(){
            // handle the date selected
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Button startbtn = (Button) findViewById(R.id.btnStart);
                startbtn.setText(monthOfYear+1 + "/" + dayOfMonth + "/" + year);

                String month = Integer.toString(monthOfYear+1);
                if (monthOfYear + 1 < 10) {
                    month = "0" + month;
                }
                Log.d("month", month);


                String day = Integer.toString(dayOfMonth);
                if (dayOfMonth < 10) {
                    day = "0" + day;
                }


                String date = Integer.toString(year) + month + day;

                filters.setBeginDate(Integer.parseInt(date));
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void onClickEnd(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setListener(new DatePickerDialog.OnDateSetListener(){
            // handle the date selected
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Button endbtn = (Button) findViewById(R.id.btnEnd);
                endbtn.setText(monthOfYear+1 + "/" + dayOfMonth + "/" + year);

                String month = Integer.toString(monthOfYear+1);
                if (monthOfYear + 1 < 10) {
                    month = "0" + month;
                }
                Log.d("month", month);


                String day = Integer.toString(dayOfMonth);
                if (dayOfMonth < 10) {
                    day = "0" + day;
                }


                String date = Integer.toString(year) + month + day;

                filters.setEndDate(Integer.parseInt(date));

            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void getSort() {
        Spinner sortSpinner = (Spinner) findViewById(R.id.sSort);
        String sort = sortSpinner.getSelectedItem().toString();

        filters.setSort(sort);
    }


    public void checkCheckboxes() {
        boolean foodchecked = ((CheckBox) findViewById(R.id.cbFood)).isChecked();
        if (foodchecked) {
            filters.getNewsDesks().add("\"Food\"");
            filters.getNewsDesks().add("\"Dining\"");
        }
        else {
            filters.getNewsDesks().remove("\"Food\"");
            filters.getNewsDesks().remove("\"Dining\"");
        }

        boolean stchecked = ((CheckBox) findViewById(R.id.cbScience)).isChecked();
        if (stchecked) {
            filters.getNewsDesks().add("\"Science\"");
            filters.getNewsDesks().add("\"Technology\"");
        }
        else {
            filters.getNewsDesks().remove("\"Science\"");
            filters.getNewsDesks().remove("\"Technology\"");
            Log.d("remove", "removed");
        }

        boolean fashionchecked = ((CheckBox) findViewById(R.id.cbFashion)).isChecked();
        if (fashionchecked) {
            filters.getNewsDesks().add("\"Fashion\"");
            filters.getNewsDesks().add("\"Style\"");
        }
        else {
            filters.getNewsDesks().remove("\"Fashion\"");
            filters.getNewsDesks().remove("\"Style\"");
        }

        boolean magchecked = ((CheckBox) findViewById(R.id.cbMagazine)).isChecked();
        if (magchecked) {
            filters.getNewsDesks().add("\"Magazine\"");
        }
        else {
            filters.getNewsDesks().remove("\"Magazine\"");
        }

        boolean artschecked = ((CheckBox) findViewById(R.id.cbArts)).isChecked();
        if (artschecked) {
            filters.getNewsDesks().add("\"Arts\"");
        }
        else {
            filters.getNewsDesks().remove("\"Arts\"");
        }

        boolean sportschecked = ((CheckBox) findViewById(R.id.cbSports)).isChecked();
        if (sportschecked) {
            filters.getNewsDesks().add("\"Sports\"");
        }
        else {
            filters.getNewsDesks().remove("\"Sports\"");
        }

    }
}

