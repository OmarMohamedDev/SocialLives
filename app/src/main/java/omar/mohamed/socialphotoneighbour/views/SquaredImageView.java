package omar.mohamed.socialphotoneighbour.views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;

public class SquaredImageView  extends AppCompatImageView {


  public SquaredImageView(Context context) {
    super(context);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int uniqueValue = getMeasuredWidth();
    setMeasuredDimension(uniqueValue, uniqueValue);
  }


}