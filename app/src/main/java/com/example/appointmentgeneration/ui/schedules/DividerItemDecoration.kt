import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Paint
import android.content.Context
import androidx.core.content.ContextCompat
import com.example.appointmentgeneration.R

class DividerItemDecoration(context: Context, private val height: Int = 2) : RecyclerView.ItemDecoration() {

    private val paint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.gray) // Divider 색상
        style = Paint.Style.FILL
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until parent.childCount - 1) { // 마지막 항목에는 Divider 추가 X
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + height

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = height // Divider 높이 추가
    }
}
