package com.workgroup.figureplayground.figure.d3

import android.content.Context
import android.graphics.Canvas
import com.workgroup.figureplayground.figure.Figure3D
import com.workgroup.figureplayground.figure.Vector

class Pyramid(context: Context) : Figure3D(context){

    init {
        paint.strokeWidth = 5f
        paint.setARGB(255, 0, 0, 0)
    }

    override fun onDraw(canvas: Canvas) {
        rotateStartingPoints(xRot + changedXRot, yRot + changedYRot, zRot + changedZRot)
        drawFigure(canvas)
    }

    private fun drawFigure(canvas: Canvas){
        canvas.drawLine(points[0].x, points[0].y, points[1].x, points[1].y, paint)
        canvas.drawLine(points[1].x, points[1].y, points[3].x, points[3].y, paint)
        canvas.drawLine(points[3].x, points[3].y, points[2].x, points[2].y, paint)
        canvas.drawLine(points[2].x, points[2].y, points[0].x, points[0].y, paint)

        canvas.drawLine(points[0].x, points[0].y, points[4].x, points[4].y, paint)
        canvas.drawLine(points[1].x, points[1].y, points[4].x, points[4].y, paint)
        canvas.drawLine(points[2].x, points[2].y, points[4].x, points[4].y, paint)
        canvas.drawLine(points[3].x, points[3].y, points[4].x, points[4].y, paint)
    }

    override fun generatePoints(){
        var lineSize = 0.25f*width
        startingPoints = arrayListOf(
            Vector(0.25f*width, 0.75f*height, -lineSize),
            Vector(0.25f*width, 0.75f*height, lineSize),
            Vector(0.75f*width, 0.75f*height, -lineSize),
            Vector(0.75f*width, 0.75f*height, lineSize),
            Vector(0.5f*width, 0.25f*height, 0f)

        )
        points  = arrayListOf(
            Vector(0.25f*width, 0.75f*height, -lineSize),
            Vector(0.25f*width, 0.75f*height, lineSize),
            Vector(0.75f*width, 0.75f*height, -lineSize),
            Vector(0.75f*width, 0.75f*height, lineSize),
            Vector(0.5f*width, 0.25f*height, 0f)
        )
    }
}
