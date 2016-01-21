package omar.mohamed.socialphotoneighbour.utility;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private String id;
    private final LatLng mPosition;

    public MyItem(LatLng position) {
        mPosition = position;
    }
    
    public MyItem(String id, LatLng position) {
      this.id = id;
      mPosition = position;
  }

    public MyItem() {
      this.id = String.valueOf(-1);
      this.mPosition = new LatLng(0,0);
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
