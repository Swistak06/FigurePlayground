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
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.*

class Triangle(context: Context) : Figure2D(context) {

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

    override fun drawInitialFigure(canvas : Canvas) {
        paint.setARGB(255, 255, 0, 0)
        canvas.drawCircle(points[0].x, points[0].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(points[1].x, points[1].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(points[2].x, points[2].y, CIRCLE_RADIUS, paint)

        paint.setARGB(255, 0, 0, 0)
        canvas.drawLine(points[0].x, points[0].y, points[1].x, points[1].y, paint)
        canvas.drawLine(points[1].x, points[1].y, points[2].x, points[2].y, paint)
        canvas.drawLine(points[2].x, points[2].y, points[0].x, points[0].y, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        currentThreadEvent.x = event!!.x
        currentThreadEvent.y = event.y
        currentThreadEventAction = event.action

        val eventPoint = Point(event.x, event.y)

        if(event.action == android.view.MotionEvent.ACTION_DOWN && !isScreenTouched){

            val distances = calculateDistancesFromPoint(eventPoint)
            val minVal = distances.min()!!

            isScreenTouched = true
            if(minVal < 1.2 * CIRCLE_RADIUS){
                distanceFromTouchedPoint = minVal
                timer  = System.currentTimeMillis()
                pointTouched = distances.indexOf(minVal)
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
            if(cameraMode == CameraMode.MOVE) {
                moveAllPoints(startCameraEventTouchPoint, eventPoint)
                findFigureMiddlePoint()
            }
            else if(cameraMode == CameraMode.ROTATE)
                rotateAllPoints(startCameraEventTouchPoint, eventPoint)
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && singlePointMovementEnabled){
            moveSinglePoint(points[pointTouched], eventPoint)
            findFigureMiddlePoint()
            calculateFieldAndPerimeterOfFigure()
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
                distanceFromTouchedPoint = countDistance(points[pointTouched], currentThreadEvent)

                if(distanceFromTouchedPoint > 1.2 * CIRCLE_RADIUS || currentThreadEventAction == android.view.MotionEvent.ACTION_UP){
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

    private fun moveAllPoints(startMovementPoint: Point, eventPoint: Point) {
        val xDiff = eventPoint.x - startMovementPoint.x
        val yDiff = eventPoint.y - startMovementPoint.y

        points.forEach {
            val index = points.indexOf(it)
            it.x = startCameraEventPoints[index].x + xDiff
            it.y = startCameraEventPoints[index].y + yDiff
        }
    }

    private fun rotateAllPoints(startEventPoint: Point, eventPoint: Point) {

        val angle = findAngle(startEventPoint, figureMiddlePoint, eventPoint)
        for(i in 0 until points.size){

            val x = (startCameraEventPoints[i].x - figureMiddlePoint!!.x).toDouble()
            val y = (startCameraEventPoints[i].y - figureMiddlePoint!!.y).toDouble()

            points[i].x = ((x * cos(-2 * angle)) - (y * sin(-2 * angle))).toFloat() + figureMiddlePoint!!.x
            points[i].y = ((x * sin(-2 * angle)) + (y * cos(-2 * angle))).toFloat() + figureMiddlePoint!!.y
        }
    }

    private fun moveSinglePoint(pointToMove: Point, eventPoint: Point) {
        pointToMove.x = eventPoint.x
        pointToMove.y = eventPoint.y
    }

    override fun findFigureMiddlePoint(){
        //find 2 points in the middle of 2 different lines
        val dPoint = Point((points[0].x + points[2].x)/2, (points[0].y + points[2].y)/2)
        val ePoint = Point((points[0].x + points[1].x)/2, (points[0].y + points[1].y)/2)

        //find function coefficients using above points and opposed points, x is a and y is b
        val functionA = Point((dPoint.y - points[1].y) / (dPoint.x - points[1].x),
            -points[1].x *(dPoint.y - points[1].y) / (dPoint.x - points[1].x) + points[1].y)
        val functionB = Point((ePoint.y - points[2].y) / (ePoint.x - points[2].x),
            -points[2].x *(ePoint.y - points[2].y) / (ePoint.x - points[2].x) + points[2].y)

        //find the middle point
        figureMiddlePoint = Point((functionB.y - functionA.y) / (functionA.x - functionB.x),
            functionA.x * (functionB.y - functionA.y) / (functionA.x - functionB.x) + functionA.y)
        this.invalidate()
    }

    override fun generatePoints(){
        points = arrayListOf(Point(0.5f*width, 0.25f*height),
            Point(0.25f*width, 0.75f*height),
            Point(0.75f*width, 0.75f*height))
        calculateFieldAndPerimeterOfFigure()
    }

    private fun resetTouchEventActions(){
        pointTouched = -1
        isScreenTouched = false
        singlePointMovementEnabled = false
        cameraEventEnabled = false
        timer = 0
    }

    override fun calculateFieldAndPerimeterOfFigure() {
        var line02 = countDistance(points[2],points[0])
        var line12 = countDistance(points[1],points[2])
        val line01 = countDistance(points[0],points[1])
        field = (0.5 * Math.sin(calculateAngleBetweenLines()) * line02 * line12)/10000
        perimeter = (line01+ line12 + line02)/100.0
        displayFieldAndPerimeterOfFigure(field,perimeter)
    }

    fun calculateAngleBetweenLines() : Double{
        var vecAX = points[0].x - points[2].x
        var vecAY = points[0].y - points[2].y
        var vecBX = points[1].x - points[2].x
        var vecBY = points[1].y - points[2].y

        var ilSkal = vecAX * vecBX + vecAY * vecBY
        var ilDL = sqrt((vecAX * vecAX) + (vecAY * vecAY)) * sqrt((vecBX * vecBX) + (vecBY * vecBY))

        var cosi = ilSkal/ilDL
        return Math.acos(cosi .toDouble())
    }
}