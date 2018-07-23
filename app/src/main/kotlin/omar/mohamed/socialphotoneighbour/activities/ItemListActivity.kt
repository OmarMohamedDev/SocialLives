package omar.mohamed.socialphotoneighbour.activities

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import java.io.IOException
import java.util.ArrayList

import omar.mohamed.socialphotoneighbour.R
import omar.mohamed.socialphotoneighbour.classes.Callbacks
import omar.mohamed.socialphotoneighbour.classes.ImageInfo
import omar.mohamed.socialphotoneighbour.fragments.ItemDetailFragment
import omar.mohamed.socialphotoneighbour.fragments.ItemListFragment
import omar.mohamed.socialphotoneighbour.services.BackgroundService


/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * [ItemDetailActivity] representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 *
 *
 * The activity makes heavy use of fragments. The list of items is a
 * [ItemListFragment] and the item details (if present) is a
 * [ItemDetailFragment].
 *
 *
 */
class ItemListActivity : AppCompatActivity(), Callbacks, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private var locationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    //
    private var isChecked: Boolean = false
    private var settings: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var mServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        mServiceIntent = Intent(this, BackgroundService::class.java)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B71C1C"))) // set your desired color

        // Restore preferences
        settings = getSharedPreferences(CHECKBOX_VALUE, 0)
        isChecked = true

        // <---- Geo-location --->
        // Create the LocationRequest object
        locationRequest = LocationRequest.create()
        // Use high accuracy
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // Set the update interval to 5 seconds
        locationRequest!!.interval = UPDATE_INTERVAL
        // Set the fastest update interval to 1 second
        locationRequest!!.fastestInterval = FASTEST_INTERVAL

        val mConnectionCallbacks = object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {
                Toast.makeText(baseContext, R.string.connected, Toast.LENGTH_SHORT).show()
            }

            override fun onConnectionSuspended(i: Int) {
                Toast.makeText(baseContext, R.string.connection_suspended, Toast.LENGTH_SHORT).show()
            }
        }

        val mOnConnectionFailedListener = GoogleApiClient.OnConnectionFailedListener { Toast.makeText(baseContext, R.string.connection_failed, Toast.LENGTH_SHORT).show() }

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .build()

        //TODO: Temporary work around to start immediately the gallery, waiting to build drawer for menu e fix map
        try {
            onItemSelected("1")
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()
        // Connect the client.
        mGoogleApiClient!!.connect()
    }

    override fun onStop() {
        mGoogleApiClient!!.disconnect()
        super.onStop()

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        settings = getSharedPreferences(CHECKBOX_VALUE, 0)
        editor = settings!!.edit()
        editor!!.putBoolean("isChecked", isChecked)

        // Commit the edits!
        editor!!.apply()
    }

    override fun onPause() {
        super.onPause()

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        settings = getSharedPreferences(CHECKBOX_VALUE, 0)
        editor = settings!!.edit()
        editor!!.putBoolean("isChecked", isChecked)

        // Commit the edits!
        editor!!.apply()
    }

    override fun onResume() {
        super.onResume()

        // Restore preferences
        settings = getSharedPreferences(CHECKBOX_VALUE, 0)
        isChecked = settings!!.getBoolean("isChecked", true)

    }

    /**
     * Callback method from [omar.mohamed.socialphotoneighbour.classes.Callbacks] indicating that the
     * item with the given ID was selected.
     * @throws IOException Input/Output Exception
     */
    @Throws(IOException::class)
    override fun onItemSelected(id: String) {

        // Start the detail activity for the selected item ID.
        val detailIntent = Intent(this, ItemDetailActivity::class.java)
        detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id)
        startActivity(detailIntent)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val tempMenu: MenuItem
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_menu, menu)
        tempMenu = menu.findItem(R.id.search)
        tempMenu.isChecked = isChecked


        //If the checkbox is checked, start a background service that enable the Image Search


        if (tempMenu.isChecked) {
            //Fixed values just used to inizialize the coordinates
            startService(mServiceIntent)
        }

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Handle checkbox selection


        if (item.itemId == R.id.search) {
            manipulateImageSearch(item)
            return true
        } else
            return super.onOptionsItemSelected(item)


    }

    fun manipulateImageSearch(item: MenuItem) {
        val alert: String

        if (!item.isChecked) {
            alert = "Automatic image search mode: ON"
            isChecked = true

            startService(mServiceIntent)

        } else {
            alert = "Automatic image search mode: OFF"
            isChecked = false

            stopService(mServiceIntent)
        }

        Toast.makeText(applicationContext, alert, Toast.LENGTH_SHORT).show()
        item.isChecked = isChecked
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    override fun onActivityResult(
            requestCode: Int, resultCode: Int, data: Intent?) {
        // Decide what to do based on the original request code
        when (requestCode) {

            CONNECTION_FAILURE_RESOLUTION_REQUEST ->
                /*
          * If the result code is Activity.RESULT_OK, try
          * to connect again
          */
                when (resultCode) {
                    Activity.RESULT_OK -> {
                    }
                }/*
                  * Try the request again
                  */
        }
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        /*
        * Google Play services can resolve some errors it detects.
        * If the error has a resolution, try sending an Intent to
        * start a Google Play services activity that can resolve
        * error.
        */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST)
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (e: IntentSender.SendIntentException) {
                // Log the error
                e.printStackTrace()
            }

        } else {
            /*
            * If no resolution is available, display a dialog to the
            * user with the error.
            */
            Toast.makeText(this, connectionResult.errorCode,
                    Toast.LENGTH_SHORT).show()

        }

    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    override fun onConnected(arg0: Bundle?) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this)
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

    }

    override fun onConnectionSuspended(i: Int) {

    }

    //When the location change, I update the map and the gallery (if possible)
    override fun onLocationChanged(location: Location) {
        // Report to the UI that the location was updated

        currentLocation = location
        //    Toast.makeText(getApplicationContext(), "Latitude: "+currentLocation.getLatitude()+" - Longitude:"+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

        startService(mServiceIntent)


    }

    companion object {

        val CHECKBOX_VALUE = "CheckboxValue"
        //Geolocation variables
        private val MILLISECONDS_PER_SECOND = 1000
        val UPDATE_INTERVAL_IN_SECONDS = 10
        private val UPDATE_INTERVAL = (MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS).toLong()
        private val FASTEST_INTERVAL_IN_SECONDS = 8
        private val FASTEST_INTERVAL = (MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS).toLong()
        var currentLocation: Location? = null
        var closestImagesList: ArrayList<ImageInfo>? = null


        /* <------------------------ Geo-localization ---------------------->*/

        //Global constants
        /*
  * Define a request code to send to Google Play services
  * This code is returned in Activity.onActivityResult
  */
        private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    }


}
