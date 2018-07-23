package omar.mohamed.socialphotoneighbour.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView

import omar.mohamed.socialphotoneighbour.R
import omar.mohamed.socialphotoneighbour.activities.ItemDetailActivity
import omar.mohamed.socialphotoneighbour.classes.ListContent

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * [ItemDetailFragment].
 *
 *
 *
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class ItemListFragment : ListFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter = ArrayAdapter<ListContent.ListItem>(activity!!,
                R.layout.cardview_item_list, R.id.card_view_item_list_title,
                ListContent.ITEMS)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView.divider = null
        listView.dividerHeight = 0
    }

    override fun onListItemClick(listView: ListView?, view: View?, position: Int,
                                 id: Long) {
        super.onListItemClick(listView, view, position, id)

        try {
            Thread.sleep(1200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Start the detail activity for the selected item ID.
        val detailIntent = Intent(context, ItemDetailActivity::class.java)
        detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id)
        startActivity(detailIntent)
    }

}
