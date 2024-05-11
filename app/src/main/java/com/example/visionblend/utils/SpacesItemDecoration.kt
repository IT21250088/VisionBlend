package com.example.visionblend.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration: RecyclerView.ItemDecoration(){
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        if(parent.getChildLayoutPosition(view) %2 != 0){
            outRect.top = 50
            outRect.left = -20

        }else{
            outRect.top = 0
        }
    }
}