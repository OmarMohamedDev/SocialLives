package omar.mohamed.socialphotoneighbour.classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.Parameter;
import com.googlecode.flickrjandroid.util.StringUtilities;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SearchParametersModified {

    private String groupId;
    private String woeId;
    private String media;
    private String contacts;
    private String[] tags;
    private String tagMode;
    private String text;
    private Date minUploadDate;
    private Date maxUploadDate;
    private Date minTakenDate;
    private Date maxTakenDate;
    private String license;
    private Set<String> extras;
    private String[] bbox;
    private int accuracy = 0;
    private String safeSearch;
    private String latitude;
    private String longitude;
    private double radius = -1;
    private String radiusUnits;
    private boolean hasGeo = false;
    private String userId;

    private static final ThreadLocal<DateFormat> MYSQL_DATE_FORMATS = new ThreadLocal<DateFormat>() {
        protected synchronized DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        }
    };

    public SearchParametersModified() {

    }

    /**
     * Optional to use, if BBox is set.<p>
     * Defaults to maximum value if not specified.
     *
     * @param accuracy from 1 to 16
     * @see com.googlecode.flickrjandroid.Flickr#ACCURACY_WORLD
     * @see com.googlecode.flickrjandroid.Flickr#ACCURACY_COUNTRY
     * @see com.googlecode.flickrjandroid.Flickr#ACCURACY_REGION
     * @see com.googlecode.flickrjandroid.Flickr#ACCURACY_CITY
     * @see com.googlecode.flickrjandroid.Flickr#ACCURACY_STREET
     */
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    private String getGroupId() {
        return groupId;
    }

    /**
     * Any photo that has been geotagged.<p>
     *
     * Geo queries require some sort of limiting agent in order to prevent
     * the database from crying. This is basically like the check against
     * "parameterless searches" for queries without a geo component.<p>
     *
     * A tag, for instance, is considered a limiting agent as are user
     * defined min_date_taken and min_date_upload parameters &emdash;
     * If no limiting factor is passed flickr will return only photos
     * added in the last 12 hours
     * (though flickr may extend the limit in the future).
     *
     * @param hasGeo has geo coordinates
     */
    public void setHasGeo(boolean hasGeo) {
        this.hasGeo = hasGeo;
    }

    public boolean getHasGeo() {
        return hasGeo;
    }

    public String[] getTags() {
        return tags;
    }

    public String getTagMode() {
        return tagMode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getMinUploadDate() {
        return minUploadDate;
    }

    public Date getMaxUploadDate() {
        return maxUploadDate;
    }

    public Date getMinTakenDate() {
        return minTakenDate;
    }

    public Date getMaxTakenDate() {
        return maxTakenDate;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
    /**
     * List of extra information to fetch for each returned
     * record. Currently supported fields are:
     * license, date_upload, date_taken, owner_name,
     * icon_server, original_format, last_update, geo,
     * tags, machine_tags, o_dims, views, media, path_alias,
     * url_sq, url_t, url_s, url_m, url_l, url_o
     *
     * @param extras A set of extra-attributes
     * @see com.googlecode.flickrjandroid.photos.Extras#ALL_EXTRAS
     * @see com.googlecode.flickrjandroid.photos.Extras#MIN_EXTRAS
     */
    public void setExtras(Set<String> extras) {
        this.extras = extras;
    }

    public String[] getBBox() {
        return bbox;
    }

    public String getSafeSearch() {
        return safeSearch;
    }

    public String getWoeId() {
        return woeId;
    }

    public String getMedia() {
        return media;
    }

    /**
     * Filter results by media type. Possible values are all (default),
     * photos or videos.
     *
     * @param media media type
     */
    public void setMedia(String media) throws FlickrException {
        if (media.equals("all") ||
            media.equals("photos") ||
            media.equals("videos")
        ) {
            this.media = media;
        } else {
            throw new FlickrException("0", "Media type is not valid.");
        }
    }

    public String getContacts() {
        return contacts;
    }

    public Collection<Parameter> getAsParameters() {
        List<Parameter> parameters = new ArrayList<>();

        String lat = getLatitude();
        if (lat != null) {
            parameters.add(new Parameter("lat", lat));
        }

        String lon = getLongitude();
        if (lon != null) {
            parameters.add(new Parameter("lon", lon));
        }

        radius = getRadius();
        if (radius > 0) {
            parameters.add(new Parameter("radius", radius));
        }

        radiusUnits = getRadiusUnits();
        if (radiusUnits != null) {
            parameters.add(new Parameter("radius_units", radiusUnits));
        }

        media = getMedia();
        if (media != null) {
            parameters.add(new Parameter("media", media));
        }

        userId = getUserId();
        if (userId != null) {
            parameters.add(new Parameter("user_id", userId));
            contacts = getContacts();
            if (contacts != null) {
                parameters.add(new Parameter("contacts", contacts));
            }
        }

        groupId = getGroupId();
        if (groupId != null) {
            parameters.add(new Parameter("group_id", groupId));
        }

        tags = getTags();
        if (tags != null) {
            parameters.add(new Parameter("tags", StringUtilities.join(tags, ",")));
        }

        tagMode = getTagMode();
        if (tagMode != null) {
            parameters.add(new Parameter("tag_mode", tagMode));
        }

        String[] mTags = tags;
        if (mTags != null) {
        	parameters.add(new Parameter("machine_tags", StringUtilities.join(mTags, ",")));
        }

        String mTagMode = tagMode;
        if (mTagMode != null) {
            parameters.add(new Parameter("machine_tag_mode", mTagMode));
        }

        text = getText();
        if (text != null) {
            parameters.add(new Parameter("text", text));
        }

        minUploadDate = getMinUploadDate();
        if (minUploadDate != null) {
            parameters.add(new Parameter("min_upload_date", Long.valueOf(minUploadDate.getTime() / 1000L)));
        }

        maxUploadDate = getMaxUploadDate();
        if (maxUploadDate != null) {
            parameters.add(new Parameter("max_upload_date", Long.valueOf(maxUploadDate.getTime() / 1000L)));
        }

        minTakenDate = getMinTakenDate();
        if (minTakenDate != null) {
            parameters.add(new Parameter("min_taken_date", MYSQL_DATE_FORMATS.get().format(minTakenDate)));
        }

        maxTakenDate = getMaxTakenDate();
        if (maxTakenDate != null) {
            parameters.add(new Parameter("max_taken_date", MYSQL_DATE_FORMATS.get().format(maxTakenDate)));
        }

        license = getLicense();
        if (license != null) {
            parameters.add(new Parameter("license", license));
        }

        bbox = getBBox();
        if (bbox != null) {
            parameters.add(new Parameter("bbox", StringUtilities.join(bbox, ",")));
            if (accuracy > 0) {
                parameters.add(new Parameter("accuracy", accuracy));
            }
        } else {
            woeId = getWoeId();
            if (woeId != null) {
                parameters.add(new Parameter("woe_id", woeId));
            }
        }

        safeSearch = getSafeSearch();
        if (safeSearch != null) {
            parameters.add(new Parameter("safe_search", safeSearch));
        }

        hasGeo = getHasGeo();
        if (hasGeo) {
            parameters.add(new Parameter("has_geo", "true"));
        }

        if (extras != null && !extras.isEmpty()) {
            parameters.add(new Parameter("extras", StringUtilities.join(extras, ",")));
        }

        return parameters;
    }

    public void setLatitude(String lat) {
        latitude = lat;
    }

    public void setRadius(double r) {
        radius = r;
    }

    public void setLongitude(String lon) {
        longitude = lon;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public double getRadius() {
        return radius;
    }

    public String getRadiusUnits() {
        return radiusUnits;
    }

    public String getUserId() {
        return userId;
    }
}
