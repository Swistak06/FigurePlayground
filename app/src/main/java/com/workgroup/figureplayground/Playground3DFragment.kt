package com.workgroup.figureplayground

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.workgroup.figureplayground.figure.Figure3D
import kotlinx.android.synthetic.main.fragment_playground3_d.*
import kotlinx.android.synthetic.main.fragment_playground3_d.view.*


class Playground3DFragment : Fragment() {


    private var figure : Figure3D? = null


    private var listener: Playground3DFrListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playground3_d, container, false)
        setBottomBarOnClickListeners(view)
        view.playgroundFrame3D.addView(figure)
        return view
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
        if (context is Playground3DFrListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement PlaygroundFrListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setBottomBarOnClickListeners(view: View){

        view.back3DBtn.setOnClickListener {
            listener?.backToMenuClick()
        }
        view.rotateXBtn.setOnClickListener {
            figure!!.selectedRotation = Figure3D.Rotation.X
            rotateXBtn.setImageResource(IC_ROTATE_X_ENABLED)
            rotateYBtn.setImageResource(IC_ROTATE_Y_DISABLED)
            rotateZBtn.setImageResource(IC_ROTATE_Z_DISABLED)
        }
        view.rotateYBtn.setOnClickListener {
            figure!!.selectedRotation = Figure3D.Rotation.Y
            rotateXBtn.setImageResource(IC_ROTATE_X_DISABLED)
            rotateYBtn.setImageResource(IC_ROTATE_Y_ENABLED)
            rotateZBtn.setImageResource(IC_ROTATE_Z_DISABLED)
        }
        view.rotateZBtn.setOnClickListener {
            figure!!.selectedRotation = Figure3D.Rotation.Z
            rotateXBtn.setImageResource(IC_ROTATE_X_DISABLED)
            rotateYBtn.setImageResource(IC_ROTATE_Y_DISABLED)
            rotateZBtn.setImageResource(IC_ROTATE_Z_ENABLED)
        }

    }

    fun setFigure(figure: Figure3D) {
        this.figure = figure
    }

    companion object {
        const val IC_ROTATE_X_ENABLED = R.drawable.x_icon_white
        const val IC_ROTATE_Y_ENABLED = R.drawable.y_icon_white
        const val IC_ROTATE_Z_ENABLED = R.drawable.z_icon_white
        const val IC_ROTATE_X_DISABLED = R.drawable.x_icon_black
        const val IC_ROTATE_Y_DISABLED = R.drawable.y_icon_black
        const val IC_ROTATE_Z_DISABLED = R.drawable.z_icon_black
    }

    interface Playground3DFrListener {
        fun backToMenuClick()
    }
}
