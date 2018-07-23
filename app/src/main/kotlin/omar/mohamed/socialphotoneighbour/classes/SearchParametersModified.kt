package omar.mohamed.socialphotoneighbour.classes

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList

import com.googlecode.flickrjandroid.FlickrException
import com.googlecode.flickrjandroid.Parameter
import com.googlecode.flickrjandroid.util.StringUtilities

import java.util.Date
import java.util.Locale

class SearchParametersModified {

    private var groupId: String? = null
    var woeId: String? = null
        private set
    private var media: String? = null
    var contacts: String? = null
        private set
    var tags: Array<String>? = null
        private set
    var tagMode: String? = null
        private set
    var text: String? = null
    var minUploadDate: Date? = null
        private set
    var maxUploadDate: Date? = null
        private set
    var minTakenDate: Date? = null
        private set
    var maxTakenDate: Date? = null
        private set
    var license: String? = null
    private var extras: Set<String>? = null
    var bBox: Array<String>? = null
        private set
    private var accuracy = 0
    var safeSearch: String? = null
        private set
    var latitude: String? = null
    var longitude: String? = null
    var radius = -1.0
    var radiusUnits: String? = null
        private set
    /**
     * Any photo that has been geotagged.
     *
     *
     *
     * Geo queries require some sort of limiting agent in order to prevent
     * the database from crying. This is basically like the check against
     * "parameterless searches" for queries without a geo component.
     *
     *
     *
     * A tag, for instance, is considered a limiting agent as are user
     * defined min_date_taken and min_date_upload parameters
     * If no limiting factor is passed flickr will return only photos
     * added in the last 12 hours
     * (though flickr may extend the limit in the future).
     *
     * @param hasGeo has geo coordinates
     */
    var hasGeo = false
    var userId: String? = null
        private set

    val asParameters: Collection<Parameter>
        get() {
            val parameters = ArrayList<Parameter>()

            val lat = latitude
            if (lat != null) {
                parameters.add(Parameter("lat", lat))
            }

            val lon = longitude
            if (lon != null) {
                parameters.add(Parameter("lon", lon))
            }

            radius = radius
            if (radius > 0) {
                parameters.add(Parameter("radius", radius))
            }

            radiusUnits = radiusUnits
            if (radiusUnits != null) {
                parameters.add(Parameter("radius_units", radiusUnits))
            }

            media = getMedia()
            if (media != null) {
                parameters.add(Parameter("media", media))
            }

            userId = userId
            if (userId != null) {
                parameters.add(Parameter("user_id", userId))
                contacts = contacts
                if (contacts != null) {
                    parameters.add(Parameter("contacts", contacts))
                }
            }

            groupId = groupId
            if (groupId != null) {
                parameters.add(Parameter("group_id", groupId))
            }

            tags = tags
            if (tags != null) {
                parameters.add(Parameter("tags", StringUtilities.join(tags!!, ",")))
            }

            tagMode = tagMode
            if (tagMode != null) {
                parameters.add(Parameter("tag_mode", tagMode))
            }

            val mTags = tags
            if (mTags != null) {
                parameters.add(Parameter("machine_tags", StringUtilities.join(mTags, ",")))
            }

            val mTagMode = tagMode
            if (mTagMode != null) {
                parameters.add(Parameter("machine_tag_mode", mTagMode))
            }

            text = text
            if (text != null) {
                parameters.add(Parameter("text", text))
            }

            minUploadDate = minUploadDate
            if (minUploadDate != null) {
                parameters.add(Parameter("min_upload_date", java.lang.Long.valueOf(minUploadDate!!.time / 1000L)))
            }

            maxUploadDate = maxUploadDate
            if (maxUploadDate != null) {
                parameters.add(Parameter("max_upload_date", java.lang.Long.valueOf(maxUploadDate!!.time / 1000L)))
            }

            minTakenDate = minTakenDate
            if (minTakenDate != null) {
                parameters.add(Parameter("min_taken_date", MYSQL_DATE_FORMATS.get().format(minTakenDate)))
            }

            maxTakenDate = maxTakenDate
            if (maxTakenDate != null) {
                parameters.add(Parameter("max_taken_date", MYSQL_DATE_FORMATS.get().format(maxTakenDate)))
            }

            license = license
            if (license != null) {
                parameters.add(Parameter("license", license))
            }

            bBox = bBox
            if (bBox != null) {
                parameters.add(Parameter("bbox", StringUtilities.join(bBox!!, ",")))
                if (accuracy > 0) {
                    parameters.add(Parameter("accuracy", accuracy.toLong()))
                }
            } else {
                woeId = woeId
                if (woeId != null) {
                    parameters.add(Parameter("woe_id", woeId))
                }
            }

            safeSearch = safeSearch
            if (safeSearch != null) {
                parameters.add(Parameter("safe_search", safeSearch))
            }

            hasGeo = hasGeo
            if (hasGeo) {
                parameters.add(Parameter("has_geo", "true"))
            }

            if (extras != null && !extras!!.isEmpty()) {
                parameters.add(Parameter("extras", StringUtilities.join(extras!!, ",")))
            }

            return parameters
        }

    /**
     * Optional to use, if BBox is set.
     *
     *
     * Defaults to maximum value if not specified.
     *
     * @param accuracy from 1 to 16
     * @see com.googlecode.flickrjandroid.Flickr.ACCURACY_WORLD
     *
     * @see com.googlecode.flickrjandroid.Flickr.ACCURACY_COUNTRY
     *
     * @see com.googlecode.flickrjandroid.Flickr.ACCURACY_REGION
     *
     * @see com.googlecode.flickrjandroid.Flickr.ACCURACY_CITY
     *
     * @see com.googlecode.flickrjandroid.Flickr.ACCURACY_STREET
     */
    fun setAccuracy(accuracy: Int) {
        this.accuracy = accuracy
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
     * @see com.googlecode.flickrjandroid.photos.Extras.ALL_EXTRAS
     *
     * @see com.googlecode.flickrjandroid.photos.Extras.MIN_EXTRAS
     */
    fun setExtras(extras: Set<String>) {
        this.extras = extras
    }

    fun getMedia(): String? {
        return media
    }

    /**
     * Filter results by media type. Possible values are all (default),
     * photos or videos.
     *
     * @param media media type
     */
    @Throws(FlickrException::class)
    fun setMedia(media: String) {
        if (media == "all" ||
                media == "photos" ||
                media == "videos") {
            this.media = media
        } else {
            throw FlickrException("0", "Media type is not valid.")
        }
    }

    companion object {

        private val MYSQL_DATE_FORMATS = object : ThreadLocal<DateFormat>() {
            @Synchronized
            override fun initialValue(): DateFormat {
                return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            }
        }
    }
}
