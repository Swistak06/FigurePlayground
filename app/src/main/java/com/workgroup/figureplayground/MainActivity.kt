package com.workgroup.figureplayground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(),MainMenuFragment.MainMenuFrListener, PlaygroundFragment.PlaygroundFrListener,
    FigureListFragment.FigureListFrListener {

    private val manager: FragmentManager = supportFragmentManager
    private val mainMenuFragment = MainMenuFragment()
    private val figureListFragment = FigureListFragment()
    private val playgroundFragment = PlaygroundFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val transaction = manager.beginTransaction()

        transaction.add(R.id.main_frame, mainMenuFragment)
        transaction.add(R.id.main_frame, figureListFragment)
        transaction.add(R.id.main_frame, playgroundFragment)

        transaction.replace(R.id.main_frame, mainMenuFragment)
        transaction.commit()
    }

    override fun showFiguresListView(mode: Int) {

        figureListFragment.mMode = mode
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.main_frame, figureListFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun listItemSelected(mode: Int, position: Int) {

        //send figure to fragment and draw it before showing the fragment

        val transaction = manager.beginTransaction()
        transaction.replace(R.id.main_frame, playgroundFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun backToMenuClick() {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.main_frame, mainMenuFragment)
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.commit()
    }


}
