package omar.mohamed.socialphotoneighbour.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast

import java.util.ArrayList

import omar.mohamed.socialphotoneighbour.adapters.GridViewAdapter
import omar.mohamed.socialphotoneighbour.classes.ImageInfo
import omar.mohamed.socialphotoneighbour.R
import omar.mohamed.socialphotoneighbour.activities.FullScreenActivity
import omar.mohamed.socialphotoneighbour.activities.ItemListActivity


class PhotoGalleryFragment : Fragment() {


    private lateinit var r: MyReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_gallery, container,
                false)
    }

    fun refresh() {
        val gv = view!!.findViewById<View>(R.id.grid_view) as GridView
        actualImagesList = ItemListActivity.closestImagesList
        if (gv != null) {
            if (actualImagesList != null) {
                val adapter = GridViewAdapter(activity!!, actualImagesList!!)
                gv.adapter = adapter
                gv.onItemClickListener = OnItemClickListener { parent, v, position, id ->
                    // Sending image url to FullScreenActivity
                    val i = Intent(activity!!.applicationContext, FullScreenActivity::class.java)

                    i.putExtra("position", position)
                    startActivity(i)
                }

            }
        } else {
            Toast.makeText(context, R.string.grid_view_not_found, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(r)
    }

    override fun onResume() {
        super.onResume()
        r = MyReceiver()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(r,
                IntentFilter("TAG_REFRESH"))
    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refresh()
        }
    }

    companion object {
        var actualImagesList: ArrayList<ImageInfo>? = null
    }
}