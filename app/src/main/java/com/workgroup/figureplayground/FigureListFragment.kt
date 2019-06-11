package com.workgroup.figureplayground

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_figure_list.view.*


class FigureListFragment : Fragment() {

    private var listener: FigureListFrListener? = null

    var mMode = 0
    private var itemList = arrayOf(FigureListItem(R.drawable.flat_fig, "Triangle"))
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_figure_list, container, false)

        if(mMode == 0){
            itemList = arrayOf(FigureListItem(R.drawable.triangle, "Triangle"),FigureListItem(R.drawable.square, "Square")
                ,FigureListItem(R.drawable.rectangle, "Rectangle"),FigureListItem(R.drawable.diamond, "Quadrangle"),FigureListItem(R.drawable.circle, "Circle"))
        }
        else{
            itemList = arrayOf(FigureListItem(R.drawable.cube, "Cube"),FigureListItem(R.drawable.pyramid, "Pyramid"),
                FigureListItem(R.drawable.cuboid, "Cuboid"), FigureListItem(R.drawable.sphere, "Sphere"),
                FigureListItem(R.drawable.cone, "Cone"))
        }

        var listView = view.FlatFiguresList

        listView.setOnItemClickListener { _, _, position, _ ->
            run {
                listener?.listItemSelected(mMode, position)
            }
        }

        var arrayAdapter = FigureListAdapter(context!!,R.layout.adapter_view_layout, itemList)
        listView.adapter = arrayAdapter

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FigureListFrListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement FigureListFrListener")
        }
    }

    public fun setMode( mode: Int){
        this.mMode = mode
    }

    interface FigureListFrListener{
        fun listItemSelected(mode: Int, position: Int)
    }

}
