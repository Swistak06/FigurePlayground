package com.workgroup.figureplayground

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_figure_list.view.*


class FigureListFragment : Fragment() {

    var mMode = 0
    var itemList = arrayOf(FigureListItem(R.drawable.flat_fig, "Triangle"))
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_figure_list, container, false)

        if(mMode == 0){
            itemList = arrayOf(FigureListItem(R.drawable.flat_fig, "Triangle"),FigureListItem(R.drawable.flat_fig, "Square")
                ,FigureListItem(R.drawable.flat_fig, "Rectangle"),FigureListItem(R.drawable.flat_fig, "Diamond"),FigureListItem(R.drawable.flat_fig, "Circle"))
        }
        else{
            itemList = arrayOf(FigureListItem(R.drawable.flat_fig, "Cube"),FigureListItem(R.drawable.flat_fig, "Sphere")
                ,FigureListItem(R.drawable.flat_fig, "Cone"),FigureListItem(R.drawable.flat_fig, "cuboid"),FigureListItem(R.drawable.flat_fig, "somhing"))
        }

        var listView = view.FlatFiguresList

        var arrayAdapter = FigureListAdapter(context!!,R.layout.adapter_view_layout, itemList)
        listView.adapter = arrayAdapter

        return view
    }

    public fun setMode( mode: Int){
        this.mMode = mode
    }

}
