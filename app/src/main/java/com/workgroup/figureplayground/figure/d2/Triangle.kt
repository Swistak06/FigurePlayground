package com.workgroup.figureplayground.figure.d2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.math.sqrt

class Triangle(context: Context) : View(context) {

    var isTouched = false
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var moveEnabled = false
    var pointTouched = 0;
    var timer = 0L
    var distance = 0f
    var diff = 0L
    var action = 0
    var eventX = 0f
    var eventY = 0f


    var poitsXValues = floatArrayOf(0.25f*right,0.75f*right,0.5f*right)
    var poitsYValues = floatArrayOf(0.25f*bottom,0.25f*bottom,0.5f*bottom)


    fun initValues(){
        poitsXValues[0] = 0.25f*right
        poitsYValues[0] = 0.25f*bottom

        poitsXValues[1] = 0.75f*right
        poitsYValues[1] = 0.25f*bottom

        poitsXValues[2] = 0.5f*right
        poitsYValues[2] = 0.75f*bottom
    }



    override fun onDraw(canvas: Canvas) {
        val paint = Paint()
        if(poitsXValues[0] == 0f)
            initValues()
        paint.setARGB(255, 255, 0, 0)
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(poitsXValues[0],poitsYValues[0],10f,paint)
        canvas.drawCircle(poitsXValues[1],poitsYValues[1],30f,paint)
        canvas.drawCircle(poitsXValues[2],poitsYValues[2],30f,paint)

        canvas.drawLine(poitsXValues[0], poitsYValues[0],poitsXValues[1],poitsYValues[1],paint)
        canvas.drawLine(poitsXValues[1],poitsYValues[1],poitsXValues[2],poitsYValues[2],paint)
        canvas.drawLine(poitsXValues[2], poitsYValues[2], poitsXValues[0],poitsYValues[0],paint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        eventX = event!!.x
        eventY = event.y
        action = event.action

        if(event.action == android.view.MotionEvent.ACTION_DOWN && !isTouched){
            val distances = floatArrayOf(0f,0f,0f)

            distances[0] = countDistance(poitsXValues[0] ,poitsYValues[0],event.x , event.y)
            distances[1] = countDistance(poitsXValues[1] ,poitsYValues[1],event.x , event.y)
            distances[2] = countDistance(poitsXValues[2] ,poitsYValues[2],event.x , event.y)

            val minVal = minOf(distances[0],distances[1],distances[2])


            if(minVal < 30){
                distance = minVal
                isTouched = true
                when(minVal){
                    distances[0] -> {

                        timer  = System.currentTimeMillis()
                        pointTouched = 1
                    }
                    distances[1] -> {
                        timer  = System.currentTimeMillis()
                        pointTouched = 2
                    }
                    distances[2] -> {
                        timer  = System.currentTimeMillis()
                        pointTouched = 3
                    }
                }
                thread(start = true) {
                    var condition = true
                    while(diff < 1000){
                        diff = System.currentTimeMillis() - timer
                        when(pointTouched){
                            1-> distance = countDistance(poitsXValues[0] ,poitsYValues[0],eventX ,eventY)
                            2-> distance = countDistance(poitsXValues[1] ,poitsYValues[1],eventX , eventY)
                            3-> distance = countDistance(poitsXValues[2] ,poitsYValues[2],eventX , eventY)
                        }
                        if(distance > 30 || action == android.view.MotionEvent.ACTION_UP){
                            condition = false
                            break
                        }
                    }
                    diff = 0
                    if(condition){
                        if (Build.VERSION.SDK_INT >= 26) {
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            vibrator.vibrate(200)
                        }
                        moveEnabled = true
                    }
                }
            }
        }
        else if(event.action == android.view.MotionEvent.ACTION_MOVE && moveEnabled){

            when(pointTouched){
                1 ->{
                    poitsXValues[0] = event.x
                    poitsYValues[0] = event.y
                }
                2->{
                    poitsXValues[1] = event.x
                    poitsYValues[1] = event.y
                }
                3->{
                    poitsXValues[2] = event.x
                    poitsYValues[2] = event.y
                }
            }
            this.invalidate()
        }
        else if(event.action == android.view.MotionEvent.ACTION_UP){
            pointTouched = 0
            isTouched = false
            moveEnabled = false
            timer = 0
        }
        return true
    }

    private fun countDistance(pointXA : Float, pointYA : Float, pointXB : Float, pointYB : Float): Float{
        return sqrt((pointXA - pointXB).pow(2) + (pointYA- pointYB).pow(2))
    }
}