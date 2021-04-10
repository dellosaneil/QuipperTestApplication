package com.example.quippertrainingapplication

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewDecorator(private val topBottomPadding : Int = 5, private val leftRightPadding : Int = 5) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = topBottomPadding
        outRect.top = topBottomPadding
        outRect.left = leftRightPadding
        outRect.right = leftRightPadding
    }
}