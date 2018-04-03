package com.example.android.sciencefeeds;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Class for async downloading of the science feeds. Handles the background thread.
 */
public class ScienceFeedLoader extends AsyncTaskLoader<List<ScienceFeed>> {
    private static final String LOG_TAG = ScienceFeedLoader.class.getSimpleName();
    String mUrl = "";

    public ScienceFeedLoader(Context context, String url) {
        super(context);
        Log.i(LOG_TAG, "Created new ScienceFeedLoader");
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<ScienceFeed> loadInBackground() {
        Log.i(LOG_TAG, "Started loadInBackGround");
        if (mUrl == null) {
            return null;
        }
        List<ScienceFeed> scienceFeeds = QueryUtils.extractScienceFeeds(mUrl);
        return scienceFeeds;
    }
}