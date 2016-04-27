package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by nopphon on 4/22/16.
 */
public class SearchTrackActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }
}
