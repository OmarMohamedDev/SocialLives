package omar.mohamed.socialphotoneighbour.classes

import java.io.IOException

/**
 * A callback interface that all activities containing this fragment must
 * implement. This mechanism allows activities to be notified of item
 * selections.
 */
interface Callbacks {
    /**
     * Callback for when an item has been selected.
     * @throws IOException Input/Output Exception
     */
    @Throws(IOException::class)
    fun onItemSelected(id: String)
}