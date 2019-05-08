package com.workgroup.figureplayground.figure

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Vibrator
import android.view.View
import java.util.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt

open class Figure(context: Context) : View(context) {

    protected var points: ArrayList<Point> = ArrayList()
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var pointTouched = 0
    var timer = 0L
    protected val paint = Paint()
    protected var lines: List<Line> = ArrayList()



    //X and Y of current touch
    protected var currentThreadEventX = 0f
    protected var currentThreadEventY = 0f
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


    var cameraMode: CameraMode = CameraMode.MOVE
    protected var figureMiddlePoint: Point? = null

    open fun findFigureMiddlePoint(){}
    open fun generatePoints(){}

    fun deleteFigureMiddlePoint(){
        figureMiddlePoint = null
    }

    open fun drawInitialFigure(canvas : Canvas){}


    companion object {
        const val CIRCLE_RADIUS: Float = 30.0f
        const val SECOND: Int = 1000
    }

    protected fun calculateDistancesFromPoint(point: Point): List<Float>{
        val distances = ArrayList<Float>()

        points.forEach {
            distances.add(countDistance(it.x, it.y, point.x , point.y))
        }
        return distances
    }
    protected fun countDistance(pointXA : Float, pointYA : Float, pointXB : Float, pointYB : Float): Float{
        return sqrt((pointXA - pointXB).pow(2) + (pointYA- pointYB).pow(2))
    }



}
