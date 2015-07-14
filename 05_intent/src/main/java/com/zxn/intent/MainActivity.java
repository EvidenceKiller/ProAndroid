package com.zxn.intent;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private final static String tag = "MainActivity";

    // Initialize this in onCreateOptions
    Menu myMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setupButton();
        this.setupEditText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        this.myMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            handleMenus(item);
        } catch (Throwable t) {
            Log.d(tag, t.getMessage(), t);
            throw new RuntimeException("error", t);
        }
        return true;
    }

    private void handleMenus(MenuItem item) {
        this.appendMenuItemText(item);
        if(item.getItemId() == R.id.menu_clear) {
            this.emptyText();
        } else if(item.getItemId() == R.id.menu_basic_view) {
            IntentsUtils.invokeBasicActivity(this);
        }
    }
}
