package omar.mohamed.socialphotoneighbour.utility;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private String id;
    private final LatLng mPosition;

    public MyItem(LatLng position) {
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    
    public String getId(){
      return id;
    }
    
    public void setId(String id){
      this.id = id;
    }
}
