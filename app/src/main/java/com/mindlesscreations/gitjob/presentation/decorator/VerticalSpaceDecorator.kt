package com.mindlesscreations.gitjob.presentation.decorator


import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class VerticalSpaceDecorator(private val verticalSpacing: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        outRect.bottom = verticalSpacing

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = verticalSpacing
        }
    }
}