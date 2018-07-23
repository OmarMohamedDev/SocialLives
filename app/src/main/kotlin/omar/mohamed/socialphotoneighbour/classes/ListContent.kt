package omar.mohamed.socialphotoneighbour.classes

import java.util.ArrayList
import java.util.HashMap

object ListContent {

    /**
     * An array of list items.
     */
    var ITEMS: MutableList<ListItem> = ArrayList()

    /**
     * A map of list items, by ID.
     */
    var ITEM_MAP: MutableMap<String, ListItem> = HashMap()

    init {
        // Add 3 sample items.
        addItem(ListItem("1", "Photos around you"))
        //  addItem(new ListItem("2", "Where am I?"));
    }

    private fun addItem(item: ListItem) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    /**
     * A list item representing the his own content
     */
    class ListItem(var id: String, var content: String) {

        override fun toString(): String {
            return content
        }
    }
}
