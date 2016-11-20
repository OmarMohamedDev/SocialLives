package omar.mohamed.socialphotoneighbour.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import omar.mohamed.socialphotoneighbour.fragments.PhotoGalleryFragment;
import omar.mohamed.socialphotoneighbour.R;

public class FullScreenActivity extends Activity {
  private final String TAG = this.getClass().getSimpleName();
  private int index;

  @Override
  public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  requestWindowFeature(Window.FEATURE_NO_TITLE);
  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
  setContentView(R.layout.fragment_fullscreen);

  // get intent data
  Intent i = getIntent();

  index = i.getExtras().getInt("position");
    String title = PhotoGalleryFragment.actualImagesList.get(index).getTitle();
    String image = PhotoGalleryFragment.actualImagesList.get(index).getImage();
    String description = PhotoGalleryFragment.actualImagesList.get(index).getDescription();
    Date dateTaken = PhotoGalleryFragment.actualImagesList.get(index).getDateTaken();
    Date dateUpload = PhotoGalleryFragment.actualImagesList.get(index).getDateUpload();
    String ownerName = PhotoGalleryFragment.actualImagesList.get(index).getOwnerName();

  ImageView iv = (ImageView) findViewById(R.id.fullscreen_view);

  Picasso.with(this)
      .load(Uri.parse(image))
      .fit()
      .centerCrop()
      .into(iv);

  TextView tv = (TextView) findViewById(R.id.imageInformation);
  tv.setText("Title: "+ title +" - "+"Description: "+ description +" - Date Taken:"
              + dateTaken +" - Date Upload:"+ dateUpload +" - Owner: "+ ownerName);

  }

  public void SaveImage(View view) {

    ImageView image = (ImageView)findViewById(R.id.fullscreen_view);
    Bitmap finalBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
    String root = Environment.getExternalStorageDirectory().toString();
    File myDir = new File(root + "/FlickrNeighbour/saved_images");
    String fname = "FlickrImage-"+ PhotoGalleryFragment.actualImagesList.get(index).getTitle() +".jpg";
    File file = new File (myDir, fname);
    if (file.exists ()) {
      boolean delete = file.delete();

      if(!delete) {
        Log.w(TAG, "The file wasn't deleted when expected");
      }
    }
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
