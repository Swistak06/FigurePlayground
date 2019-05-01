package com.workgroup.figureplayground

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class TriangleCanvas(context: Context) : View(context) {


    var point13X = 0.25f*right
    var point13Y = 0.25f*bottom

    var point23X = 0.75f*right
    var point23Y = 0.25f*bottom

    var point31X = 0.5f*right
    var point31Y = 0.5f*bottom

    fun initValues(){
        point13X = 0.25f*right
        point13Y = 0.25f*bottom

        point23X = 0.75f*right
        point23Y = 0.25f*bottom

        point31X = 0.5f*right
        point31Y = 0.75f*bottom
    }



    override fun onDraw(canvas: Canvas) {
        val paint = Paint()
        if(point13X == 0f)
            initValues()
        paint.setARGB(255, 255, 0, 0)
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(point13X,point13Y,10f,paint)
        canvas.drawCircle(point23X,point23Y,10f,paint)
        canvas.drawCircle(point31X,point31Y,10f,paint)

        canvas.drawLine(point13X, point13Y,point23X,point23Y,paint)
        canvas.drawLine(point23X,point23Y,point31X,point31Y,paint)
        canvas.drawLine(point31X, point31Y, point13X,point13Y,paint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        var roznicaX = floatArrayOf(0f,0f,0f)
        var roznicaY = floatArrayOf(0f,0f,0f)

        roznicaX[0] = Math.abs(point13X - event!!.x)
        roznicaX[1] = Math.abs(point23X - event!!.x)
        roznicaX[2] = Math.abs(point31X - event!!.x)
        roznicaY[0] = Math.abs(point13Y - event!!.y)
        roznicaY[1] = Math.abs(point23Y - event!!.y)
        roznicaY[2] = Math.abs(point31Y - event!!.y)

        var roznicaXY = floatArrayOf(roznicaX[0]+ roznicaY[0],roznicaX[1]+roznicaY[1],roznicaX[2]+roznicaY[2])

        var minVal = minOf(roznicaXY[0],roznicaXY[1],roznicaXY[2])

        when(minVal){
            roznicaXY[0] -> {
                point13X = event!!.x
                point13Y = event!!.y
            }
            roznicaXY[1] -> {
                point23X = event!!.x
                point23Y = event!!.y
            }
            roznicaXY[2] -> {
                point31X = event!!.x
                point31Y = event!!.y
            }
        }
        this.invalidate()
        return true
        //return super.onTouchEvent(event)
    }
}


//val layout1 = findViewById(R.id.consLay) as android.support.constraint.ConstraintLayout
//val tr = TriangleCanvas(this)
//layout1.addView(tr)