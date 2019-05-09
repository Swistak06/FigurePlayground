package com.workgroup.figureplayground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.workgroup.figureplayground.figure.Figure
import com.workgroup.figureplayground.figure.d2.*

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

        val figure = selectFigureByModeAndPosition(mode, position)
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT)
        figure.layoutParams = params
        playgroundFragment.setFigure(figure)

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

    private fun selectFigureByModeAndPosition(mode: Int, position: Int): Figure{

        if(mode == 0){
            return when(position){
                1 -> Square(this)
                2 -> Rectangle(this)
                3 -> Quadrangle(this)
                4 -> Circle(this)
                else -> Triangle(this)
            }
        }
        else{
            return when(position){
                //TODO create 3d figured and change this
                1 -> Triangle(this)
                2 -> Triangle(this)
                3 -> Triangle(this)
                4 -> Triangle(this)
                else -> Triangle(this)
            }
        }
    }


}
