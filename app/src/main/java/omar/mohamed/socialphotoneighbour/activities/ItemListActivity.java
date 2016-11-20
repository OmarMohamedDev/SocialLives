package omar.mohamed.socialphotoneighbour.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;

import omar.mohamed.socialphotoneighbour.classes.ImageInfo;
import omar.mohamed.socialphotoneighbour.fragments.ItemDetailFragment;
import omar.mohamed.socialphotoneighbour.fragments.ItemListFragment;
import omar.mohamed.socialphotoneighbour.R;
import omar.mohamed.socialphotoneighbour.services.BackgroundService;


/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragment}.
 * <p>
 */
public class ItemListActivity extends AppCompatActivity implements
        ItemListFragment.Callbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1;
    private static boolean firstTime = true;
    public static final String CHECKBOX_VALUE = "CheckboxValue";
    //Geolocation variables
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 8;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private LocationRequest locationRequest;
    public static Location currentLocation;
    private GoogleApiClient mGoogleApiClient;
    //
    private boolean isChecked;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    public static ArrayList<ImageInfo> closestImagesList;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        mServiceIntent = new Intent(this, BackgroundService.class);

        // Restore preferences
        settings = getSharedPreferences(CHECKBOX_VALUE, 0);
        isChecked = true;

        // <---- Geo-location --->
        // Create the LocationRequest object
        locationRequest = LocationRequest.create();
        // Use high accuracy
        locationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        locationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Toast.makeText(getBaseContext(), R.string.connected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectionSuspended(int i) {
                Toast.makeText(getBaseContext(), R.string.connection_suspended, Toast.LENGTH_SHORT).show();
            }
        };

        GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(getBaseContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .build();


    }

    protected boolean isPlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        settings = getSharedPreferences(CHECKBOX_VALUE, 0);
        editor = settings.edit();
        editor.putBoolean("isChecked", isChecked);

        // Commit the edits!
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        settings = getSharedPreferences(CHECKBOX_VALUE, 0);
        editor = settings.edit();
        editor.putBoolean("isChecked", isChecked);

        // Commit the edits!
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restore preferences
        settings = getSharedPreferences(CHECKBOX_VALUE, 0);
        isChecked = settings.getBoolean("isChecked", true);

    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks} indicating that the
     * item with the given ID was selected.
     * @throws IOException Input/Output Exception
     */
    @Override
    public void onItemSelected(String id) throws IOException {
        //To give to the background service the time to find the photos
        if (firstTime) {
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            firstTime = false;
        }

        // Start the detail activity for the selected item ID.
        Intent detailIntent = new Intent(this, ItemDetailActivity.class);
        detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
        startActivity(detailIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem tempMenu;
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        tempMenu = menu.findItem(R.id.search);
        tempMenu.setChecked(isChecked);


        //If the checkbox is checked, start a background service that enable the Image Search


        if (tempMenu.isChecked()) {
            //Fixed values just used to inizialize the coordinates
            startService(mServiceIntent);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle checkbox selection


        if (item.getItemId() == R.id.search) {
            manipulateImageSearch(item);
            return true;
        } else
            return super.onOptionsItemSelected(item);


    }

    public void manipulateImageSearch(MenuItem item) {
        String alert;

        if (!item.isChecked()) {
            alert = "Automatic image search mode: ON";
            isChecked = true;

            startService(mServiceIntent);

        } else {
            alert = "Automatic image search mode: OFF";
            isChecked = false;

            stopService(mServiceIntent);
        }

        Toast.makeText(getApplicationContext(), alert, Toast.LENGTH_SHORT).show();
        item.setChecked(isChecked);
    }
  
 
/* <------------------------ Geo-localization ---------------------->*/

    //Global constants
 /*
  * Define a request code to send to Google Play services
  * This code is returned in Activity.onActivityResult
  */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
         /*
          * If the result code is Activity.RESULT_OK, try
          * to connect again
          */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                 /*
                  * Try the request again
                  */

                        break;
                }

        }
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
               /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
           /*
            * If no resolution is available, display a dialog to the
            * user with the error.
            */
            Toast.makeText(this, connectionResult.getErrorCode(),
                    Toast.LENGTH_SHORT).show();

        }

    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle arg0) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
       
     }

    @Override
    public void onConnectionSuspended(int i) {
        
    }
     
     //When the location change, I update the map and the gallery (if possible)
     @Override
     public void onLocationChanged(Location location) {
         // Report to the UI that the location was updated

         currentLocation = location;
     //    Toast.makeText(getApplicationContext(), "Latitude: "+currentLocation.getLatitude()+" - Longitude:"+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
         
         startService(mServiceIntent);
           
         
     }

 
}
