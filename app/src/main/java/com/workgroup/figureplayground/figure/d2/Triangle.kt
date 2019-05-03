package com.workgroup.figureplayground.figure.d2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.workgroup.figureplayground.figure.Figure
import com.workgroup.figureplayground.figure.Point
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.math.sqrt

class Triangle(context: Context) : Figure(context) {

    private val paint = Paint()

    var isTouched = false
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var moveEnabled = false
    var pointTouched = 0
    var timer = 0L
    var distance = 0f
    var diff = 0L

    //For thread in onTouchEvent
    private var startEventX = 0f
    private var startEventY = 0f
    private var startEventAction = 0

    init {
        paint.setARGB(255, 255, 0, 0)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawCircle(points[0].x, points[0].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(points[1].x, points[1].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(points[2].x, points[2].y, CIRCLE_RADIUS, paint)

        canvas.drawLine(points[0].x, points[0].y, points[1].x, points[1].y, paint)
        canvas.drawLine(points[1].x, points[1].y, points[2].x, points[2].y, paint)
        canvas.drawLine(points[2].x, points[2].y, points[0].x, points[0].y, paint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        startEventX = event!!.x
        startEventY = event.y
        startEventAction = event.action

        val eventPoint = Point(event.x, event.y)

        if(event.action == android.view.MotionEvent.ACTION_DOWN && !isTouched){

            val distances = calculateDistancesFromPoint(eventPoint)
            val minVal = distances.min()!!

            if(minVal < 1.2 * CIRCLE_RADIUS){
                distance = minVal
                isTouched = true
                timer  = System.currentTimeMillis()
                pointTouched = distances.indexOf(minVal)

                thread(start = true) {
                    var condition = true
                    while(diff < SECOND){

                        diff = System.currentTimeMillis() - timer
                        distance = countDistance(points[pointTouched].x, points[pointTouched].y, startEventX, startEventY)

                        if(distance > 1.2 * CIRCLE_RADIUS || startEventAction == android.view.MotionEvent.ACTION_UP){
                            condition = false
                            break
                        }
                    }
                    diff = 0
                    if(condition){
                        if (Build.VERSION.SDK_INT >= 26) {
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            vibrator.vibrate(200)
                        }
                        moveEnabled = true
                    }
                }
            }
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && moveEnabled){
            points[pointTouched].x = eventPoint.x
            points[pointTouched].y = eventPoint.y
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_UP){
            pointTouched = -1
            isTouched = false
            moveEnabled = false
            timer = 0
        }

        return true
    }

    private fun calculateDistancesFromPoint(point: Point): List<Float>{
        val distances = ArrayList<Float>()

        points.forEach {
            distances.add(countDistance(it.x, it.y, point.x , point.y))
        }
        return distances
    }

    private fun countDistance(pointXA : Float, pointYA : Float, pointXB : Float, pointYB : Float): Float{
        return sqrt((pointXA - pointXB).pow(2) + (pointYA- pointYB).pow(2))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generatePoints()
    }

    private fun generatePoints(){
        points = arrayListOf(Point(0.5f*width, 0.25f*height),
            Point(0.25f*width, 0.75f*height),
            Point(0.75f*width, 0.75f*height))
    }

    companion object {
        const val CIRCLE_RADIUS: Float = 30.0f
        const val SECOND: Int = 1000
    }
}