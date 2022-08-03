package com.example.mobiledamalegends

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
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
        var white_turn = true

        val white_image = R.drawable.whitedamapiece
        val black_image = R.drawable.blackdamapiece

        val pop_up = 20
        val pop_half = pop_up.toFloat()/2

    }

    enum class TileState {blank, white, black, lit}

    data class TileContent(var tile_state: TileState) {
        var is_dama = false
        public var clicked = false
        var image_index = 0
    }

    // TODO: transfer var map and paths to companion object

    //    --28--29--30--31
    //    24--25--26--27--
    //    --20--21--22--23
    //    16--17--18--19--
    //    --12--13--14--15
    //    08--09--10--11--
    //    --04--05--06--07
    //    00--01--02--03--

    var piece_map = Array <TileContent> (32) {TileContent(TileState.blank)}.toMutableList()

//        index:     0   1   2   3
//        direction: se  sw  ne  nw

    val move_path = arrayListOf <IntArray> (
        intArrayOf(-1,-1,-1, 4),intArrayOf(-1,-1, 4, 5),intArrayOf(-1,-1, 5, 6),intArrayOf(-1,-1, 6, 7),
        intArrayOf( 0, 1, 8, 9),intArrayOf( 1, 2, 9,10),intArrayOf( 2, 3,10,11),intArrayOf( 3,-1,11,-1),
        intArrayOf( 4,-1,12,-1),intArrayOf( 4, 5,12,13),intArrayOf( 5, 6,13,14),intArrayOf( 6, 7,14,15),
        intArrayOf( 8, 9,16,17),intArrayOf( 9,10,17,18),intArrayOf(10,11,18,19),intArrayOf(11,-1,19,-1),
        intArrayOf(12,-1,20,-1),intArrayOf(12,13,20,21),intArrayOf(13,14,21,22),intArrayOf(14,15,22,23),
        intArrayOf(16,17,24,25),intArrayOf(17,18,25,26),intArrayOf(18,19,26,27),intArrayOf(19,-1,27,-1),
        intArrayOf(20,-1,28,-1),intArrayOf(20,21,28,29),intArrayOf(21,22,29,30),intArrayOf(22,23,30,31),
        intArrayOf(24,25,-1,-1),intArrayOf(25,26,-1,-1),intArrayOf(26,27,-1,-1),intArrayOf(27,-1,-1,-1)
    )

    // TODO: 1. backwards capture 2. prevent player on opponent turn

    fun do_move() {
        var ate = false
        val ts = {i: Int -> piece_map[i].tile_state}
        val to = to_pos
        val fr = from_pos
        to_pos = from_pos
        var s = ts(fr)
        var _s =
            if (s == TileState.white) TileState.black
            else TileState.white
        val valid = { to_pos=to; white_turn=!white_turn
            println("${if(white_turn) "White" else "Black"} to move!")}

        if (ts(to) != s)
            if (!piece_map[fr].is_dama){ // execute simple movement
                for (i in if(white_turn) 2..3 else 0..1) {
                    var pos1 = move_path[fr][i]
                    if(pos1 != -1){
                        if (ts(pos1) == _s) {
                            var pos2 = move_path[pos1][i]
                            if (pos2!=-1 && ts(pos2)==TileState.blank)
                                if (to == pos2) {
                                    ate = true
                                    do_eat(intArrayOf(pos1))
                                    valid()
                                }
                        } else if (ts(pos1) == TileState.blank)
                            if (to == pos1)
                                valid()
                    }
                }
            }

        val coOrd = pos_to_coOrd(to_pos)
        image_focused?.animate()?.x(coOrd[0])?.y(coOrd[1])?.setDuration(0)
        Collections.swap(piece_map, from_pos, to_pos)
    }

    fun do_eat(arr: IntArray) {
        var index: Int
        for (i in arr){
            piece_map[i].tile_state = TileState.blank
            index = piece_map.get(i).image_index
            var im = binding.damaField.getChildAt(index)

            if(im != null) im.setVisibility(View.GONE)
        }
    }

//    converts board position (0-31) to coOrds on screen
    fun pos_to_coOrd(_pos: Int): FloatArray {
        val pos = if(!white_playing) 31-_pos else _pos
        return floatArrayOf (chp*((pos%4)*2+(pos/4)%2)+ofs,
            chp*(7-pos/4)+ofs)
    }

    fun coOrd_to_pos(x: Float, y: Float): Int{
        val clip = {i: Int -> if(i<0) 0 else if(i>7) 7 else i}

        var y_component = clip( ((brd-y)/chp-.5).toInt() )
        var x_component = clip( (x/chp+.5).toInt() )

        return if((x_component%2==0) == (y_component%2==0))
                (x_component + y_component*8)/2
        else from_pos
    }

    fun print_board(){
        for(y in 7 downTo 0) {
            for (x in 0..7) {
                val pos = {(x+y*8)/2}
                print(
                    if ((x%2==0) == (y%2==0))
                        if (piece_map[pos()].tile_state != TileState.blank)

//                            "%02d".format(piece_map[pos()].image_index)

                            when (piece_map[pos()].tile_state)  {
                                TileState.white -> "WW"
                                TileState.black -> "BB"
                                else -> ""
                            }

                        else "**"
                    else "--"
                )
            }
            println()
        }
    }


    val put_image = {r: Int ->
    println("impregnating with resource r = ${r}")
        image_focused?.setImageResource(r)
    }

    val put_state = {i: Int, t: TileState -> piece_map.set(i, TileContent(t))}

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_GameFragment_to_lobbyFragment)
        }

        r = resources
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,r.displayMetrics).toInt()
        brd = binding.gameFrame.layoutParams.width
        chp = (brd.toFloat()/8)
        ofs = ((brd.toFloat()/8-px)/2)

        for(i in 0..11)  put_state(i, TileState.white)
        for(i in 20..31) put_state(i, TileState.black)

        for (i in 0..31) {
            println("checking state at i = ${i}...")
            var state = piece_map[i].tile_state
            println("state: ${state}")

            if(state == TileState.blank) image_focused = null

            else {
                println("image is not null!")
                println("making new image...")
                image_focused = ImageView(this.context)

                when (state) {
                    TileState.white -> put_image(white_image)
                    TileState.black -> put_image(black_image)
                    TileState.lit   -> {}
                    TileState.blank -> {}
                }
                println("image impregnated with content: ${image_focused}")

                val p= {w: Int -> RelativeLayout.LayoutParams(px+w, px+w)}
                // set size, then location
                image_focused!!.setLayoutParams(p(0))
                val coOrd = pos_to_coOrd(i)
                image_focused!!.animate()
                    .x(coOrd[0])
                    .y(coOrd[1])
                    .setDuration(0)

//                make image clickable
                image_focused!!.setOnTouchListener(OnTouchListener { v, event ->
                    val is_white =
                        if(state == TileState.white)
                            true
                        else
                            false

                    if (!(is_white == white_turn)) return@OnTouchListener false

                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
//                            println("Piece touched!")
//                            println("tile state = ${piece_map[i].tile_state}")
//                            println("is white? ${is_white}")
//                            println("white to move? ${white_turn}")

                            image_focused = v as ImageView
                            from_pos = coOrd_to_pos(v.x, v.y)
                            v.animate()
                                .scaleX(1.2f)
                                .scaleY(1.2f)
                                .x(v.x-pop_half)
                                .y(v.y-pop_half)
                                .z(1f)
                            xCoOrdinate = v.x - event.rawX - pop_half
                            yCoOrdinate = v.y - event.rawY - pop_half
                        }
                        MotionEvent.ACTION_MOVE -> {
                            v.animate()
                                .x(event.rawX + xCoOrdinate)
                                .y(event.rawY + yCoOrdinate)
                                .setDuration(0)
                                .start()
                        }
                        MotionEvent.ACTION_UP -> {
                            to_pos = coOrd_to_pos(v.x, v.y)
                            v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .z(0f)
                                .setDuration(0)
                                .start()

                            do_move()
                        }
                        else -> return@OnTouchListener false
                    }
                    true
                })

                println("image_focused: ${image_focused}")
                binding.damaField.addView(image_focused)
                piece_map[i].image_index = binding.damaField.indexOfChild(image_focused)
            }
        }

        print_board()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }
}