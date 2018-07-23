package omar.mohamed.socialphotoneighbour.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.googlecode.flickrjandroid.FlickrException
import com.googlecode.flickrjandroid.Parameter
import com.googlecode.flickrjandroid.REST
import com.googlecode.flickrjandroid.Response
import com.googlecode.flickrjandroid.Transport
import com.googlecode.flickrjandroid.photos.GeoData

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.util.ArrayList

import javax.xml.parsers.ParserConfigurationException

import omar.mohamed.socialphotoneighbour.activities.ItemListActivity
import omar.mohamed.socialphotoneighbour.classes.ImageInfo
import omar.mohamed.socialphotoneighbour.classes.MyItem
import omar.mohamed.socialphotoneighbour.services.BackgroundService


class PhotoMapFragment : SupportMapFragment(), LocationListener, OnMyLocationButtonClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var mMapFragment: SupportMapFragment? = null
    private var mMapView: GoogleMap? = null
    protected lateinit var mCurrentLocation: LatLng
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mClusterManager: ClusterManager<MyItem>? = null
    private var mContext: Context? = null
    private var tempGeoDataContainer: GeoData? = null
    private var transport: Transport? = null
    private val REQUEST_PERMISSION_LOCATION_FINE = 1
    /**
     * Used  at the startup or when the app is paused to guide the user to his current location
     * and provide a smother UX
     */
    private var centerLocationIfNeeded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = getContext()
        actualImagesList = ItemListActivity.closestImagesList
        mMapFragment = this
        centerLocationIfNeeded = true
        mMapFragment!!.getMapAsync(this)

    }

    private fun setUpClusterer() {

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.        // Add cluster items (markers) to the cluster manager.
        if (actualImagesList != null) {
            lookAroundForNewMarkers(actualImagesList!!)
        } else {
            Toast.makeText(mContext,
                    "No photo found. Please try to move to a different psysical location",
                    Toast.LENGTH_SHORT).show()
        }


        mMapView!!.setOnCameraIdleListener { mClusterManager!!.cluster() }

        mMapView!!.setOnMarkerClickListener(mClusterManager)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_LOCATION_FINE) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]

                if (permission == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION_FINE)
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMapView = googleMap
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = ClusterManager(mContext!!, mMapView)

        if (ActivityCompat.checkSelfPermission(mContext!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (getActivity()?.let {
                        ActivityCompat.shouldShowRequestPermissionRationale(it,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                    }!!) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                getActivity()?.let {
                    ActivityCompat.requestPermissions(it,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_PERMISSION_LOCATION_FINE)
                }

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        mMapView!!.isMyLocationEnabled = true
        mMapView!!.isBuildingsEnabled = true
        mMapView!!.isIndoorEnabled = false
        mMapView!!.uiSettings.isZoomControlsEnabled = true
        mMapView!!.uiSettings.isMyLocationButtonEnabled = true

        setUpClusterer()

    }

    override fun onResume() {
        super.onResume()

        if (mGoogleApiClient == null || !mGoogleApiClient!!.isConnected) {

            buildGoogleApiClient()
            mGoogleApiClient!!.connect()

        }

        if (mMapView == null) {
            mMapFragment = this

            mMapFragment!!.getMapAsync(this)
        }

    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        Toast.makeText(mContext, "buildGoogleApiClient", Toast.LENGTH_SHORT).show()
        mGoogleApiClient = GoogleApiClient.Builder(mContext!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onConnected(bundle: Bundle?) {
        Toast.makeText(mContext, "onConnected", Toast.LENGTH_SHORT).show()

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    private fun addMarker(myItem: MyItem) {
        mMapView!!.addMarker(MarkerOptions()
                .position(myItem.position)
                .title(myItem.id))
    }

    fun lookAroundForNewMarkers(actualImagesList: List<ImageInfo>) {
        // put a pin in the map for every image received

        //Verify if there are no image close to the user or the image, in this session,
        //was already found: in both the cases don't add a new Marker

        Thread(Runnable {
            for (imageIndex in actualImagesList) {

                try {
                    tempGeoDataContainer = getPhotoLocation(imageIndex.id)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }

                Log.d("Test", "Is inside onLocationChanged - ending handling the image" + imageIndex.title)

            }
        }).start()

        if (tempGeoDataContainer != null)
            if (mClusterManager != null) {
                val myItem = MyItem(
                        LatLng(tempGeoDataContainer!!.latitude.toDouble(),
                                tempGeoDataContainer!!.longitude.toDouble()))
                addMarker(myItem)
                mClusterManager!!.addItem(myItem)
            }
    }

    override fun onLocationChanged(location: Location) {
        setUpClusterer()

        if (centerLocationIfNeeded) {
            moveCameraToCurrentPosition(location)
            centerLocationIfNeeded = false

        }
    }

    private fun moveCameraToCurrentPosition(currentLocation: Location) {
        mCurrentLocation = LatLng(currentLocation.latitude, currentLocation.longitude)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 16f)
        mMapView!!.animateCamera(cameraUpdate)
    }

    ///Get the geo data (latitude and longitude and the accuracy level) for a photo.
    @Throws(IOException::class, FlickrException::class, JSONException::class)
    fun getPhotoLocation(photoId: String?): GeoData {
        val parameters = ArrayList<Parameter>()
        parameters.add(Parameter("method", METHOD_GET_LOCATION))
        parameters.add(Parameter("api_key", API_KEY))
        parameters.add(Parameter("photo_id", photoId))

        try {
            transport = REST()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }

        val response = BackgroundService.getModified(transport!!.path, parameters)
        if (response.isError) {
            throw FlickrException(response.errorCode, response.errorMessage)
        }
        val photoElement = response.data.getJSONObject("photo")
        val locationElement = photoElement.getJSONObject("location")
        val latStr = locationElement.getString("latitude")
        val lonStr = locationElement.getString("longitude")
        val accStr = locationElement.getString("accuracy")

        return GeoData(lonStr, latStr, accStr)
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    companion object {
        protected var actualImagesList: ArrayList<ImageInfo>? = null
        val METHOD_GET_LOCATION = "flickr.photos.geo.getLocation"
        val API_KEY = "01bd8e557c0167f56bbc1d82e5e6370e" //$NON-NLS-1$
    }
}