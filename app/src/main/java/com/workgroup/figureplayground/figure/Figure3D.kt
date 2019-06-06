package com.workgroup.figureplayground.figure

import android.content.Context
import android.graphics.Paint
import android.view.View
import java.util.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt

open class Figure3D(context: Context) : View(context), Figure {

    enum class Rotation { X, Y, Z}

    var selectedRotation = Rotation.X
    protected var points: ArrayList<Vector> = ArrayList()
    protected var startingPoints: ArrayList<Vector> = ArrayList()
    protected val paint = Paint()
    protected var startEventTouchPoint : Point = Point(0f, 0f)

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
