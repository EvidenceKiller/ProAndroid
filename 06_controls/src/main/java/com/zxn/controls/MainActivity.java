package com.zxn.controls;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private String[] activities = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();

        activities = res.getStringArray(R.array.list_activities);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.list_titles, android.R.layout.simple_list_item_1);

        this.setListAdapter(adapter);
        ListView listView = getListView();
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            Intent intent = new Intent(this, Class.forName("com.zxn.controls." + activities[i]));
            startActivity(intent);
        } catch (Exception e) {
            Log.e("CommonControls", "Problem with activity list for main menu");
        }
    }
}
