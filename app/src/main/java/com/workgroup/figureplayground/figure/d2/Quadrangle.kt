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
import kotlin.math.cos
import kotlin.math.sin

class Quadrangle(context: Context) : Figure2D(context){

    override fun onDraw(canvas: Canvas) {
        if(figureMiddlePoint != null) {
            paint.setARGB(255, 0, 0, 255)
            canvas.drawCircle(figureMiddlePoint!!.x, figureMiddlePoint!!.y, CIRCLE_RADIUS / 3, paint)
        }
        drawInitialFigure(canvas)
    }

    init {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 5f
    }

    override fun generatePoints(){

        points = arrayListOf(
            Point(0.35f*width, 0.4f*height),
            Point(0.65f*width, 0.4f*height),
            Point(0.75f*width, 0.6f*height),
            Point(0.25f*width, 0.6f*height)
        )
        calculateFieldAndPerimeterOfFigure()
    }

    override fun drawInitialFigure(canvas : Canvas) {
        paint.setARGB(255, 255, 0, 0)
        canvas.drawCircle(points[0].x, points[0].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(points[1].x, points[1].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(points[2].x, points[2].y, CIRCLE_RADIUS, paint)
        canvas.drawCircle(points[3].x, points[3].y, CIRCLE_RADIUS, paint)

        paint.setARGB(255, 0, 0, 0)
        canvas.drawLine(points[0].x, points[0].y, points[1].x, points[1].y, paint)
        canvas.drawLine(points[1].x, points[1].y, points[2].x, points[2].y, paint)
        canvas.drawLine(points[2].x, points[2].y, points[3].x, points[3].y, paint)
        canvas.drawLine(points[3].x, points[3].y, points[0].x, points[0].y, paint)
    }

    private fun createPointLongPressEventThread(){
        thread(start = true) {
            var condition = true
            while(timeDifference < SECOND){

                timeDifference = System.currentTimeMillis() - timer
                distanceFromTouchedPoint = countDistance(points[pointTouched], currentThreadEvent)

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
        //Resize whole figure
        pointToMove.x = eventPoint.x
        pointToMove.y = eventPoint.y
        calculateFieldAndPerimeterOfFigure()
    }

    private fun resetTouchEventActions(){
        pointTouched = -1
        isScreenTouched = false
        singlePointMovementEnabled = false
        cameraEventEnabled = false
        timer = 0
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
                points.forEach {
                    startCameraEventPoints.add(Point(it.x, it.y))
                }
            }
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && cameraEventEnabled){
            if(cameraMode == CameraMode.MOVE) {
                moveAllPoints(startCameraEventTouchPoint, eventPoint)
                findFigureMiddlePoint()
            }
            else if(cameraMode == CameraMode.ROTATE) {
                rotateAllPoints(startCameraEventTouchPoint, eventPoint)
            }
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && singlePointMovementEnabled){
            moveSinglePoint(points[pointTouched], eventPoint)
            findFigureMiddlePoint()
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_UP){
            resetTouchEventActions()
            if(cameraMode == CameraMode.ROTATE)
                findFigureMiddlePoint()
        }
        return true
    }
    override fun findFigureMiddlePoint(){
        //find function coefficients using above points and opposed points, x is a and y is b
        val functionA = Point((points[3].y - points[1].y) / (points[3].x - points[1].x),
            -points[1].x *(points[3].y - points[1].y) / (points[3].x - points[1].x) + points[1].y)
        val functionB = Point((points[0].y - points[2].y) / (points[0].x - points[2].x),
            -points[2].x *(points[0].y - points[2].y) / (points[0].x - points[2].x) + points[2].y)

        //find the middle point
        figureMiddlePoint = Point((functionB.y - functionA.y) / (functionA.x - functionB.x),
            functionA.x * (functionB.y - functionA.y) / (functionA.x - functionB.x) + functionA.y)
        this.invalidate()
    }

    override fun calculateFieldAndPerimeterOfFigure() {
        perimeter = (countDistance(points[0],points[1]) + countDistance(points[1],points[2]) + countDistance(points[2],points[3]) + countDistance(points[3],points[0]))/100.0
        displayFieldAndPerimeterOfFigure(-1.0,perimeter)
    }
}
