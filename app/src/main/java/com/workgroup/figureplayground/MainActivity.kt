package com.workgroup.figureplayground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.workgroup.figureplayground.figure.Figure
import com.workgroup.figureplayground.figure.Figure2D
import com.workgroup.figureplayground.figure.Figure3D
import com.workgroup.figureplayground.figure.d2.*
import com.workgroup.figureplayground.figure.d3.Cube

class MainActivity : AppCompatActivity(),MainMenuFragment.MainMenuFrListener, PlaygroundFragment.PlaygroundFrListener,
    FigureListFragment.FigureListFrListener, Playground3DFragment.Playground3DFrListener {

    private val manager: FragmentManager = supportFragmentManager
    private val mainMenuFragment = MainMenuFragment()
    private val figureListFragment = FigureListFragment()
    private val playgroundFragment = PlaygroundFragment()
    private val playgroundFragment3D = Playground3DFragment()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val transaction = manager.beginTransaction()

        transaction.add(R.id.main_frame, mainMenuFragment)
        transaction.add(R.id.main_frame, figureListFragment)
        transaction.add(R.id.main_frame, playgroundFragment)
        transaction.add(R.id.main_frame, playgroundFragment3D)

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

        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT)
        val transaction = manager.beginTransaction()

        val figure = selectFigureByModeAndPosition(mode, position)
        if(figure is Figure2D){
            figure.layoutParams = params
            playgroundFragment.setFigure(figure)
            transaction.replace(R.id.main_frame, playgroundFragment)
        }
        else if(figure is Figure3D){
            figure.layoutParams = params
            playgroundFragment3D.setFigure(figure)
            transaction.replace(R.id.main_frame, playgroundFragment3D)
        }

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
                else -> Cube(this)
            }
        }
    }


}
