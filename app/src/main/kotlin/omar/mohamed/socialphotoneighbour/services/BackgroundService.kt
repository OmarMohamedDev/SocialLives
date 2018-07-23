package omar.mohamed.socialphotoneighbour.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.util.Log

import com.googlecode.flickrjandroid.FlickrException
import com.googlecode.flickrjandroid.Parameter
import com.googlecode.flickrjandroid.REST
import com.googlecode.flickrjandroid.RESTResponse
import com.googlecode.flickrjandroid.Response
import com.googlecode.flickrjandroid.Transport
import com.googlecode.flickrjandroid.oauth.OAuthUtils
import com.googlecode.flickrjandroid.people.User
import com.googlecode.flickrjandroid.photos.GeoData
import com.googlecode.flickrjandroid.photos.Photo
import com.googlecode.flickrjandroid.photos.PhotoList
import com.googlecode.flickrjandroid.photos.PhotoUtils
import com.googlecode.flickrjandroid.util.IOUtilities
import com.googlecode.flickrjandroid.util.UrlUtilities

import org.json.JSONException

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.Date
import java.util.HashSet

import javax.xml.parsers.ParserConfigurationException

import omar.mohamed.socialphotoneighbour.classes.ImageInfo
import omar.mohamed.socialphotoneighbour.activities.ItemListActivity
import omar.mohamed.socialphotoneighbour.R
import omar.mohamed.socialphotoneighbour.classes.SearchParametersModified

class BackgroundService : IntentService("ReminderService") {
    private var mContext: Context? = null
    private var IMAGE_NOT_FOUND: String? = null

    override fun onCreate() {
        super.onCreate()

        mContext = applicationContext

        if (mContext != null) {
            IMAGE_NOT_FOUND = mContext!!.resources.getString(R.string.images_not_found)
        }
    }


    override fun onHandleIntent(workIntent: Intent?) {
        val actualLatitude: String
        val actualLongitude: String

        if (ItemListActivity.currentLocation != null) {
            actualLatitude = ItemListActivity.currentLocation!!.latitude.toString()
            actualLongitude = ItemListActivity.currentLocation!!.longitude.toString()
        } else {
            actualLatitude = 0.toString()
            actualLongitude = 0.toString()
        }

        val extras = HashSet<String>()
        extras.add("description") //$NON-NLS-1$
        extras.add("date_taken") //$NON-NLS-1$
        extras.add("date_upload") //$NON-NLS-1$
        extras.add("owner_name") //$NON-NLS-1$
        extras.add("url_o") //$NON-NLS-1$
        val searchParam = SearchParametersModified()
        searchParam.radius = SEARCH_RADIUS //the radius units are as default: km
        searchParam.setExtras(extras)
        searchParam.hasGeo = true
        searchParam.latitude = actualLatitude
        searchParam.longitude = actualLongitude
        try {
            searchParam.setMedia("photos")
        } catch (e1: FlickrException) {
            e1.printStackTrace()
        }

        searchParam.setAccuracy(11)

        //Method that look for the flickr's images closest to the user
        //N.B. I'am not using the official library method to get the images
        //from flickr due to a Flickr update problem that changed the communication protocol
        //few days ago. To get more info about it: http://bit.ly/1kuYWfl

        try {
            photos = imageSearch(searchParam, PHOTOS_PER_TIME_EVERY_SEARCH, 1)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        resultList = ArrayList()
        //If not are photos around the user
        if (photos == null) {
            //Instantiate a new PhotoList element
            photos = PhotoList()
            //Add temporary image while the user waiting to reach a position with photos close to him
            //Factitious user and Geodata created just to fit the photo class structure
            //when not are any photos
            val nobody = User()
            nobody.username = mContext!!.resources.getString(R.string.any)
            val nowhere = GeoData()
            nowhere.latitude = 0f
            nowhere.longitude = 0f

            val temp = Photo()
            temp.title = IMAGE_NOT_FOUND
            temp.url = mContext!!.resources.getString(R.string.url_images_not_found)
            temp.description = IMAGE_NOT_FOUND
            temp.dateTaken = Date()
            temp.datePosted = Date()
            temp.owner = nobody
            temp.geoData = nowhere

            photos!!.add(temp)
        }

        for (photo in photos!!) {

            try {
                resultList.add(ImageInfo(photo.id, photo.title, photo.mediumUrl, photo.description,
                        photo.dateTaken, photo.datePosted, photo.owner.username))
            } catch (e: Exception) {
                Log.d("print", "Problem about geodata.")
            }

        }

        ItemListActivity.closestImagesList = resultList

        val lbm = LocalBroadcastManager.getInstance(this)
        val i = Intent("TAG_REFRESH")
        lbm.sendBroadcast(i)
    }

    @Throws(IOException::class, FlickrException::class, JSONException::class, ParserConfigurationException::class)
    private fun imageSearch(searchParam: SearchParametersModified, perPage: Int, page: Int): MutableList<Photo> {
        val parameters = ArrayList<Parameter>()
        parameters.add(Parameter("method", METHOD_SEARCH))
        parameters.add(Parameter("api_key", API_KEY))
        parameters.addAll(searchParam.asParameters)
        if (perPage > 0) {
            parameters.add(Parameter("per_page", "" + perPage))
        }
        if (page > 0) {
            parameters.add(Parameter("page", "" + page))
        }
        val transport = REST()
        val response = getModified(transport.path, parameters)
        if (response.isError) {
            throw FlickrException(response.errorCode, response.errorMessage)
        }

        return PhotoUtils.createPhotoList(response.data) //RETURN FINALE
    }

    companion object {

        val PHOTOS_PER_TIME_EVERY_SEARCH = 500
        val SEARCH_RADIUS = 0.2
        val METHOD_SEARCH = "flickr.photos.search"
        protected var photos: MutableList<Photo>? = null
        protected lateinit var resultList: ArrayList<ImageInfo>
        val API_KEY = "01bd8e557c0167f56bbc1d82e5e6370e" //$NON-NLS-1$

        @Throws(IOException::class, JSONException::class)
        fun getModified(path: String, parameters: MutableList<Parameter>): Response {
            parameters.add(Parameter("nojsoncallback", "1"))
            parameters.add(Parameter("format", "json"))
            val data = getLineModified(path, parameters)
            return RESTResponse(data)

        }

        @Throws(IOException::class)
        private fun getLineModified(path: String, parameters: List<Parameter>): String {
            var `in`: InputStream? = null
            var rd: BufferedReader? = null
            try {
                `in` = getInputStreamModified(path, parameters)
                rd = BufferedReader(InputStreamReader(`in`, OAuthUtils.ENC))
                val buf = StringBuilder()
                var line = rd.readLine()
                while (line != null) {
                    buf.append(line)
                    line = rd.readLine()
                }
                return buf.toString()
            } finally {
                IOUtilities.close(`in`)
                IOUtilities.close(rd)
            }
        }

        @Throws(IOException::class)
        private fun getInputStreamModified(path: String, parameters: List<Parameter>): InputStream {
            var url = UrlUtilities.buildUrl("www.flickr.com", 80, path, parameters)
            //Operation that permit, finally, to connect to the new Flickr communication protocol
            //(Before: Http, After: Https)
            val urlDaModificare = url.toString()
            val urlModificato = urlDaModificare.replace("http:", "https:")
            url = URL(urlModificato)
            //
            val conn = url.openConnection() as HttpURLConnection
            conn.addRequestProperty("Cache-Control", "no-cache,max-age=0")
            conn.addRequestProperty("Pragma", "no-cache")
            conn.requestMethod = "GET"
            conn.connect()
            return conn.inputStream
        }
    }


}
