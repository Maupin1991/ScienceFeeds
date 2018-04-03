package com.example.android.sciencefeeds;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<ScienceFeed>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String URL_QUERY = "http://content.guardianapis.com/search";
    private static final int SCIENCE_FEED_LOADER_ID = 1;
    private static final String SHOW_TAGS_KEY = "show-tags";
    private static final String SHOW_TAGS_VALUE = "contributor";
    private static final String SECTION_KEY = "section";
    private static final String SECTION_VALUE = "science";
    private static final String ORDER_BY_KEY = "order-by";
    private static final String FROM_DATE_KEY = "from-date";
    private static final String API_KEY_KEY = "api-key";
    private static final String API_KEY_VALUE = "test";


    private ScienceFeedAdapter mAdapter;
    private TextView emptyListView;
    private ProgressBar progressBar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        emptyListView = (TextView) findViewById(R.id.empty_list);
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);

        earthquakeListView.setEmptyView(emptyListView);
        // Create a new adapter that takes an empty list of science feeds as input
        mAdapter = new ScienceFeedAdapter(this, new ArrayList<ScienceFeed>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        // Check connectivity
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            progressBar.setVisibility(View.GONE);
            emptyListView.setText(R.string.no_internet);
        } else {
            getLoaderManager().initLoader(SCIENCE_FEED_LOADER_ID, null, this);
        }
    }


    /**
     * Loader callback
     *
     * @param i      id of the loader
     * @param bundle bundle for the loader
     * @return {@link ScienceFeedLoader} custom loader for science feeds
     */
    @Override
    public Loader<List<ScienceFeed>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(URL_QUERY);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String fromDate = sharedPrefs.getString(
                getString(R.string.start_from_date_key),
                getString(R.string.settings_from_date_default)
        );


        // Append query parameter and its value.


        // this is needed in order to get the author name
        uriBuilder.appendQueryParameter(SHOW_TAGS_KEY, SHOW_TAGS_VALUE);

        // query for science feeds
        uriBuilder.appendQueryParameter(SECTION_KEY, SECTION_VALUE);

        uriBuilder.appendQueryParameter(ORDER_BY_KEY, orderBy);

        uriBuilder.appendQueryParameter(FROM_DATE_KEY, fromDate);

        // API key
        uriBuilder.appendQueryParameter(API_KEY_KEY, API_KEY_VALUE);

        Log.i(LOG_TAG, "Created URI:" + uriBuilder.toString());
        return new ScienceFeedLoader(this, uriBuilder.toString());

    }

    /**
     * Callback for load finished
     *
     * @param loader       loader that has finished the task
     * @param scienceFeeds list of {@link ScienceFeed} objects fetched from the url
     */
    @Override
    public void onLoadFinished(Loader<List<ScienceFeed>> loader, List<ScienceFeed> scienceFeeds) {
        // Clear the adapter of previous science feed data
        mAdapter.clear();
        // If there is a valid list of {@link ScienceFeed}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (scienceFeeds != null && !scienceFeeds.isEmpty()) {
            mAdapter.addAll(scienceFeeds);
        } else {
            emptyListView.setText(R.string.sorry_no_science_feed_found);
        }
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Callback for loader reset
     *
     * @param loader loader that has been reset
     */
    @Override
    public void onLoaderReset(Loader<List<ScienceFeed>> loader) {
        Log.i(LOG_TAG, "Resetting Loader");
        mAdapter.clear();
    }
}

