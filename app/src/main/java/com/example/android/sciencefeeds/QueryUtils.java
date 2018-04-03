package com.example.android.sciencefeeds;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


/**
 * Helper methods related to requesting and receiving science feeds from The Guardian API.
 */
public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String SECTION_NAME = "sectionName";
    private static final String WEB_TITLE = "webTitle";
    private static final String WEB_URL = "webUrl";
    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String TAGS = "tags";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * Return a list of {@link ScienceFeed} objects that has been built up from
     * parsing a JSON response.
     *
     * @param urlString: a String containing the query url for the Guardian API
     */
    public static ArrayList<ScienceFeed> extractScienceFeeds(String urlString) {

        // Create an empty ArrayList that we can start adding scienceFeeds to
        ArrayList<ScienceFeed> scienceFeeds = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            String scienceFeedsString = makeHttpRequest(createUrl(urlString));

            JSONObject baseJsonResponse = new JSONObject(scienceFeedsString);
            JSONArray scienceFeedsJSON = baseJsonResponse.getJSONObject(RESPONSE).getJSONArray(RESULTS);

            for (int i = 0; i < scienceFeedsJSON.length(); i++) {
                // Extract out the elements one by one and create the ArrayList of feeds
                JSONObject scienceFeedJSON = scienceFeedsJSON.getJSONObject(i);

                String sectionName = scienceFeedJSON.getString(SECTION_NAME);
                String webTitle = scienceFeedJSON.getString(WEB_TITLE);
                String webUrl = scienceFeedJSON.getString(WEB_URL);

                // date and author are optional parameter in the JSON response

                // try to get the date, set empty string otherwise
                String datePublished = scienceFeedJSON.optString(WEB_PUBLICATION_DATE);
                if (datePublished == null) datePublished = "";

                // try to get the author name, otherwise set as empty string
                String authorName;
                String authorSurname;
                JSONObject tagsJSONObject = scienceFeedJSON.optJSONArray(TAGS).optJSONObject(0);
                if (tagsJSONObject != null) {
                    authorName = tagsJSONObject.optString(FIRST_NAME);
                    authorSurname = tagsJSONObject.optString(LAST_NAME);

                    // capitalize first letter of name and surname
                    if (authorName.length() > 0)
                        authorName = authorName.substring(0, 1).toUpperCase() + authorName.substring(1);
                    if (authorSurname.length() > 0)
                        authorSurname = authorSurname.substring(0, 1).toUpperCase() + authorSurname.substring(1);
                } else {
                    authorName = "";
                    authorSurname = "";
                }

                scienceFeeds.add(new ScienceFeed(sectionName, webTitle, webUrl, datePublished, authorName, authorSurname));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem IOException", e);
        }

        // Return the list of scienceFeeds
        return scienceFeeds;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == urlConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


}