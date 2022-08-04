package com.example.mobiledamalegends

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobiledamalegends.databinding.FragmentGameBinding
import java.util.*
import kotlin.properties.Delegates

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

        var game_is_new = true
        var white_playing = true
        var white_turn = true
        var ate = false

        var board_image by Delegates.notNull<Int>()
        var white_image by Delegates.notNull<Int>()
        var black_image by Delegates.notNull<Int>()

        var eat_chain = intArrayOf()
        var pos_chain = intArrayOf()

        //    --28--29--30--31
        //    24--25--26--27--
        //    --20--21--22--23
        //    16--17--18--19--
        //    --12--13--14--15
        //    08--09--10--11--
        //    --04--05--06--07
        //    00--01--02--03--

        var tile_map = Array <TileContent> (32) {TileContent(TileState.blank)}.toMutableList()
        var tile_map_cpy = tile_map

//        index:     0   1   2   3
//        direction: se  sw  ne  nw

        val move_path = arrayListOf <IntArray> (
            intArrayOf(-1,-1,-1, 4),intArrayOf(-1,-1, 4, 5),intArrayOf(-1,-1, 5, 6),intArrayOf(-1,-1, 6, 7),
            intArrayOf( 0, 1, 8, 9),intArrayOf( 1, 2, 9,10),intArrayOf( 2, 3,10,11),intArrayOf( 3,-1,11,-1),
            intArrayOf(-1, 4,-1,12),intArrayOf( 4, 5,12,13),intArrayOf( 5, 6,13,14),intArrayOf( 6, 7,14,15),
            intArrayOf( 8, 9,16,17),intArrayOf( 9,10,17,18),intArrayOf(10,11,18,19),intArrayOf(11,-1,19,-1),
            intArrayOf(-1,12,-1,20),intArrayOf(12,13,20,21),intArrayOf(13,14,21,22),intArrayOf(14,15,22,23),
            intArrayOf(16,17,24,25),intArrayOf(17,18,25,26),intArrayOf(18,19,26,27),intArrayOf(19,-1,27,-1),
            intArrayOf(-1,20,-1,28),intArrayOf(20,21,28,29),intArrayOf(21,22,29,30),intArrayOf(22,23,30,31),
            intArrayOf(24,25,-1,-1),intArrayOf(25,26,-1,-1),intArrayOf(26,27,-1,-1),intArrayOf(27,-1,-1,-1)
        )
    }

    enum class TileState {blank, white, black, lit}

    data class TileContent(var state: TileState) {
        var is_dama = false
        public var clicked = false
        var id = 0
    }

    // TODO: multiple piece capture

    fun do_move() {
        val ts = {i: Int -> tile_map[i].state}
        val tp = to_pos
        val fp = from_pos
        to_pos = from_pos
        var s = ts(fp) // tile state of from position
        var _s =
            if (s == TileState.white) TileState.black
            else TileState.white

//        var valid = false
        val valid = {
            to_pos = tp
            if(white_turn && tp in (28..31) || !white_turn && tp in (0..3)) {
                tile_map_cpy[fp].is_dama = true
                println("piece# ${tile_map_cpy[fp].id} has been promoted!")
            }
            if(!ate) submit_turn()
            else check_again(fp, tp, _s)
        }

        if (ts(tp) == TileState.blank) //  pieces only hop to spaces, not on pieces
            if (!tile_map_cpy[fp].is_dama) // piece is not dama. execute simple movement
            {
                // check for blank tile
                for (i in if(white_turn) 2..3 else 0..1) {
                    println("fp: ${fp}")
                    println("tp: ${tp}")
                    println("move_path[fp]: ${move_path[fp].joinToString() }")
                    println("tile_state: ${tile_map_cpy[tp].state}")

                    if (
                        move_path[fp].indexOf(tp) == i &&
                        tile_map_cpy[tp].state == TileState.blank &&
                        !ate
                    ) {
                        valid()
                        break
                    }
                }
                // check for enemy tile
                for (i in move_path[fp].filterIndexed { // enemy tile
                        _, to -> to!=-1 && tile_map_cpy[to].state==_s}) {
                    val dir = move_path[fp].indexOf(i)
                    println("piece was adjacent to opponent at direction ${dir}")
                    if(move_path[move_path[fp][dir]][dir]!=-1) {
                        if (tp == move_path[move_path[fp][dir]][dir]) {
                            tile_map_cpy[move_path[fp][dir]].state = TileState.blank
//                            do_eat(intArrayOf(move_path[fp][dir]))
                            binding.buttonSubmit.setEnabled(true)
                            eat_chain += move_path[fp][dir]
                            pos_chain += to_pos
                            ate = true
                            valid()
                            break
                        }
                    }
                }
            }

        val coOrd = pos_to_coOrd(to_pos)
        image_focused?.animate()?.x(coOrd[0])?.y(coOrd[1])?.setDuration(0)
        Collections.swap(tile_map, from_pos, to_pos)

        print_board()
    }

    fun check_again(fp: Int, tp: Int, _s: TileState) {
        for (i in move_path[fp].filterIndexed { // enemy tile
                _, to -> to!=-1 && tile_map_cpy[to].state==_s}) {
            val dir = move_path[fp].indexOf(i)
            println("piece was adjacent to opponent at direction ${dir}")
            if(move_path[move_path[fp][dir]][dir]!=-1) {
                if (tp == move_path[move_path[fp][dir]][dir]
                    && tile_map_cpy[tp].state != TileState.blank) {
                    println("found ahead! ${move_path[move_path[fp][dir]][dir]}")
                    return
                }
                else submit_turn()
            }
        }
    }

    fun submit_turn(){
        println(">> submitting turn...")
        println("eat_chain = ${eat_chain.joinToString()}")
        println("pos_chain = ${pos_chain.joinToString()}")
        if (ate) {
            do_eat(eat_chain)
            eat_chain = intArrayOf()
            pos_chain = intArrayOf()
            binding.buttonSubmit.setEnabled(false)
        }

        ate=false
        tile_map=tile_map_cpy
        white_turn=!white_turn

        println("${
            if(white_turn) "White"
            else "Black"
        } to move!")
    }

    fun do_eat(arr: IntArray) {
        for (i in arr) {
            tile_map[i].state = TileState.blank
            binding
                .damaField
                .getChildAt(tile_map[i].id)
                .setVisibility(View.GONE)
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
        println(">> print_board")
        for(y in 7 downTo 0) {
            for (x in 0..7) {
                val pos = {(x+y*8)/2}
                print(
                    if ((x%2==0) == (y%2==0))
                        if (tile_map_cpy[pos()].state != TileState.blank)

//                            "%02d".format(piece_map[pos()].image_index)

                            when (tile_map_cpy[pos()].state)  {
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

//        for(y in 7 downTo 0) {
//            for (x in 0..7) {
//                val pos = {(x+y*8)/2}
//                print(
//                    if ((x%2==0) == (y%2==0))
//                        if (tile_map[pos()].state != TileState.blank)
//
////                            "%02d".format(piece_map[pos()].image_index)
//
//                            when (tile_map[pos()].state)  {
//                                TileState.white -> "WW"
//                                TileState.black -> "BB"
//                                else -> ""
//                            }
//
//                        else "**"
//                    else "--"
//                )
//            }
//            println()
//        }
    }

    val put_image = {r: Int ->
        println(">> put_image")
        println("impregnating with resource r = ${r}")
        image_focused?.setImageResource(r)
    }

    val put_state = {i: Int, t: TileState -> tile_map.set(i, TileContent(t))}

    fun new_game(){
        for(i in 0..11)  put_state(i, TileState.white)
        for(i in 20..31) put_state(i, TileState.black)

        game_is_new = false

        load_game()
        print_board()
    }

    fun old_game(){
        load_game()
        print_board()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun load_game(){
        for (i in 0..31) {
            println("checking state at i = ${i}...")
            var state = tile_map[i].state
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

//                 set size, then location
                image_focused!!.setLayoutParams(p(0))
                val coOrd = pos_to_coOrd(i)
//                image_focused!!.x = 4*chp-px/2
//                image_focused!!.y = 4*chp-px/2
                image_focused!!.animate()
                    .x(coOrd[0])
                    .y(coOrd[1])
                    .setDuration(0)
//                    .setDuration(200)
//                    .setStartDelay(i%12*10.toLong())
//                make image clickable
                image_focused!!.setOnTouchListener(OnTouchListener { v, event ->
                    val is_white =
                        if(state == TileState.white) true
                        else false

                    if (!(is_white == white_turn)) return@OnTouchListener false

                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
//                            println(">> Piece touched!")
//                            println("tile state = ${piece_map[i].tile_state}")
//                            println("is white? ${is_white}")
//                            println("white to move? ${white_turn}")

                            image_focused = v as ImageView
                            from_pos = coOrd_to_pos(v.x, v.y)
                            v.animate()
                                .scaleX(1.2f)
                                .scaleY(1.2f)
                                .x(v.x)
                                .y(v.y)
                                .z(1f)
                                .setDuration(0)
                            xCoOrdinate = v.x - event.rawX
                            yCoOrdinate = v.y - event.rawY
                        }
                        MotionEvent.ACTION_MOVE -> {
                            v.animate()
                                .x(event.rawX + xCoOrdinate)
                                .y(event.rawY + yCoOrdinate)
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

//                println("image_focused: ${image_focused}")
                binding.damaField.addView(image_focused)
                tile_map[i].id = binding.damaField.indexOfChild(image_focused)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_GameFragment_to_lobbyFragment)
        }

        binding.buttonSubmit.setOnClickListener {
            submit_turn()
        }

        r = resources
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,r.displayMetrics).toInt()
        brd = binding.gameFrame.layoutParams.width
        chp = (brd.toFloat()/8)
        ofs = ((brd.toFloat()/8-px)/2)

        val index = (activity as MainActivity).theme_index
        val fetch = {key: String ->
            (activity as MainActivity)
                .theme_list[index].getValue(key) as Int}
        white_image = fetch("white")
        black_image = fetch("black")
        board_image = fetch("board")

        binding.damaBoard.setImageResource(board_image)

        if(game_is_new) new_game()
        else old_game()
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