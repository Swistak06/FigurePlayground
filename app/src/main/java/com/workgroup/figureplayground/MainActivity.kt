package com.workgroup.figureplayground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(),MainMenuFragment.MainMenuFragmentListener {


    private val manager: FragmentManager = supportFragmentManager
    private val mainMenuFragment = MainMenuFragment()


    override fun showFiguresListView(mode: Int) {
        val figureListFragment = FigureListFragment()
        figureListFragment.mMode = mode
        val transaction = manager.beginTransaction()
        transaction.add(R.id.main_frame, figureListFragment)
        transaction.replace(R.id.main_frame, figureListFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = manager.beginTransaction()
        transaction.add(R.id.main_frame, mainMenuFragment)
        transaction.replace(R.id.main_frame, mainMenuFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
