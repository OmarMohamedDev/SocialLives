package omar.mohamed.socialphotoneighbour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import omar.mohamed.socialphotoneighbour.R;
import omar.mohamed.socialphotoneighbour.onepane.ItemDetailActivity;
import omar.mohamed.socialphotoneighbour.onepane.ItemDetailFragment;
import omar.mohamed.socialphotoneighbour.utility.BackgroundService;
import omar.mohamed.socialphotoneighbour.utility.ImageInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


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
public class ItemListActivity extends FragmentActivity implements
    ItemListFragment.Callbacks,
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener{
  
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
  public static Context context;
  private LocationRequest locationRequest;
  private LocationClient locationClient;
  public static Location currentLocation;
  //
  private BackgroundService backgroundService;
  private boolean isChecked;
  private MenuItem tempMenu;
  private SharedPreferences settings;
  private SharedPreferences.Editor editor;
  public static ArrayList<ImageInfo> closestImagesList;
  private Intent mServiceIntent;

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getApplicationContext();
    setContentView(R.layout.activity_item_list);
    mServiceIntent = new Intent(this, BackgroundService.class);

    if (findViewById(R.id.item_detail_container) != null) {
        // The detail container view will be present only in the
        // large-screen layouts (res/values-large and
        // res/values-sw600dp). If this view is present, then the
        // activity should be in two-pane mode.
        mTwoPane = true;
  
        // In two-pane mode, list items should be given the
        // 'activated' state when touched.
        ((ItemListFragment) getSupportFragmentManager().findFragmentById(
            R.id.item_list)).setActivateOnItemClick(true);
    }
    
 // Restore preferences
    settings = getSharedPreferences(CHECKBOX_VALUE, 0);
    isChecked = true;
    
    // <---- Geo-location --->
    locationClient = new LocationClient(this, this, this);
 // Create the LocationRequest object
    locationRequest = LocationRequest.create();
    // Use high accuracy
    locationRequest.setPriority(
            LocationRequest.PRIORITY_HIGH_ACCURACY);
    // Set the update interval to 5 seconds
    locationRequest.setInterval(UPDATE_INTERVAL);
    // Set the fastest update interval to 1 second
    locationRequest.setFastestInterval(FASTEST_INTERVAL);

    
  }
  
  protected boolean isPlayServicesAvailable() {
    int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (status == ConnectionResult.SUCCESS) {
        return (true);
    } else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
        // deal with error
    } else {
            // maps is not available
    }

    return (false);
    
    
}
  
  
  
  @Override
  protected void onStart() {
      super.onStart();
      // Connect the client.
      locationClient.connect();
  }
  
  @Override
  protected void onStop(){
     locationClient.disconnect();
     super.onStop();

    // We need an Editor object to make preference changes.
    // All objects are from android.context.Context
    settings = getSharedPreferences(CHECKBOX_VALUE, 0);
    editor = settings.edit();
    editor.putBoolean("isChecked", isChecked);

    // Commit the edits!
    editor.commit();
  }
  
  @Override
  protected void onPause(){
     super.onPause();

    // We need an Editor object to make preference changes.
    // All objects are from android.context.Context
    settings = getSharedPreferences(CHECKBOX_VALUE, 0);
    editor = settings.edit();
    editor.putBoolean("isChecked", isChecked);

    // Commit the edits!
    editor.commit();
  }
  
  @Override
  protected void onResume(){
     super.onResume();

  // Restore preferences
     settings = getSharedPreferences(CHECKBOX_VALUE, 0);
     isChecked = settings.getBoolean("isChecked", true);

  }

  /**
   * Callback method from {@link ItemListFragment.Callbacks} indicating that the
   * item with the given ID was selected.
   * @throws IOException 
   */
  @Override
  public void onItemSelected(String id) throws IOException {
  //To give to the background service the time to find the photos
    if(firstTime){
        try {
          Thread.sleep(1200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        firstTime = false;
    }
    if (mTwoPane) {
      // In two-pane mode, show the detail view in this activity by
      // adding or replacing the detail fragment using a
      // fragment transaction.
      
      if(Integer.parseInt(id)==1){
        PhotoGalleryFragment.actualImagesList = closestImagesList;
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        getSupportFragmentManager().beginTransaction()
        .replace(R.id.item_detail_container, fragment).commit();
      }
      else{
        if (isPlayServicesAvailable()) {
        PhotoMapFragment fragment = new PhotoMapFragment();
        getSupportFragmentManager().beginTransaction()
        .replace(R.id.item_detail_container, fragment).commit();
        }
        else
          throw new IOException("Google Play Service not available");
      } 

    } else {
      // In single-pane mode, simply start the detail activity
      // for the selected item ID.
      Intent detailIntent = new Intent(this, ItemDetailActivity.class);
      detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
      startActivity(detailIntent);
    }
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
     

      if(tempMenu.isChecked()){
        //Fixed values just used to inizialize the coordinates
         startService(mServiceIntent);
      }

      return super.onCreateOptionsMenu(menu);
  }
  
  
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    //Handle checkbox selection
    

      if(item.getItemId()==R.id.search){
        manipulateImageSearch(item);
        return true;  
      }
      else
       return super.onOptionsItemSelected(item);
    
            
  }
  
  public void manipulateImageSearch(MenuItem item){
    String alert;
    
    if(!item.isChecked()){
      alert="Automatic image search mode: ON";
      isChecked=true;
      
      startService(mServiceIntent);

    }
    else{
      alert="Automatic image search mode: OFF";
      isChecked=false;
      
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

 //Define a DialogFragment that displays the error dialog
 public static class ErrorDialogFragment extends DialogFragment {
     // Global field to contain the error dialog
     private Dialog mDialog;
     // Default constructor. Sets the dialog field to null
     public ErrorDialogFragment() {
         super();
         mDialog = null;
     }
     // Set the dialog to display
     public void setDialog(Dialog dialog) {
         mDialog = dialog;
     }
     // Return a Dialog to the DialogFragment.
     @Override
     public Dialog onCreateDialog(Bundle savedInstanceState) {
         return mDialog;
     }
 }

 /*
  * Handle results returned to the FragmentActivity
  * by Google Play services
  */
 protected void onActivityResult(
         int requestCode, int resultCode, Intent data) {
     // Decide what to do based on the original request code
     switch (requestCode) {
      
         case CONNECTION_FAILURE_RESOLUTION_REQUEST :
         /*
          * If the result code is Activity.RESULT_OK, try
          * to connect again
          */
             switch (resultCode) {
                 case Activity.RESULT_OK :
                 /*
                  * Try the request again
                  */
                
                 break;
             }
     
     }
  }

 private boolean servicesConnected() {
     // Check that Google Play services is available
     int resultCode =
             GooglePlayServicesUtil.
                     isGooglePlayServicesAvailable(this);
     // If Google Play services is available
     if (ConnectionResult.SUCCESS == resultCode) {
         // In debug mode, log the status
         Log.d("Location Updates",
                 "Google Play services is available.");
         // Continue
         return true;
     // Google Play services was not available for some reason
     } else {
       Toast.makeText(getApplicationContext(), "CONNECTION_FAILURE_RESOLUTION_REQUEST", 
           Toast.LENGTH_SHORT).show();
       return false;
     }
 }
    
     /*
      * Called by Location Services if the attempt to
      * Location Services fails.
      */
     @Override
     public void onConnectionFailed(ConnectionResult connectionResult) {
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
         locationClient.requestLocationUpdates(locationRequest, this);
         currentLocation = locationClient.getLastLocation();
       
     }
     /*
      * Called by Location Services if the connection to the
      * location client drops because of an error.
      */
     @Override
     public void onDisconnected() {
       
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
