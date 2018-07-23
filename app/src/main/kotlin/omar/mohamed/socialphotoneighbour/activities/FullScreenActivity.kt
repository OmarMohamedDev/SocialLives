package omar.mohamed.socialphotoneighbour.activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import omar.mohamed.socialphotoneighbour.R
import omar.mohamed.socialphotoneighbour.fragments.PhotoGalleryFragment
import java.io.File
import java.io.FileOutputStream

class FullScreenActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private var index: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.fragment_fullscreen)

        // get intent data
        val i = intent

        index = i.extras!!.getInt("position")
        val title = PhotoGalleryFragment.actualImagesList?.get(index)?.title
        val image = PhotoGalleryFragment.actualImagesList?.get(index)?.image
        val description = PhotoGalleryFragment.actualImagesList?.get(index)?.description
        val dateTaken = PhotoGalleryFragment.actualImagesList?.get(index)?.dateTaken
        val dateUpload = PhotoGalleryFragment.actualImagesList?.get(index)?.dateUpload
        val ownerName = PhotoGalleryFragment.actualImagesList?.get(index)?.ownerName

        val iv = findViewById<View>(R.id.fullscreen_view) as ImageView

        Picasso.with(this)
                .load(Uri.parse(image))
                .fit()
                .centerCrop()
                .into(iv)

        val tv = findViewById<View>(R.id.imageInformation) as TextView
        tv.text = ("Title: " + title + " - " + "Description: " + description + " - Date Taken:"
                + dateTaken + " - Date Upload:" + dateUpload + " - Owner: " + ownerName)

    }

    fun SaveImage(view: View) {

        val image = findViewById<View>(R.id.fullscreen_view) as ImageView
        val finalBitmap = (image.drawable as BitmapDrawable).bitmap
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/FlickrNeighbour/saved_images")
        val fname = "FlickrImage-" + PhotoGalleryFragment.actualImagesList?.get(index)?.title + ".jpg"
        val file = File(myDir, fname)
        if (file.exists()) {
            val delete = file.delete()

            if (!delete) {
                Log.w(TAG, "The file wasn't deleted when expected")
            }
        }
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        Toast.makeText(this, "Image saved in $root/FlickrNeighbour/saved_images", Toast.LENGTH_SHORT).show()
    }


}
