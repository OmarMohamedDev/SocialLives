package omar.mohamed.socialphotoneighbour.utility;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import omar.mohamed.socialphotoneighbour.R;
import omar.mohamed.socialphotoneighbour.R.drawable;
import static android.widget.ImageView.ScaleType.CENTER_CROP;

public final class GridViewAdapter extends BaseAdapter {

final Context context;
List<ImageInfo> urls;

public GridViewAdapter(Context context, ArrayList<ImageInfo> urls) {
this.context = context;
this.urls = urls;

for(ImageInfo index: urls)
    Collections.addAll(urls);

}

@Override public View getView(int position, View convertView, ViewGroup parent) {
SquaredImageView view = (SquaredImageView) convertView;
if (view == null) {
  view = new SquaredImageView(context);
  view.setScaleType(CENTER_CROP);     
}

// Get the image URL for the current position.
String url = getItem(position).getImage().toString();

// Trigger the download of the URL asynchronously into the image view.
Picasso.with(context) //
    .load(url) //
    .placeholder(R.drawable.placeholder) //
    .error(R.drawable.error) //
    .fit() //
    .into(view);

return view;
}

@Override public int getCount() {
return urls.size();
}

@Override public ImageInfo getItem(int position) {
return urls.get(position);
}

@Override public long getItemId(int position) {
return position;
}
}