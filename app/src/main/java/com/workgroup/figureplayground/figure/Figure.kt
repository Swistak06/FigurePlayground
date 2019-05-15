package com.workgroup.figureplayground.figure

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Vibrator
import android.view.View
import java.util.ArrayList
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

open class Figure(context: Context) : View(context) {

    protected var points: ArrayList<Point> = ArrayList()
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var pointTouched = 0
    var timer = 0L
    protected val paint = Paint()

    var cameraMode: CameraMode = CameraMode.MOVE
    protected var figureMiddlePoint: Point? = null

    //X and Y of current touch
    protected var currentThreadEvent = Point(0f,0f)
    //Current touch type(move,up,dow etc)
    protected var currentThreadEventAction = 0
    //For thread calculations
    protected var distanceFromTouchedPoint = 0f
    protected var timeDifference = 0L

    protected var startCameraEventTouchPoint: Point = Point(0f,0f)
    protected var startCameraEventPoints = ArrayList<Point>()

    //If only single point is about to be moved
    protected  var singlePointMovementEnabled = false

    protected var cameraEventEnabled = false
    protected var isScreenTouched = false

    open fun findFigureMiddlePoint(){}
    open fun generatePoints(){}
    open fun drawInitialFigure(canvas : Canvas){}

    companion object {
        const val CIRCLE_RADIUS: Float = 30.0f
        const val SECOND: Int = 1000
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generatePoints()
        findFigureMiddlePoint()
    }

    protected open fun calculateDistancesFromPoint(point: Point): List<Float>{
        val distances = ArrayList<Float>()

        points.forEach {
            distances.add(countDistance(it, point))
        }
        return distances
    }

    protected fun countDistance(pointA :Point, pointB :Point): Float{
        return sqrt((pointA.x - pointB.x).pow(2) + (pointA.y - pointB.y).pow(2))
    }

    protected fun findAngle(startEventPoint: Point, figureMiddlePoint: Point?, eventPoint: Point): Double {
        val functionA = Point((startEventPoint.y - figureMiddlePoint!!.y) / (startEventPoint.x - figureMiddlePoint.x),
            -figureMiddlePoint.x *(startEventPoint.y - figureMiddlePoint.y) / (startEventPoint.x - figureMiddlePoint.x) + figureMiddlePoint.y)
        val functionB = Point((eventPoint.y - figureMiddlePoint.y) / (eventPoint.x - figureMiddlePoint.x),
            -figureMiddlePoint.x *(eventPoint.y - figureMiddlePoint.y) / (eventPoint.x - figureMiddlePoint.x) + figureMiddlePoint.y)

        return atan((functionA.x - functionB.x).toDouble()/(functionA.x * functionB.x + 1).toDouble())
    }



}
