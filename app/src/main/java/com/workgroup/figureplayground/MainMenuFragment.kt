package com.workgroup.figureplayground

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main_menu.view.*


class MainMenuFragment : Fragment() {

    private var listener: MainMenuFrListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)

        view.FlatFiguresBtn.setOnClickListener {
            listener?.showFiguresListView(0)
        }
        view.SolidFiguresBtn.setOnClickListener {
            listener?.showFiguresListView(1)
        }
        // Inflate the layout for this fragment
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainMenuFrListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement MainMenuFrListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface MainMenuFrListener {
        fun showFiguresListView(mode : Int)
    }

}
