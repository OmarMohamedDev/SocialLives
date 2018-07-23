package omar.mohamed.socialphotoneighbour.views

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView

class SquaredImageView(context: Context) : AppCompatImageView(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val uniqueValue = measuredWidth
        setMeasuredDimension(uniqueValue, uniqueValue)
    }


}