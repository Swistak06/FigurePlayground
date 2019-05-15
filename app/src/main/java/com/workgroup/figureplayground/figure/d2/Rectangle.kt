package com.workgroup.figureplayground.figure.d2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.VibrationEffect
import android.util.Log
import android.view.MotionEvent
import com.workgroup.figureplayground.figure.CameraMode
import com.workgroup.figureplayground.figure.Figure
import com.workgroup.figureplayground.figure.Point
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Rectangle(context: Context) : Figure(context){

    var angle : Double = 0.0
    var middlePoints: ArrayList<Point> = ArrayList()
    var sideLeftCorner = Point(0f, 0f)
    var sideRightCorner = Point(0f, 0f)

    init {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        if(figureMiddlePoint != null) {
            paint.setARGB(255, 0, 0, 255)
            canvas.drawCircle(figureMiddlePoint!!.x, figureMiddlePoint!!.y, CIRCLE_RADIUS / 3, paint)
        }
        drawInitialFigure(canvas)
    }

    override fun generatePoints(){

        points = arrayListOf(
            Point(0.25f*width, 0.4f*height),
            Point(0.75f*width, 0.4f*height),
            Point(0.75f*width, 0.6f*height),
            Point(0.25f*width, 0.6f*height)
        )
        generateMiddlePoints()
    }

    private fun generateMiddlePoints(){
        middlePoints = arrayListOf(
            Point((points[0].x + points[1].x)/2, (points[0].y + points[1].y)/2),
            Point((points[1].x + points[2].x)/2, (points[1].y + points[2].y)/2),
            Point((points[2].x + points[3].x)/2, (points[2].y + points[3].y)/2),
            Point((points[3].x + points[0].x)/2, (points[3].y + points[0].y)/2)
        )
    }

    override fun drawInitialFigure(canvas : Canvas) {
        paint.setARGB(255, 255, 0, 0)
        canvas.drawCircle(middlePoints[0].x, middlePoints[0].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(middlePoints[1].x, middlePoints[1].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(middlePoints[2].x, middlePoints[2].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(middlePoints[3].x, middlePoints[3].y, CIRCLE_RADIUS, paint)

        paint.setARGB(255, 0, 0, 0)
        canvas.drawLine(points[0].x, points[0].y, points[1].x, points[1].y, paint)
        canvas.drawLine(points[1].x, points[1].y, points[2].x, points[2].y, paint)
        canvas.drawLine(points[2].x, points[2].y, points[3].x, points[3].y, paint)
        canvas.drawLine(points[3].x, points[3].y, points[0].x, points[0].y, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        currentThreadEvent.x = event!!.x
        currentThreadEvent.y = event.y
        currentThreadEventAction = event.action

        val eventPoint = Point(event.x, event.y)

        //If touched without holding
        if(event.action == android.view.MotionEvent.ACTION_DOWN && !isScreenTouched){

            val distances = calculateDistancesFromPoint(eventPoint)
            val minVal = distances.min()!!

            startCameraEventPoints = ArrayList()
            for(i in 0 until points.size){
                startCameraEventPoints.add(Point(points[i].x, points[i].y))
            }

            isScreenTouched = true
            if(minVal < 1.2 * CIRCLE_RADIUS){
                distanceFromTouchedPoint = minVal
                timer  = System.currentTimeMillis()
                pointTouched = distances.indexOf(minVal)
                createPointLongPressEventThread()
                startCameraEventTouchPoint = points[pointTouched]
                angle = findAngle(points[0], figureMiddlePoint!!, Point(0.25f*width, 0.4f*height))
                findSideCorners(pointTouched)
            }
            else{
                cameraEventEnabled = true
                startCameraEventTouchPoint = eventPoint
            }
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && cameraEventEnabled){
            if(cameraMode == CameraMode.MOVE) {
                moveAllPoints(startCameraEventTouchPoint, eventPoint)
                findFigureMiddlePoint()
            }
            else if(cameraMode == CameraMode.ROTATE) {
                rotateAllPoints(findAngle(startCameraEventTouchPoint, figureMiddlePoint, eventPoint))
            }
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && singlePointMovementEnabled){
            moveRectangleSide(middlePoints[pointTouched], eventPoint)
            generateMiddlePoints()
            findFigureMiddlePoint()
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_UP){
            resetTouchEventActions()
            findFigureMiddlePoint()
        }
        return true
    }

    private fun createPointLongPressEventThread(){
        thread(start = true) {
            var condition = true
            while(timeDifference < SECOND){

                timeDifference = System.currentTimeMillis() - timer
                distanceFromTouchedPoint = countDistance(middlePoints[pointTouched], currentThreadEvent)

                if(distanceFromTouchedPoint > 1.2 * CIRCLE_RADIUS || currentThreadEventAction == MotionEvent.ACTION_UP){
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

    private fun findSideCorners(pointTouched: Int) {
        when(pointTouched){
            1 ->{
                sideLeftCorner = points[1]
                sideRightCorner = points[2]
            }
            2 ->{
                sideLeftCorner = points[2]
                sideRightCorner = points[3]
            }
            3 ->{
                sideLeftCorner = points[3]
                sideRightCorner = points[0]
            }
            else -> { // for 0 also
                sideLeftCorner = points[0]
                sideRightCorner = points[1]
            }
        }
    }

    private fun moveAllPoints(startMovementPoint: Point, eventPoint: Point) {
        val xDiff = eventPoint.x - startMovementPoint.x
        val yDiff = eventPoint.y - startMovementPoint.y

        points.forEach {
            val index = points.indexOf(it)
            it.x = startCameraEventPoints[index].x + xDiff
            it.y = startCameraEventPoints[index].y + yDiff
        }
        generateMiddlePoints()
    }

    private fun rotateAllPoints(angle : Double) {

        for(i in 0 until points.size){
            val x = (startCameraEventPoints[i].x - figureMiddlePoint!!.x).toDouble()
            val y = (startCameraEventPoints[i].y - figureMiddlePoint!!.y).toDouble()

            points[i].x = ((x * cos(-2 * angle)) - (y * sin(-2 * angle))).toFloat() + figureMiddlePoint!!.x
            points[i].y = ((x * sin(-2 * angle)) + (y * cos(-2 * angle))).toFloat() + figureMiddlePoint!!.y

            middlePoints[i].x = ((x * cos(-2 * angle)) - (y * sin(-2 * angle))).toFloat() + figureMiddlePoint!!.x
            middlePoints[i].y = ((x * sin(-2 * angle)) + (y * cos(-2 * angle))).toFloat() + figureMiddlePoint!!.y
        }
        generateMiddlePoints()
    }

    private fun moveRectangleSide(point: Point, eventPoint: Point) {
        //if x or y of point is the same as figure middle point
        if(point.x == figureMiddlePoint!!.x){
            val diff = eventPoint.y - point.y
            point.y = eventPoint.y
            sideLeftCorner.y += diff
            sideRightCorner.y += diff
        }
        else if(point.y == figureMiddlePoint!!.y){
            val diff = eventPoint.x - point.x
            point.x = eventPoint.x
            sideLeftCorner.x += diff
            sideRightCorner.x += diff
        }
        //calculate perpendicular function to function of points point and middle and find cross point of these two
        else{
            val functionPointToMiddle = Point((point.y - figureMiddlePoint!!.y) / (point.x - figureMiddlePoint!!.x),
                -figureMiddlePoint!!.x *(point.y - figureMiddlePoint!!.y) / (point.x - figureMiddlePoint!!.x) + figureMiddlePoint!!.y)
            val functionPerpendicular = Point(-1/functionPointToMiddle.x, 0f)
            functionPerpendicular.y = -functionPerpendicular.x * eventPoint.x + eventPoint.y
            val crossPoint = Point((functionPointToMiddle.y - functionPerpendicular.y)/(functionPerpendicular.x - functionPointToMiddle.x),
                0f)
            crossPoint.y = functionPerpendicular.x * crossPoint.x + functionPerpendicular.y
            val moveVector = Point(crossPoint.x - point.x, crossPoint.y - point.y)
            point.x = crossPoint.x
            point.y = crossPoint.y

            sideLeftCorner.x += moveVector.x
            sideLeftCorner.y += moveVector.y

            sideRightCorner.x += moveVector.x
            sideRightCorner.y += moveVector.y
        }

    }

    override fun findFigureMiddlePoint(){
        val midXLine = abs(points[0].x - points[2].x) /2
        val midYLine = abs(points[0].y - points[2].y) /2
        figureMiddlePoint = Point(min(points[0].x ,points[2].x) +midXLine, min(points[0].y ,points[2].y) +midYLine)
        this.invalidate()
    }

    override fun calculateDistancesFromPoint(point: Point): List<Float>{
        val distances = java.util.ArrayList<Float>()

        middlePoints.forEach {
            distances.add(countDistance(it, point))
        }
        return distances
    }

    private fun resetTouchEventActions(){
        pointTouched = -1
        isScreenTouched = false
        singlePointMovementEnabled = false
        cameraEventEnabled = false
        timer = 0
    }
}
