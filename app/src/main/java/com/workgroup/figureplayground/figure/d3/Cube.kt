package com.workgroup.figureplayground.figure.d3

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import com.workgroup.figureplayground.figure.*

class Cube(context: Context) : Figure3D(context){

    private var xRot: Float = 0f
    private var yRot: Float = 0f
    private var zRot: Float = 0f

    private var changedXRot: Float = 0f
    private var changedYRot: Float = 0f
    private var changedZRot: Float = 0f

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


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val eventPoint = Point(event!!.x, event.y)
        if(event!!.action == android.view.MotionEvent.ACTION_DOWN){
            startEventTouchPoint = eventPoint
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE){
            if(selectedRotation == Rotation.X) {
                Log.d("info", "x")
                val angle = (eventPoint.y - startEventTouchPoint.y) / height * Math.PI
                changedXRot = angle.toFloat()
            }
            else if(selectedRotation == Rotation.Y){
                Log.d("info", "y")
                val angle = (eventPoint.x - startEventTouchPoint.x) / width * Math.PI
                changedYRot = angle.toFloat()
            }
            else if(selectedRotation == Rotation.Z){
                Log.d("info", "z")
                val changeDist = calculateDistance(startEventTouchPoint, eventPoint)
                val maxDist = calculateDistance(Point(0f,0f), Point(width.toFloat(), height.toFloat()))
                val angle = changeDist / maxDist * Math.PI
                changedZRot = angle.toFloat()
            }
        }
        else if(event.action == android.view.MotionEvent.ACTION_UP){
            xRot = (xRot + changedXRot % Math.PI).toFloat()
            yRot = (yRot + changedYRot % Math.PI).toFloat()
            zRot = (zRot + changedZRot % Math.PI).toFloat()
            changedXRot = 0f
            changedYRot = 0f
            changedZRot = 0f
        }
        this.invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generatePoints()
    }
    private fun generatePoints(){
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
