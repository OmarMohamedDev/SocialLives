package omar.mohamed.socialphotoneighbour.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import omar.mohamed.socialphotoneighbour.classes.ListContent
import omar.mohamed.socialphotoneighbour.R
import omar.mohamed.socialphotoneighbour.activities.ItemDetailActivity
import omar.mohamed.socialphotoneighbour.activities.ItemListActivity

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a [ItemListActivity] in two-pane mode (on tablets) or a
 * [ItemDetailActivity] on handsets.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class ItemDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var mItem: ListContent.ListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments!!.containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ListContent.ITEM_MAP[arguments!!.getString(ARG_ITEM_ID)]
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_item_detail, container,
                false)

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            (rootView.findViewById<View>(R.id.item_detail) as TextView).text = mItem!!.content
        }
        //setHasOptionsMenu(true);

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        val ARG_ITEM_ID = "item_id"
    }
}
