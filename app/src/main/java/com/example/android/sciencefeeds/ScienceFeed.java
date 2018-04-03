package com.example.android.sciencefeeds;

/**
 * ScienceFeed class. This contains a science feed article fetched from [The Guardian API](http://open-platform.theguardian.com/)
 */
public class ScienceFeed {

    private String mSectionName = "";
    private String mWebTitle = "";
    private String mWebUrl = "";
    private String mDatePublished = "";
    private String mAuthorName = "";
    private String mAuthorSurname = "";

    /**
     * Constructor for ScienceFeed class.
     *
     * @param sectionName   name of the section the feed belongs to
     * @param webTitle      title of the article
     * @param webUrl        usl of the science feed
     * @param datePublished date of the publication. If not specified, set as empty string
     * @param authorName    first name of the author, if not specified set as empty string
     * @param authorSurname last name of the author, if not specified set as empty string
     */
    public ScienceFeed(String sectionName, String webTitle, String webUrl, String datePublished, String authorName, String authorSurname) {
        mSectionName = sectionName;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
        mDatePublished = datePublished;
        mAuthorName = authorName;
        mAuthorSurname = authorSurname;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getWebTitle() {
        return mWebTitle;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public String getDatePublished() {
        return mDatePublished;
    }

    public String getAuthorSurname() {
        return mAuthorSurname;
    }

    public boolean hasAuthorName() {
        return (mAuthorName.length() > 0 || mAuthorSurname.length() > 0);
    }
}

