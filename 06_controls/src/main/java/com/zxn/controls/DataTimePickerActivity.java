package com.zxn.controls;

import android.app.Activity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Formatter;

/**
 * Created by Administrator on 2015/8/13.
 */
public class DataTimePickerActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datatimepicker);

        TextView dateDefault = (TextView) findViewById(R.id.dateDefault);
        TextView timeDefault = (TextView) findViewById(R.id.timeDefault);

        DatePicker dp = (DatePicker) findViewById(R.id.datePicker);

        dateDefault.setText("Date defaulted to " + (dp.getMonth() + 1) + "/" + dp.getDayOfMonth() + "/" + dp.getYear());
        dp.init(2008, 11, 10, null);

        TimePicker tp = (TimePicker) findViewById(R.id.timePicker);

        Formatter timeFormatter = new Formatter();
        timeFormatter.format("Time defaulted to %d : %02d", tp.getCurrentHour(), tp.getCurrentMinute());

        timeDefault.setText(timeFormatter.toString());

        tp.setIs24HourView(true);
        tp.setCurrentHour(new Integer(10));
        tp.setCurrentMinute(new Integer(10));

    }

}
