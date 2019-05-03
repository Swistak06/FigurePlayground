package com.workgroup.figureplayground.figure

import android.content.Context
import android.view.View
import java.util.ArrayList

open class Figure(context: Context) : View(context) {

    protected var points: ArrayList<Point> = ArrayList()
    protected var lines: List<Line> = ArrayList()

    var cameraMode: CameraMode = CameraMode.MOVE


}
