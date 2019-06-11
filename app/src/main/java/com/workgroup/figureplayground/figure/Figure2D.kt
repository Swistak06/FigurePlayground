package com.workgroup.figureplayground.figure

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import com.workgroup.figureplayground.PlaygroundFragment
import kotlinx.android.synthetic.main.fragment_playground.*
import org.w3c.dom.Text
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.ArrayList
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

open class Figure2D(context: Context) : View(context), Figure {

    protected var points: ArrayList<Point> = ArrayList()
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var pointTouched = 0
    var timer = 0L
    protected val paint = Paint()
    var field = 0.0
    var perimeter = 0.0
    var fieldTV : TextView? = null
    var perimeterTV : TextView? = null


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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generatePoints()
        findFigureMiddlePoint()
    }
    protected fun findAngle(startEventPoint: Point, figureMiddlePoint: Point?, eventPoint: Point): Double {
        val functionA = Point((startEventPoint.y - figureMiddlePoint!!.y) / (startEventPoint.x - figureMiddlePoint.x),
            -figureMiddlePoint.x *(startEventPoint.y - figureMiddlePoint.y) / (startEventPoint.x - figureMiddlePoint.x) + figureMiddlePoint.y)
        val functionB = Point((eventPoint.y - figureMiddlePoint.y) / (eventPoint.x - figureMiddlePoint.x),
            -figureMiddlePoint.x *(eventPoint.y - figureMiddlePoint.y) / (eventPoint.x - figureMiddlePoint.x) + figureMiddlePoint.y)

        return atan((functionA.x - functionB.x).toDouble()/(functionA.x * functionB.x + 1).toDouble())
    }
    protected open fun calculateFieldAndPerimeterOfFigure(){}
    protected fun displayFieldAndPerimeterOfFigure(field : Double, perimeter : Double){
        if(field < 0)
            fieldTV!!.text = ""
        else
            fieldTV!!.text = "Field: " + roundToThreeDecimals(field)
        perimeterTV!!.text = "Perimeter: " + roundToThreeDecimals(perimeter)
    }

    fun setFieldandPerimeterTV(fieldTV : TextView, perimeterTV : TextView){
        this.fieldTV = fieldTV
        this.perimeterTV = perimeterTV
    }

    fun roundToThreeDecimals(roundedNumber: Double):String{
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING

        return df.format(roundedNumber)
    }
}
