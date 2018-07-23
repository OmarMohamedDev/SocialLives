package omar.mohamed.socialphotoneighbour.classes

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyItem(private val mPosition: LatLng) : ClusterItem {
    var id: String? = null

    override fun getPosition(): LatLng {
        return mPosition
    }
}
