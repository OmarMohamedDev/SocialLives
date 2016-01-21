package omar.mohamed.socialphotoneighbour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

import omar.mohamed.socialphotoneighbour.utility.BackgroundService;
import omar.mohamed.socialphotoneighbour.utility.FlickrHelper;
import omar.mohamed.socialphotoneighbour.utility.ImageInfo;
import omar.mohamed.socialphotoneighbour.utility.MyItem;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.yuyang226.flickr.FlickrException;
import com.gmail.yuyang226.flickr.Parameter;
import com.gmail.yuyang226.flickr.REST;
import com.gmail.yuyang226.flickr.Response;
import com.gmail.yuyang226.flickr.Transport;
import com.gmail.yuyang226.flickr.photos.GeoData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

public class PhotoMapFragment extends SupportMapFragment implements LocationListener, OnMyLocationButtonClickListener{

  private SupportMapFragment mMapFragment;
  protected static ArrayList<ImageInfo> actualImagesList;
  private GoogleMap mMapView;
  protected static LatLng myCurrentLocation;
  private Context context;
  //Couple <Url, MarkerOptions>
  private Map<String, MarkerOptions> imagesNamesArchive;
  protected static ClusterManager clusterManager;
  private UiSettings uiSettings;
  LocationManager lm;
  private Context mContext;
  private GeoData tempGeoDataContainer;
  private Transport transport;
  public static final String METHOD_GET_LOCATION = "flickr.photos.geo.getLocation";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = ItemListActivity.context;
    actualImagesList = ItemListActivity.closestImagesList;
    imagesNamesArchive = new HashMap<String, MarkerOptions>();
    
    if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy 
              = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }  
    

    setUpMapIfNeeded();
   }
  
  @Override
  public void onResume(){
    super.onResume();
    
    setUpMapIfNeeded();
    
    clusterManager = new ClusterManager<MyItem>(context, mMapView);
    mMapView.setOnCameraChangeListener(clusterManager);
    
    if(!(actualImagesList==null))
          lookAroundForNewMarkers(actualImagesList);
    
    setUpMapIfNeeded();
    
  }  
  

  
  private void setUpMapIfNeeded() {
    if(mMapFragment == null){
      mMapFragment = this;
    }
    // Do a null check to confirm that we have not already instantiated the map.
    if (mMapFragment != null) {
        // Try to obtain the map from the SupportMapFragment.
        mMapView = mMapFragment.getMap();
        // Check if we were successful in obtaining the map.
        if (mMapView != null) {
            mMapView.setMyLocationEnabled(true);
            uiSettings = mMapView.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(true);
            LocationManager lm = (LocationManager)context.getSystemService(context.LOCATION_SERVICE); 
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                myCurrentLocation = new LatLng(latitude,longitude);
              
                CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myCurrentLocation)      // Sets the center of the map to the user location
                .zoom(17)                   // Sets the zoom
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
                mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        mMapView.setOnCameraChangeListener(clusterManager);
        Log.d("Test","Is inside Setupmap");
        }
    }
}
  
  public void lookAroundForNewMarkers(final ArrayList<ImageInfo> actualImagesList){
      // put a pin in the map for every image received
        
          //Verify if there are no image close to the user or the image, in this session, 
          //was already found: in both the cases don't add a new Marker
     
      /*    if(!(imagesNamesArchive.containsKey(imageIndex.getImage()) 
              || imageIndex.getImage().equals(getString(R.string.url_images_not_found)))){
               
            //Handling the case that one image don't have the title and put a String
            //in the way that, if the user, click on the marker, at least that string appear 
            //(To avoid user's confusion about this functionality)
            if(imageIndex.getTitle().equals(""))
                 imageIndex.setTitle("Image with No Title");
            */
            new Thread(new Runnable(){
              @Override
              public void run() {
                
                for(ImageInfo imageIndex: actualImagesList){
                
                    try {
                      tempGeoDataContainer = getPhotoLocation(imageIndex.getId());
                    } catch (Exception e1) {
                      e1.printStackTrace();
                    }  
                    
                    if(tempGeoDataContainer != null)
                       PhotoMapFragment.clusterManager.addItem(new MyItem(new LatLng(tempGeoDataContainer.getLatitude(),tempGeoDataContainer.getLongitude())));
                    
                    Log.d("Test","Is inside onLocationChanged - ending handling the image"+imageIndex.getTitle());
                  
                 }
              }
          }).start();
        
      
}

  
  @Override
  public void onLocationChanged(Location location) {
    //Updating the images related to new position
    actualImagesList = ItemListActivity.closestImagesList;
    
    if(!(actualImagesList==null))
       lookAroundForNewMarkers(actualImagesList);
          
    setUpMapIfNeeded();
      
  }
  
  /*Get the geo data (latitude and longitude and the accuracy level) for a photo.*/
  //N.B. i had to modify also this library method for the communication protocol problem
  public GeoData getPhotoLocation(String photoId) throws IOException, FlickrException, JSONException {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(new Parameter("method", METHOD_GET_LOCATION));
    parameters.add(new Parameter("api_key", FlickrHelper.API_KEY));
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
    // I ignore the id attribute. should be the same as the given
    // photo id.
    GeoData geoData = new GeoData(lonStr, latStr, accStr);
    
    return geoData;
}
  
  @Override
  public boolean onMyLocationButtonClick() {
    // TODO Auto-generated method stub
    return false;
  }
  @Override
  public void onProviderDisabled(String provider) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onProviderEnabled(String provider) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub
    
  }


  
  
  

} 