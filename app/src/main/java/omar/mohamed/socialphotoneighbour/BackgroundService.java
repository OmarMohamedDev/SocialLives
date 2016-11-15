package omar.mohamed.socialphotoneighbour;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.Parameter;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RESTResponse;
import com.googlecode.flickrjandroid.Response;
import com.googlecode.flickrjandroid.Transport;
import com.googlecode.flickrjandroid.oauth.OAuthUtils;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotoUtils;
import com.googlecode.flickrjandroid.util.IOUtilities;
import com.googlecode.flickrjandroid.util.UrlUtilities;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

public class  BackgroundService extends IntentService {

  public static final int PHOTOS_PER_TIME_EVERY_SEARCH = 500;
  public static final double SEARCH_RADIUS = 0.2;
  public static final String METHOD_SEARCH = "flickr.photos.search";
  protected static List<Photo> photos;
  private Context mContext;
  private String IMAGE_NOT_FOUND;
  protected static ArrayList<ImageInfo> resultList;
  public static final String API_KEY = "01bd8e557c0167f56bbc1d82e5e6370e"; //$NON-NLS-1$

    public BackgroundService() {
        super("BackgroundService");
    }

    public BackgroundService(String name, Context mContext) {
        super(name);
        this.mContext = mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        if (mContext != null) {
            IMAGE_NOT_FOUND = mContext.getResources().getString(R.string.images_not_found);
        }
    }


    @Override
  protected void onHandleIntent(Intent workIntent) {
    String actualLatitude;
    String actualLongitude;

    if(ItemListActivity.currentLocation != null){
      actualLatitude = String.valueOf(ItemListActivity.currentLocation.getLatitude());
      actualLongitude = String.valueOf(ItemListActivity.currentLocation.getLongitude());
    }
    else
    {
      actualLatitude = String.valueOf(0);
      actualLongitude = String.valueOf(0);
    }

    Set<String> extras = new HashSet<>();
    extras.add("description"); //$NON-NLS-1$
    extras.add("date_taken"); //$NON-NLS-1$
    extras.add("date_upload"); //$NON-NLS-1$
    extras.add("owner_name"); //$NON-NLS-1$
    extras.add("url_o"); //$NON-NLS-1$
    SearchParametersModified searchParam = new SearchParametersModified();
    searchParam.setRadius(SEARCH_RADIUS); //the radius units are as default: km
    searchParam.setExtras(extras);
    searchParam.setHasGeo(true);
    searchParam.setLatitude(actualLatitude);
    searchParam.setLongitude(actualLongitude);
    try {
      searchParam.setMedia("photos");
    } catch (FlickrException e1) {
      e1.printStackTrace();
    }
    searchParam.setAccuracy(16);
    
    //Method that look for the flickr's images closest to the user
    //N.B. I'am not using the official library method to get the images
    //from flickr due to a Flickr update problem that changed the communication protocol
    //few days ago. To get more info about it: http://bit.ly/1kuYWfl
    
    try {
      photos = imageSearch(searchParam, PHOTOS_PER_TIME_EVERY_SEARCH, 1);
    } catch (Exception e1) {
      e1.printStackTrace();
    } 
     
    resultList = new ArrayList<>();
    //If not are photos around the user
    if(photos==null){ 
      //Instantiate a new PhotoList element
      photos = new PhotoList();
      //Add temporary image while the user waiting to reach a position with photos close to him
      //Factitious user and Geodata created just to fit the photo class structure
      //when not are any photos
      User nobody = new User();
      nobody.setUsername(mContext.getResources().getString(R.string.any));
      GeoData nowhere= new GeoData();
      nowhere.setLatitude(0);
      nowhere.setLongitude(0);

      Photo temp = new Photo();
      temp.setTitle(IMAGE_NOT_FOUND);
      temp.setUrl(mContext.getResources().getString(R.string.url_images_not_found));
      temp.setDescription(IMAGE_NOT_FOUND);
      temp.setDateTaken(new Date());
      temp.setDatePosted(new Date());
      temp.setOwner(nobody);
      temp.setGeoData(nowhere);
      
      photos.add(temp);
    }

        for (Photo photo : photos) {

            try{
            resultList.add(new ImageInfo(photo.getId(),photo.getTitle(),photo.getMediumUrl() ,photo.getDescription(),
                photo.getDateTaken(),photo.getDatePosted(), photo.getOwner().getUsername()));
            }
            catch(Exception e){
            Log.d("print","Problem about geodata.");
            }

        }
        
        ItemListActivity.closestImagesList = resultList;

  }

    private List<Photo> imageSearch(SearchParametersModified searchParam, int perPage, int page)
      throws IOException, FlickrException, JSONException, ParserConfigurationException {
      List<Parameter> parameters = new ArrayList<>();
      parameters.add(new Parameter("method", METHOD_SEARCH));
      parameters.add(new Parameter("api_key", API_KEY));
      parameters.addAll(searchParam.getAsParameters());
      if (perPage > 0) {
          parameters.add(new Parameter("per_page", "" + perPage));
      }
      if (page > 0) {
          parameters.add(new Parameter("page", "" + page));
      }
      Transport transport = new REST();
      Response response = getModified(transport.getPath(), parameters);
      if (response.isError()) {
          throw new FlickrException(response.getErrorCode(), response.getErrorMessage());
      }
      
    return PhotoUtils.createPhotoList(response.getData()); //RETURN FINALE
  }
  
  public static Response getModified(String path, List<Parameter> parameters) throws IOException, JSONException{
    parameters.add(new Parameter("nojsoncallback", "1"));
    parameters.add(new Parameter("format", "json"));
    String data = getLineModified(path, parameters);
    return new RESTResponse(data);
  
  }
  
  private static String getLineModified(String path, List<Parameter> parameters) throws IOException {
    InputStream in = null;
    BufferedReader rd = null;
    try {
        in = getInputStreamModified(path, parameters);
        rd = new BufferedReader(new InputStreamReader(in, OAuthUtils.ENC));
        final StringBuilder buf = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            buf.append(line);
        }
        return buf.toString();
    } finally {
        IOUtilities.close(in);
        IOUtilities.close(rd);
    }
   }
    
    private static InputStream getInputStreamModified(String path, List<Parameter> parameters) throws IOException {
      URL url = UrlUtilities.buildUrl("www.flickr.com", 80, path, parameters);
      //Operation that permit, finally, to connect to the new Flickr communication protocol 
      //(Before: Http, After: Https)
      String urlDaModificare = url.toString();
      String urlModificato = urlDaModificare.replace("http:", "https:");
      url = new URL(urlModificato);
      //
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.addRequestProperty("Cache-Control", "no-cache,max-age=0"); 
      conn.addRequestProperty("Pragma", "no-cache"); 
      conn.setRequestMethod("GET");
      conn.connect();
      return conn.getInputStream();
  }


}
