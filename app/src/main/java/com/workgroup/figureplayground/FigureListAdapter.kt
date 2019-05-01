package com.workgroup.figureplayground


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.adapter_view_layout.view.*

class FigureListAdapter(context : Context,resource : Int, objects : Array<FigureListItem>) : ArrayAdapter<FigureListItem>(context,resource,objects )  {

    val res = resource
    private val con = context



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageID = getItem(position).imageId
        val figureName = getItem(position).figureName

        val inflater = LayoutInflater.from(con)
        val convertView2 = inflater.inflate(res, parent,false)

        val imageTV = convertView2.LeftSide
        val typeTV = convertView2.RightSide

        imageTV.setImageResource(imageID)
        typeTV.text = figureName


        return convertView2
    }
}