package omar.mohamed.socialphotoneighbour.utility;

import android.content.Context;
import android.widget.ImageView;

public class SquaredImageView  extends ImageView {


  public SquaredImageView(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int width = getMeasuredWidth();
    setMeasuredDimension(width, width);
  }


}