package omar.mohamed.socialphotoneighbour;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import omar.mohamed.socialphotoneighbour.R;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class FullScreenActivity extends Activity {
  private int index;
  private String title;
  private String image;
  private String description;
  private Date dateTaken;
  private Date dateUpload;
  private String ownerName;
  private LatLng imageCoordinates;

  @Override
  public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  requestWindowFeature(Window.FEATURE_NO_TITLE);
  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
  setContentView(R.layout.fragment_fullscreen);

  // get intent data
  Intent i = getIntent();
 
  index = i.getExtras().getInt("position");
  title = PhotoGalleryFragment.actualImagesList.get(index).getTitle();
  image = PhotoGalleryFragment.actualImagesList.get(index).getImage();
  description = PhotoGalleryFragment.actualImagesList.get(index).getDescription();
  dateTaken = PhotoGalleryFragment.actualImagesList.get(index).getDateTaken();
  dateUpload = PhotoGalleryFragment.actualImagesList.get(index).getDateUpload();
  ownerName = PhotoGalleryFragment.actualImagesList.get(index).getOwnerName();

  ImageView iv = (ImageView) findViewById(R.id.fullscreen_view);
  
  Picasso.with(this) 
      .load(Uri.parse(image)) 
      .fit()
      .centerCrop()
      .into(iv);
  
  TextView tv = (TextView) findViewById(R.id.imageInformation);
  tv.setText("Title: "+title+" - "+"Description: "+description+" - Date Taken:"
              +dateTaken+" - Date Upload:"+dateUpload+" - Owner: "+ownerName);
  
  }
  
  public void SaveImage(View view) {
    
    ImageView image = (ImageView)findViewById(R.id.fullscreen_view);
    Bitmap finalBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
    String root = Environment.getExternalStorageDirectory().toString();
    File myDir = new File(root + "/FlickrNeighbour/saved_images");    
    myDir.mkdirs();
    Random generator = new Random();
    int n = 10000;
    n = generator.nextInt(n);
    String fname = "FlickrImage-"+ PhotoGalleryFragment.actualImagesList.get(index).getTitle() +".jpg";
    File file = new File (myDir, fname);
    if (file.exists ()) file.delete (); 
    try { 
           FileOutputStream out = new FileOutputStream(file);
           finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
           out.flush();
           out.close();

    } catch (Exception e) {
           e.printStackTrace();
    }
    
    Toast.makeText(this, "Image saved in "+ root + "/FlickrNeighbour/saved_images", Toast.LENGTH_SHORT).show();
}
  

}
