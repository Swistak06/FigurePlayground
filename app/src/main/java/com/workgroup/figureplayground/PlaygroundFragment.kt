package com.workgroup.figureplayground

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.workgroup.figureplayground.figure.CameraMode
import com.workgroup.figureplayground.figure.Figure
import com.workgroup.figureplayground.figure.d2.Triangle
import kotlinx.android.synthetic.main.fragment_playground.*
import kotlinx.android.synthetic.main.fragment_playground.view.*
import java.util.*

class PlaygroundFragment : Fragment() {

    private var moveEnabled = true
    private var rotateEnabled = false
    private var figure : Figure? = null

    private var listener: PlaygroundFrListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_playground, container, false)
        setBottomBarOnClickListeners(view)
        view.playgroundFrame.addView(figure)
        return view
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
        if (context is PlaygroundFrListener) {
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

        view.backBtn.setOnClickListener {
            listener?.backToMenuClick()
        }
        view.rotateBtn.setOnClickListener {
            rotateEnabled = true
            moveEnabled = false
            rotateBtn.setImageResource(IC_ROTATE_ENABLED)
            moveBtn.setImageResource(IC_MOVE_DISABLED)
            figure!!.cameraMode = CameraMode.ROTATE
            figure!!.findFigureMiddlePoint()
        }
        view.moveBtn.setOnClickListener {
            rotateEnabled = false
            moveEnabled = true
            rotateBtn.setImageResource(IC_ROTATE_DISABLED)
            moveBtn.setImageResource(IC_MOVE_ENABLED)
            figure!!.cameraMode = CameraMode.MOVE
            figure!!.deleteFigureMiddlePoint()
        }

    }

    fun setFigure(figure: Figure) {
        this.figure = figure
    }

    companion object {
        const val IC_MOVE_ENABLED = R.drawable.move_black_24
        const val IC_MOVE_DISABLED = R.drawable.move_white_24
        const val IC_ROTATE_ENABLED = R.drawable.rotate_black_24
        const val IC_ROTATE_DISABLED = R.drawable.rotate_white_24
    }

    interface PlaygroundFrListener {
        fun backToMenuClick()
    }
}
