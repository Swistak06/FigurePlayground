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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_figure_list, container, false)

        val listItems = arrayOf(FigureListItem(R.drawable.flat_fig, "Triangle"),FigureListItem(R.drawable.flat_fig, "Square")
            ,FigureListItem(R.drawable.flat_fig, "Item 3"))

        var listView = view.FlatFiguresList

        var arrayAdapter = FigureListAdapter(context!!,R.layout.adapter_view_layout, listItems)
        listView.adapter = arrayAdapter

        return view
    }
}
