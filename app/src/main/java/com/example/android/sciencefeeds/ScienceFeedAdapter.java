package com.example.android.sciencefeeds;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ScienceFeedAdapter extends ArrayAdapter<ScienceFeed> {
    private static final String LOG_TAG = ScienceFeedAdapter.class.getSimpleName();


    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context      The current context. Used to inflate the layout file.
     * @param scienceFeeds A List of {@link ScienceFeed} objects to display in a list
     */
    public ScienceFeedAdapter(Activity context, List<ScienceFeed> scienceFeeds) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, scienceFeeds);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link ScienceFeed} object located at this position in the list
        final ScienceFeed currentScienceFeed = getItem(position);

        // Get the author first and last name and create the text for the author text view
        // if no author name is specified, it will leave the text empty
        String authorName = "";
        if (currentScienceFeed.hasAuthorName()) {
            authorName = currentScienceFeed.getAuthorName();
            authorName += " ";
            authorName += currentScienceFeed.getAuthorSurname();
        }

        // Get the date of the feed. If date is not specified, it will leave the text view
        // empty inserting empty strings
        String datePublished = currentScienceFeed.getDatePublished();

        String sectionName = currentScienceFeed.getSectionName();
        String webTitle = currentScienceFeed.getWebTitle();

        TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
        titleTextView.setText(webTitle);

        TextView sectionTextView = (TextView) convertView.findViewById(R.id.section);
        sectionTextView.setText(sectionName);

        // assign default date and time, so that if they aren't specified later they stay empty
        String date = "";
        String time = "";

        // if date is specified in the response JSON object, extracts the time and date.
        // Otherwise, it will leave an empty string for the text views so that the app has some
        // text to set.
        if (datePublished.length() > 0) {
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date dateObj = formatDate.parse(currentScienceFeed.getDatePublished());
                formatDate.applyPattern("LLL dd, yyyy");
                date = formatDate.format(dateObj);
                formatDate.applyPattern("h:mm a");
                time = formatDate.format(dateObj);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error parsing date");
                e.printStackTrace();
            }
        }

        // Find the TextView with view ID date
        TextView dateView = (TextView) convertView.findViewById(R.id.date);
        // Display the date of the current science feed in that TextView
        dateView.setText(date);

        // Find the TextView with view ID time
        TextView timeView = (TextView) convertView.findViewById(R.id.time);
        // Display the time of the current science feed in that TextView
        timeView.setText(time);

        // Add author name. If author name is not specified, remove the text view
        TextView author = (TextView) convertView.findViewById(R.id.author);
        if (authorName.length() > 0) {
            author.setText(authorName);
        } else {
            author.setVisibility(View.GONE);
        }

        // Sets click listener for the item. If pressed, it should open the browser in the
        // user's phone and go to the article web page
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri urlResource = Uri.parse(currentScienceFeed.getWebUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, urlResource);
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    view.getContext().startActivity(intent);
                }
            }
        });

        return convertView;
    }

}