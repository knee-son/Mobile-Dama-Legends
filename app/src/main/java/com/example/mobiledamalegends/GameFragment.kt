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


    enum class TileState {blank, white, black, lit}

    data class TileContent (val tile_state:TileState) {
        public var tile_image : ImageView? = null
        public var clicked = false
    }

    var piece_map = Array <TileContent> (32) {TileContent(TileState.blank)}

    //    --28--29--30--31
    //    24--25--26--27--
    //    --20--21--22--23
    //    16--17--18--19--
    //    --12--13--14--15
    //    08--09--10--11--
    //    --04--05--06--07
    //    00--01--02--03--

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

    companion object {
        // piece height is 39
        val dip = 39f
        lateinit var r: Resources
        var px by Delegates.notNull<Int>()
        var brd by Delegates.notNull<Int>()
        var chp by Delegates.notNull<Int>()
        var ofs by Delegates.notNull<Int>()

        var xCoOrdinate = 0f
        var yCoOrdinate = 0f

        var from_pos = 0
        var to_pos = 0
    }

    fun do_move() {
        val lp = piece_map[from_pos].tile_image?.getLayoutParams()
                as RelativeLayout.LayoutParams
        println("to_pos: "+ to_pos)
        val coOrd = pos_to_coOrd(to_pos)
        println("coOrd: "+ coOrd[0] +", "+ coOrd[1])
        lp.setMargins(coOrd[0], 0, 0, coOrd[1])
        piece_map[from_pos].tile_image?.setLayoutParams(lp)
        Collections.swap(piece_map.toMutableList(), from_pos, to_pos)
    }

//    converts board position (0-31) to coOrds on screen
    fun pos_to_coOrd(pos: Int): IntArray {
        println("pos_to_coOrd converted: "+ chp+ ", " + pos/4)
        return intArrayOf (chp*((pos%4)*2+(pos/4)%2)+ofs, chp*(pos/4)+ofs)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_GameFragment_to_MenuFragment)
        }

        r = resources
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,r.displayMetrics).toInt()
        brd = binding.gameFrame.layoutParams.width
        chp = (brd.toFloat()/8).toInt()+1
        println("chp calculated as: "+ chp)
        ofs = ((brd.toFloat()/8-px)/2).toInt()

        for(i in  0..11) {piece_map.set(i, TileContent(TileState.white))}
        for(i in 20..31) {piece_map.set(i, TileContent(TileState.black))}

        for (i in 0..31) {
            var state = piece_map.get(i).tile_state

            if (state != TileState.blank) {
                piece_map.get(i).tile_image = ImageView(this.context)
                when (state) {
                    TileState.white ->
                        piece_map.get(i).tile_image?.setImageResource(R.drawable.whitedamapiece)
                    TileState.black ->
                        piece_map.get(i).tile_image?.setImageResource(R.drawable.blackdamapiece)
                    else -> {}
                }
            }

            var cur_im = piece_map.get(i).tile_image

            var lp = RelativeLayout.LayoutParams(px, px)
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            val coOrd = pos_to_coOrd(i)
            lp.setMargins(coOrd[0], 0, 0, coOrd[1])

            if (cur_im != null) {
                cur_im.setLayoutParams(lp)
                cur_im.setOnTouchListener(OnTouchListener { view, event ->

                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            xCoOrdinate = view.x - event.rawX
                            yCoOrdinate = view.y - event.rawY
                        }
                        MotionEvent.ACTION_MOVE -> view.animate().x(event.rawX + xCoOrdinate)
                            .y(event.rawY + yCoOrdinate).setDuration(0).start()
                        else -> return@OnTouchListener false
                    }
                    true
                })

//                cur_im.setOnClickListener {
//                    view.setLayoutParams(RelativeLayout.LayoutParams(px+5,px+5))
//                }

                binding.damaField.addView(cur_im)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            from_pos = 8
            to_pos = 12
            do_move()
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            from_pos = 9
            to_pos = 13
            do_move()
        }, 2000)
    }

    init {
//        xCoOrdinate = 0f
//        yCoOrdinate = 0f


//        val handler = Handler()
//        handler.postDelayed(Runnable {
//            from_pos = 9
//            to_pos = 13
//            do_move()
//        }, 1000)
//
//        handler.postDelayed(Runnable {
//            from_pos = 13
//            to_pos = 17
//            do_move()
//        }, 1000)

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