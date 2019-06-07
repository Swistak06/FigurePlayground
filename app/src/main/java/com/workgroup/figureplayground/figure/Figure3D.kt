package com.workgroup.figureplayground.figure

import android.content.Context
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import java.util.ArrayList
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

open class Figure3D(context: Context) : View(context), Figure {

    enum class Rotation { X, Y, Z}

    var selectedRotation = Rotation.X
    protected val paint = Paint()
    protected var points: ArrayList<Vector> = ArrayList()
    protected var startingPoints: ArrayList<Vector> = ArrayList()
    private var startEventTouchPoint : Point = Point(0f, 0f)

    protected var xRot: Float = 0f
    protected var yRot: Float = 0f
    protected var zRot: Float = 0f

    protected var changedXRot: Float = 0f
    protected var changedYRot: Float = 0f
    protected var changedZRot: Float = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val eventPoint = Point(event!!.x, event.y)
        if(event!!.action == android.view.MotionEvent.ACTION_DOWN){
            startEventTouchPoint = eventPoint
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE){
            if(selectedRotation == Rotation.X) {
                val angle = (eventPoint.y - startEventTouchPoint.y) / height * Math.PI
                changedXRot = angle.toFloat()
            }
            else if(selectedRotation == Rotation.Y){
                val angle = (eventPoint.x - startEventTouchPoint.x) / width * Math.PI
                changedYRot = angle.toFloat()
            }
            else if(selectedRotation == Rotation.Z){

                val angle = findAngle(startEventTouchPoint, Point((0.5*width).toFloat(), (0.5*height).toFloat()), eventPoint)
                changedZRot = 2*angle.toFloat()
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

    protected fun rotateStartingPoints(x: Float, y: Float, z: Float){
        val rotMatrix = arrayOf(
            arrayOf(cos(y)*cos(z), -cos(x)*sin(z) + sin(x)*sin(y)*cos(z), sin(x)*sin(z) + cos(x)*sin(y)*cos(z)),
            arrayOf(cos(y)*sin(z), cos(x)*cos(z) + sin(x)*sin(y)*sin(z), -sin(x)*cos(z) + cos(x)*sin(y)*sin(z)),
            arrayOf(-sin(y), sin(x)*cos(y), cos(x)*cos(y))
        )
        startingPoints.forEach {
            val index = startingPoints.indexOf(it)
            points[index].x = rotMatrix[0][0] * (it.x-0.5*width).toFloat() + rotMatrix[1][0] * (it.y-0.5*height).toFloat() + rotMatrix[2][0] * it.z
            points[index].x += (0.5*width).toFloat()
            points[index].y = rotMatrix[0][1] * (it.x-0.5*width).toFloat() + rotMatrix[1][1] * (it.y-0.5*height).toFloat() + rotMatrix[2][1] * it.z
            points[index].y += (0.5*height).toFloat()
            points[index].z = rotMatrix[0][2] * (it.x-0.5*width).toFloat() + rotMatrix[1][2] * (it.y-0.5*height).toFloat() + rotMatrix[2][2] * it.z
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generatePoints()
    }
    protected open fun generatePoints(){}

    protected fun findAngle(startEventPoint: Point, figureMiddlePoint: Point?, eventPoint: Point): Double {
        val functionA = Point((startEventPoint.y - figureMiddlePoint!!.y) / (startEventPoint.x - figureMiddlePoint.x),
            -figureMiddlePoint.x *(startEventPoint.y - figureMiddlePoint.y) / (startEventPoint.x - figureMiddlePoint.x) + figureMiddlePoint.y)
        val functionB = Point((eventPoint.y - figureMiddlePoint.y) / (eventPoint.x - figureMiddlePoint.x),
            -figureMiddlePoint.x *(eventPoint.y - figureMiddlePoint.y) / (eventPoint.x - figureMiddlePoint.x) + figureMiddlePoint.y)

        return atan((functionA.x - functionB.x).toDouble()/(functionA.x * functionB.x + 1).toDouble())
    }

    private fun cos(angle: Float): Float{
        return Math.cos(angle.toDouble()).toFloat()
    }
    private fun sin(angle: Float): Float{
        return Math.sin(angle.toDouble()).toFloat()
    }
    protected fun calculateDistance(pointA :Point, pointB :Point): Float{
        return sqrt((pointA.x - pointB.x).pow(2) + (pointA.y - pointB.y).pow(2))
    }




}
