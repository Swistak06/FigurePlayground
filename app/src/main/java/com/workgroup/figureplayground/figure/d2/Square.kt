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
import java.lang.Math.pow
import kotlin.concurrent.thread
import kotlin.math.*

class Square(context: Context) : Figure2D(context){

    var lineSize = 250f
    var angle : Double = 0.0

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
                findFigureMiddlePoint()
                startCameraEventTouchPoint = points[pointTouched]
                angle = findAngle(points[0], figureMiddlePoint!!, Point(0.25f*width, (0.5f*height)-lineSize))
            }
            else{
                cameraEventEnabled = true
                startCameraEventTouchPoint = eventPoint
            }
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && cameraEventEnabled){
            if(cameraMode == CameraMode.MOVE)
                moveAllPoints(startCameraEventTouchPoint, eventPoint)
            else if(cameraMode == CameraMode.ROTATE) {
                rotateAllPoints(findAngle(startCameraEventTouchPoint, figureMiddlePoint, eventPoint))
            }
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && singlePointMovementEnabled){
            resizeSquare(eventPoint)
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

    private fun rotateAllPoints(angle : Double) {

        for(i in 0 until points.size){
            val x = (startCameraEventPoints[i].x - figureMiddlePoint!!.x).toDouble()
            val y = (startCameraEventPoints[i].y - figureMiddlePoint!!.y).toDouble()

            points[i].x = ((x * cos(-2 * angle)) - (y * sin(-2 * angle))).toFloat() + figureMiddlePoint!!.x
            points[i].y = ((x * sin(-2 * angle)) + (y * cos(-2 * angle))).toFloat() + figureMiddlePoint!!.y
        }
    }

    private fun resizeSquare(eventPoint: Point) {

        val diffVector = Point(abs(eventPoint.x - figureMiddlePoint!!.x), abs(eventPoint.y - figureMiddlePoint!!.y))
        val diffVectorLenght = sqrt(pow(diffVector.x.toDouble(), 2.0) + pow(diffVector.y.toDouble(), 2.0))
        val diffFromMiddle = diffVectorLenght / sqrt(2.0)

        //left up
        points[0].x = figureMiddlePoint!!.x - diffFromMiddle.toFloat()
        points[0].y = figureMiddlePoint!!.y + diffFromMiddle.toFloat()
        //right up
        points[1].x = figureMiddlePoint!!.x + diffFromMiddle.toFloat()
        points[1].y = figureMiddlePoint!!.y + diffFromMiddle.toFloat()
        //right down
        points[2].x = figureMiddlePoint!!.x + diffFromMiddle.toFloat()
        points[2].y = figureMiddlePoint!!.y - diffFromMiddle.toFloat()
        //left down
        points[3].x = figureMiddlePoint!!.x - diffFromMiddle.toFloat()
        points[3].y = figureMiddlePoint!!.y - diffFromMiddle.toFloat()

        resizeSquareRotate(angle)
    }

    private fun resizeSquareRotate(angle : Double) {

        for(i in 0 until points.size){
            val x = (points[i].x - figureMiddlePoint!!.x).toDouble()
            val y = (points[i].y - figureMiddlePoint!!.y).toDouble()

            points[i].x = ((x * cos(angle)) - (y * sin(angle))).toFloat() + figureMiddlePoint!!.x
            points[i].y = ((x * sin(angle)) + (y * cos(angle))).toFloat() + figureMiddlePoint!!.y
        }
    }

    private fun findAngle(startEventPoint: Point, figureMiddlePoint: Point?, eventPoint: Point): Double {
        val functionA = Point((startEventPoint.y - figureMiddlePoint!!.y) / (startEventPoint.x - figureMiddlePoint.x),
            -figureMiddlePoint.x *(startEventPoint.y - figureMiddlePoint.y) / (startEventPoint.x - figureMiddlePoint.x) + figureMiddlePoint.y)
        val functionB = Point((eventPoint.y - figureMiddlePoint.y) / (eventPoint.x - figureMiddlePoint.x),
            -figureMiddlePoint.x *(eventPoint.y - figureMiddlePoint.y) / (eventPoint.x - figureMiddlePoint.x) + figureMiddlePoint.y)

        return atan((functionA.x - functionB.x).toDouble()/(functionA.x * functionB.x + 1).toDouble())

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

    private fun resetTouchEventActions(){
        pointTouched = -1
        isScreenTouched = false
        singlePointMovementEnabled = false
        cameraEventEnabled = false
        timer = 0
    }
    override fun generatePoints(){
        lineSize = 0.25f*width
        points = arrayListOf(
            Point(0.25f*width, (0.5f*height)-lineSize),
            Point(0.75f*width, (0.5f*height)-lineSize),
            Point(0.75f*width, (0.5f*height)+lineSize),
            Point(0.25f*width, (0.5f*height)+lineSize)
        )
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generatePoints()
    }

    override fun findFigureMiddlePoint(){
        val midXLine = abs(points[0].x - points[2].x)/2
        val midYLine = abs(points[0].y - points[2].y)/2
        figureMiddlePoint = Point(min(points[0].x ,points[2].x)+midXLine,min(points[0].y ,points[2].y)+midYLine)
        this.invalidate()
    }
}
