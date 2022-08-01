package com.example.mobiledamalegends

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.ancestors
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobiledamalegends.databinding.FragmentGameBinding
import java.util.*
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    var image_focused : ImageView? = null

    companion object {
        // piece height is 39dp
        val dip = 39f
        lateinit var r: Resources
        var px by Delegates.notNull<Int>() // piece size in px
        var brd by Delegates.notNull<Int>() // board size in px
        var chp by Delegates.notNull<Float>() // chop (1/8th) off the board
        var ofs by Delegates.notNull<Float>() // piece to chop offset

        //for moving pieces on drag:
        var xCoOrdinate = 0f
        var yCoOrdinate = 0f

        var from_pos = 0
        var to_pos = 0

        var white_playing = true

        val white_image = R.drawable.whitedamapiece
        val black_image = R.drawable.blackdamapiece

        val pop_up = 20
        val pop_half = pop_up.toFloat()/2
    }

    enum class TileState {blank, white, black, lit}

    data class TileContent (var tile_state:TileState) {
        public var clicked = false
    }

    //    --28--29--30--31
    //    24--25--26--27--
    //    --20--21--22--23
    //    16--17--18--19--
    //    --12--13--14--15
    //    08--09--10--11--
    //    --04--05--06--07
    //    00--01--02--03--

    var piece_map = Array <TileContent> (32) {TileContent(TileState.blank)}.toMutableList()

    val piece_path = arrayListOf <IntArray> (
        intArrayOf( 4         ),intArrayOf( 4, 5      ),intArrayOf(5, 6       ),intArrayOf( 6, 7      ),
        intArrayOf( 0, 1, 8, 9),intArrayOf( 1, 2, 9,10),intArrayOf( 2, 3,10,11),intArrayOf( 3,11      ),
        intArrayOf( 4,12      ),intArrayOf( 4, 5,12,13),intArrayOf( 5, 6,13,14),intArrayOf( 6, 7,14,15),
        intArrayOf( 8, 9,16,17),intArrayOf( 9,10,17,18),intArrayOf(10,11,18,19),intArrayOf(11,19      ),
        intArrayOf(12,20      ),intArrayOf(12,13,20,21),intArrayOf(13,14,21,22),intArrayOf(14,15,22,23),
        intArrayOf(16,17,24,25),intArrayOf(17,18,25,26),intArrayOf(18,19,26,27),intArrayOf(19,27      ),
        intArrayOf(20,28      ),intArrayOf(20,21,28,29),intArrayOf(21,22,29,30),intArrayOf(22,23,30,31),
        intArrayOf(24,25      ),intArrayOf(25,26      ),intArrayOf(26,27      ),intArrayOf(27         )
    )

    fun do_move() {
        val coOrd = pos_to_coOrd(to_pos)
        image_focused?.animate()?.x(coOrd[0])?.y(coOrd[1])?.setDuration(0)
        Collections.swap(piece_map, from_pos, to_pos)
        for(y in 0..7) {
            for (x in 0..7)
                print(
                    if ((x%2==0) == ((7-y)%2==0))
                        when (piece_map[(x+(7-y)*8)/2].tile_state)  {
                            TileState.white -> "WW"
                            TileState.black -> "BB"
                            else -> "**"
                        }
                    else "--"
                )
            println()
        }
    }

    fun do_eat(arr: IntArray) {
        for (i in arr){
            put_state(i, TileState.blank)
//            piece_map[i].tile_image?.setVisibility(View.GONE)
        }
    }

//    converts board position (0-31) to coOrds on screen
    fun pos_to_coOrd(_pos: Int): FloatArray {
        val pos = if(!white_playing) 31-_pos else _pos
        return floatArrayOf (chp*((pos%4)*2+(pos/4)%2)+ofs, chp*(7-pos/4)+ofs)
    }

    fun coOrd_to_pos(x: Float, y: Float): Int{
        val clip = {i: Int -> if(i<0) 0 else if(i>7) 7 else i}

        var y_component = clip( ((brd-y)/chp-.5).toInt() )
        println("y_component: ${y_component}")

        var x_component = clip( (x/chp+.5).toInt() )
        println("x_component: ${x_component}")

        return if((x_component%2==0) == (y_component%2==0))
                (x_component + y_component*8)/2
        else from_pos
    }

    val put_state = {i: Int, t: TileState -> piece_map.set(i, TileContent(t))}
    val put_image = {r: Int -> image_focused?.setImageResource(r)}

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_GameFragment_to_MenuFragment)
        }

        r = resources
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,r.displayMetrics).toInt()
        brd = binding.gameFrame.layoutParams.width
        chp = (brd.toFloat()/8)
        ofs = ((brd.toFloat()/8-px)/2)

        for(i in 0..11)  put_state(i, TileState.white)
        for(i in 20..31) put_state(i, TileState.black)

        for (i in 0..31) {
            var state = piece_map[i].tile_state

            if (state != TileState.blank){
                image_focused = ImageView(this.context)
                when (state) {
                    TileState.white -> put_image(white_image)
                    TileState.black -> put_image(black_image)
                    else -> {}
                }
            }
            else image_focused = null

            if (image_focused != null) {
                val p= {_i: Int -> RelativeLayout.LayoutParams(px+_i, px+_i)}

                // set size, then location
                image_focused!!.setLayoutParams(p(0))
                val coOrd = pos_to_coOrd(i)
                image_focused!!.animate().x(coOrd[0].toFloat()).y(coOrd[1].toFloat()).setDuration(0)

                image_focused!!.setOnTouchListener(OnTouchListener { v, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            image_focused = v as ImageView
                            from_pos = coOrd_to_pos(v.x, v.y)
                            println("from_pos: ${from_pos}")
                            v.z = 1f
                            v.setLayoutParams(p(pop_up))
                            v.animate().x(v.x-pop_half).y(v.y-pop_half).z(1f)
                            xCoOrdinate = v.x - event.rawX - pop_half
                            yCoOrdinate = v.y - event.rawY - pop_half
                        }
                        MotionEvent.ACTION_MOVE -> v.animate().x(event.rawX + xCoOrdinate)
                            .y(event.rawY + yCoOrdinate).setDuration(0).start()
                        MotionEvent.ACTION_UP -> {
                            to_pos = coOrd_to_pos(v.x, v.y)
                            println("to_pos: ${to_pos}")
                            v.setLayoutParams(p(0))
                            v.z = 0f
                            do_move()
                            println("the thing moved!")
                        }
                        else -> return@OnTouchListener false
                    }
                    true
                })

                binding.damaField.addView(image_focused)
            }
        }

//        Handler(Looper.getMainLooper()).postDelayed({
//            from_pos = 8
//            to_pos = 12
//            do_move()
//        }, 1000)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            from_pos = 9
//            to_pos = 13
//            do_move()
//        }, 2000)
    }

    init {


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }
}