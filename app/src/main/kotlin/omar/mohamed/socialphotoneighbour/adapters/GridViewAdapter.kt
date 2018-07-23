package omar.mohamed.socialphotoneighbour.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.squareup.picasso.Picasso

import java.util.ArrayList
import java.util.Collections

import omar.mohamed.socialphotoneighbour.classes.ImageInfo
import omar.mohamed.socialphotoneighbour.R
import omar.mohamed.socialphotoneighbour.views.SquaredImageView

import android.widget.ImageView.ScaleType.CENTER_CROP

class GridViewAdapter(internal val context: Context, urls: ArrayList<ImageInfo>) : BaseAdapter() {
    internal var urls: List<ImageInfo>

    init {
        this.urls = urls


        Collections.addAll(urls)

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var view: SquaredImageView

        convertView?.let {
            view = convertView as SquaredImageView
        } ?: run {
            view = SquaredImageView(context)
            view.scaleType = CENTER_CROP
        }

        // Get the image URL for the current position.
        val url = getItem(position).image

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.error) //
                .fit() //
                .into(view)

        return view
    }

    override fun getCount(): Int {
        return urls.size
    }

    override fun getItem(position: Int): ImageInfo {
        return urls[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}