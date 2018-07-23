package omar.mohamed.socialphotoneighbour.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import omar.mohamed.socialphotoneighbour.R
import omar.mohamed.socialphotoneighbour.fragments.ItemDetailFragment
import omar.mohamed.socialphotoneighbour.fragments.PhotoGalleryFragment
import omar.mohamed.socialphotoneighbour.fragments.PhotoMapFragment
import omar.mohamed.socialphotoneighbour.services.BackgroundService

/**
 * An activity representing a single Item detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a [ItemListActivity].
 *
 *
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a [ItemDetailFragment].
 */
class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        //TODO:Temporary code block used to start the image discovery until we fix menu and map
        val serviceIntent = Intent(this, BackgroundService::class.java)
        startService(serviceIntent)
        //

        // Show the Up button in the action bar.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B71C1C"))) // set your desired color

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            val fragment: Fragment
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val intent = intent
            val id = intent.getLongExtra("item_id", -1)
            if (id <= 0) { //TODO: Temporary <=, put == when fix map mode
                fragment = PhotoGalleryFragment()
            } else {
                fragment = PhotoMapFragment()
            }

            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.item_detail_container, fragment)
            ft.commit()


        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, Intent(this, ItemListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
