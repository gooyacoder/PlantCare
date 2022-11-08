package com.ahm.plantcare.Utils

import android.content.Context
import android.graphics.drawable.Drawable


object IconTagDecoder {
    // Explanation:     tag -> ID -> Drawable
    //                  tag:            got from onClick in dialog                  "res/drawable/ic_common_1.xml"
    //                  ID:             stored in Plant class                       R.drawable.ic_common_1
    //                  Drawable:       loaded on run time from a given ID          Actual image
    //  If a any point you need a way to get ID given a Drawable, there's no generic way, you would need a map
    fun tagToId(context: Context, tag: String): Int {
        // Input format example: tag = "res/drawable/ic_common_1.xml"
        // After trimming : "ic_common_1"
        // Return: R.drawable.ic_common_1 (is an int, the id)
        return context.resources.getIdentifier(trimTag(tag), "drawable", context.packageName)
    }

    fun idToDrawable(context: Context, drawableId: Int): Drawable {
        return context.resources.getDrawable(drawableId)
    }

    private fun trimTag(longTag: String): String {
        // Input format example: longTag = "res/drawable/ic_common_1.xml"
        // Output format : "ic_common_1"
        val leftIdx = longTag.lastIndexOf('/')
        val rightIdx = longTag.lastIndexOf('.')
        return longTag.substring(leftIdx + 1, rightIdx)
    }
}
