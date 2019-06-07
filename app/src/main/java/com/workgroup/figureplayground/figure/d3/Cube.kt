package com.workgroup.figureplayground.figure.d3

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import com.workgroup.figureplayground.figure.*

class Cube(context: Context) : Figure3D(context){

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
        canvas.drawLine(points[1].x, points[1].y, points[7].x, points[7].y, paint)
        canvas.drawLine(points[7].x, points[7].y, points[6].x, points[6].y, paint)
        canvas.drawLine(points[6].x, points[6].y, points[0].x, points[0].y, paint)

        canvas.drawLine(points[2].x, points[2].y, points[3].x, points[3].y, paint)
        canvas.drawLine(points[3].x, points[3].y, points[5].x, points[5].y, paint)
        canvas.drawLine(points[5].x, points[5].y, points[4].x, points[4].y, paint)
        canvas.drawLine(points[4].x, points[4].y, points[2].x, points[2].y, paint)

        canvas.drawLine(points[0].x, points[0].y, points[2].x, points[2].y, paint)
        canvas.drawLine(points[1].x, points[1].y, points[3].x, points[3].y, paint)
        canvas.drawLine(points[6].x, points[6].y, points[4].x, points[4].y, paint)
        canvas.drawLine(points[7].x, points[7].y, points[5].x, points[5].y, paint)
    }

    override fun generatePoints(){
        var lineSize = 0.25f*width
        startingPoints = arrayListOf(
            Vector(0.25f*width, (0.5f*height)-lineSize, -lineSize),
            Vector(0.25f*width, (0.5f*height)-lineSize, lineSize),
            Vector(0.75f*width, (0.5f*height)-lineSize, -lineSize),
            Vector(0.75f*width, (0.5f*height)-lineSize, lineSize),
            Vector(0.75f*width, (0.5f*height)+lineSize, -lineSize),
            Vector(0.75f*width, (0.5f*height)+lineSize, lineSize),
            Vector(0.25f*width, (0.5f*height)+lineSize, -lineSize),
            Vector(0.25f*width, (0.5f*height)+lineSize, lineSize)
        )
        points  = arrayListOf(
            Vector(0.25f*width, (0.5f*height)-lineSize, -lineSize),
            Vector(0.25f*width, (0.5f*height)-lineSize, lineSize),
            Vector(0.75f*width, (0.5f*height)-lineSize, -lineSize),
            Vector(0.75f*width, (0.5f*height)-lineSize, lineSize),
            Vector(0.75f*width, (0.5f*height)+lineSize, -lineSize),
            Vector(0.75f*width, (0.5f*height)+lineSize, lineSize),
            Vector(0.25f*width, (0.5f*height)+lineSize, -lineSize),
            Vector(0.25f*width, (0.5f*height)+lineSize, lineSize)
        )
    }
}
