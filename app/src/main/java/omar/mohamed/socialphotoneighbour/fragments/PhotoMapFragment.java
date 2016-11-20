package omar.mohamed.socialphotoneighbour.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.Parameter;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.Response;
import com.googlecode.flickrjandroid.Transport;
import com.googlecode.flickrjandroid.photos.GeoData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import omar.mohamed.socialphotoneighbour.activities.ItemListActivity;
import omar.mohamed.socialphotoneighbour.classes.ImageInfo;
import omar.mohamed.socialphotoneighbour.classes.MyItem;
import omar.mohamed.socialphotoneighbour.services.BackgroundService;


public class PhotoMapFragment extends SupportMapFragment implements LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private SupportMapFragment mMapFragment;
    protected static ArrayList<ImageInfo> actualImagesList;
    private GoogleMap mMapView;
    protected LatLng mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private ClusterManager<MyItem> mClusterManager;
    private Context mContext;
    private GeoData tempGeoDataContainer;
    private Transport transport;
    public static final String METHOD_GET_LOCATION = "flickr.photos.geo.getLocation";
    private final int REQUEST_PERMISSION_LOCATION_FINE = 1;
    public static final String API_KEY = "01bd8e557c0167f56bbc1d82e5e6370e"; //$NON-NLS-1$
    /**
     * Used  at the startup or when the app is paused to guide the user to his current location
     * and provide a smother UX
     */
    private boolean centerLocationIfNeeded = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        actualImagesList = ItemListActivity.closestImagesList;
        mMapFragment = this;
        centerLocationIfNeeded = true;
        mMapFragment.getMapAsync(this);

    }

    private void setUpClusterer() {

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.        // Add cluster items (markers) to the cluster manager.
        if (actualImagesList != null) {
            lookAroundForNewMarkers(actualImagesList);
        } else {
            Toast.makeText(mContext,
                    "No photo found. Please try to move to a different psysical location",
                    Toast.LENGTH_SHORT).show();
        }


        mMapView.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mClusterManager.cluster();
            }
        });

        mMapView.setOnMarkerClickListener(mClusterManager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION_FINE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (!(grantResult == PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION_FINE);
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(mContext, mMapView);

        if (ActivityCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCATION_FINE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        mMapView.setMyLocationEnabled(true);
        mMapView.setBuildingsEnabled(true);
        mMapView.setIndoorEnabled(false);
        mMapView.getUiSettings().setZoomControlsEnabled(true);
        mMapView.getUiSettings().setMyLocationButtonEnabled(true);

        setUpClusterer();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {

            buildGoogleApiClient();
            mGoogleApiClient.connect();

        }

        if (mMapView == null) {
            mMapFragment = this;

            mMapFragment.getMapAsync(this);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(mContext, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(mContext, "onConnected", Toast.LENGTH_SHORT).show();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void addMarker(MyItem myItem){
        mMapView.addMarker(new MarkerOptions()
                .position(myItem.getPosition())
                .title(myItem.getId()));
    }

    public void lookAroundForNewMarkers(final List<ImageInfo> actualImagesList) {
        // put a pin in the map for every image received

        //Verify if there are no image close to the user or the image, in this session,
        //was already found: in both the cases don't add a new Marker

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (ImageInfo imageIndex : actualImagesList) {

                    try {
                        tempGeoDataContainer = getPhotoLocation(imageIndex.getId());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                        
                    Log.d("Test", "Is inside onLocationChanged - ending handling the image"
                            + imageIndex.getTitle());

                }
            }
        }).start();

        if (tempGeoDataContainer != null)
            if(mClusterManager != null) {
                MyItem myItem = new MyItem(
                        new LatLng(tempGeoDataContainer.getLatitude(),
                                tempGeoDataContainer.getLongitude()));
                addMarker(myItem);
                mClusterManager.addItem(myItem);
            }
    }

    @Override
    public void onLocationChanged(Location location) {
        setUpClusterer();

        if(centerLocationIfNeeded) {
            moveCameraToCurrentPosition(location);
            centerLocationIfNeeded = false;

        }
    }

    private void moveCameraToCurrentPosition(Location currentLocation){
        mCurrentLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 16);
        mMapView.animateCamera(cameraUpdate);
    }

    ///Get the geo data (latitude and longitude and the accuracy level) for a photo.
    public GeoData getPhotoLocation(String photoId) throws IOException, FlickrException, JSONException {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("method", METHOD_GET_LOCATION));
        parameters.add(new Parameter("api_key", API_KEY));
        parameters.add(new Parameter("photo_id", photoId));

        try {
            transport = new REST();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Response response = BackgroundService.getModified(transport.getPath(), parameters);
        if (response.isError()) {
            throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
        }
        JSONObject photoElement = response.getData().getJSONObject("photo");
        JSONObject locationElement = photoElement.getJSONObject("location");
        String latStr = locationElement.getString("latitude");
        String lonStr = locationElement.getString("longitude");
        String accStr = locationElement.getString("accuracy");

        return new GeoData(lonStr, latStr, accStr);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}