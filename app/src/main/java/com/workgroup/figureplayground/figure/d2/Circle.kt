package com.workgroup.figureplayground.figure.d2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.VibrationEffect
import android.view.MotionEvent
import com.workgroup.figureplayground.figure.CameraMode
import com.workgroup.figureplayground.figure.Figure2D
import com.workgroup.figureplayground.figure.Point
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.math.sqrt

class Circle(context: Context) : Figure2D(context){

    var radius = 200f

    init {
        paint.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        if(figureMiddlePoint != null) {
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.setARGB(255, 0, 0, 255)
            canvas.drawCircle(points[0].x, points[0].y, CIRCLE_RADIUS / 3, paint)
        }
        paint.style = Paint.Style.STROKE
        paint.setARGB(255, 0, 0, 0)
        canvas.drawCircle(points[0].x, points[0].y,radius, paint)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        currentThreadEvent.x = event!!.x
        currentThreadEvent.y = event.y
        currentThreadEventAction = event.action

        val eventPoint = Point(event.x, event.y)

        if(event.action == android.view.MotionEvent.ACTION_DOWN && !isScreenTouched){

            val distance = countDistance(points[0],eventPoint)

            isScreenTouched = true

            if(distance > radius - 30 && distance < radius + 30){
                distanceFromTouchedPoint = distance
                timer  = System.currentTimeMillis()
                createPointLongPressEventThread()
            }
            else{
                cameraEventEnabled = true
                startCameraEventTouchPoint = Point(event.x, event.y)
                startCameraEventPoints = ArrayList()
                for(i in 0 until points.size){
                    startCameraEventPoints.add(Point(points[i].x, points[i].y))
                }
            }
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && cameraEventEnabled){
            if(cameraMode == CameraMode.MOVE)
                moveAllPoints(startCameraEventTouchPoint, eventPoint)
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && singlePointMovementEnabled){
            resizeCircle()
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_UP){
            resetTouchEventActions()
            if(cameraMode == CameraMode.ROTATE) {
                findFigureMiddlePoint()
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generatePoints()
    }

    override fun generatePoints(){
        points = arrayListOf(
            Point(0.5f*width, 0.5f*height)
        )
    }

    private fun moveAllPoints(startMovementPoint: Point, eventPoint: Point) {
        val xDiff = eventPoint.x - startMovementPoint.x
        val yDiff = eventPoint.y - startMovementPoint.y

        points.forEach {
            val index = points.indexOf(it)
            it.x = startCameraEventPoints[index].x + xDiff
            it.y = startCameraEventPoints[index].y + yDiff
        }
    }

    private fun createPointLongPressEventThread(){
        thread(start = true) {
            var condition = true
            while(timeDifference < SECOND){

                timeDifference = System.currentTimeMillis() - timer
                distanceFromTouchedPoint = countDistance(points[0], currentThreadEvent)
                if(distanceFromTouchedPoint <radius - 30 || distanceFromTouchedPoint >radius + 30 || currentThreadEventAction == MotionEvent.ACTION_UP){
                    condition = false
                    break
                }
            }
            timeDifference = 0
            if(condition){
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(200)
                }
                singlePointMovementEnabled = true
            }
        }
    }

    private fun resetTouchEventActions(){
        pointTouched = -1
        isScreenTouched = false
        singlePointMovementEnabled = false
        cameraEventEnabled = false
        timer = 0
    }

    private fun resizeCircle(){
        radius = sqrt((currentThreadEvent.x - points[0].x).pow(2) +(currentThreadEvent.y - points[0].y).pow(2))
    }

    override fun findFigureMiddlePoint() {
        figureMiddlePoint = points[0]
        this.invalidate()
    }

}
