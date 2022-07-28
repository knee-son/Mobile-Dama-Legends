package com.example.mobiledamalegends

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


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameFragment : Fragment() {
    enum class TileState {blank, white, black}

    data class TileContent (val tile_state:TileState) {
        public var tile_image : ImageView? = null
        public var clicked = false}

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

    private var _binding: FragmentGameBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for(i in  0..11) {piece_map.set(i, TileContent(TileState.white))}
        for(i in 20..31) {piece_map.set(i, TileContent(TileState.black))}

//      piece height is 36
        val dip = 39f; val r: Resources = resources
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,r.displayMetrics).toInt()

        val brd = binding.gameFrame.layoutParams.width
        val chp = (brd.toFloat()/8).toInt()+1
        val ofs = ((brd.toFloat()/8-px)/2).toInt()

        for (i in 0..31){
            var state = piece_map.get(i).tile_state

            if(state != TileState.blank){
                piece_map.get(i).tile_image = ImageView(this.context)
                when(state){
                    TileState.white ->
                        piece_map.get(i).tile_image?.setImageResource(R.drawable.whitedamapiece)
                    TileState.black ->
                        piece_map.get(i).tile_image?.setImageResource(R.drawable.blackdamapiece)
                    else -> return
                }
            }

            var cur_im = piece_map.get(i).tile_image

            var lp = RelativeLayout.LayoutParams(px,px)
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            lp.setMargins(chp*((i%4)*2 + (i/4)%2)+ofs, 0, 0, chp*(i/4)+ofs)

            var xCoOrdinate = 0f
            var yCoOrdinate = 0f
            if(cur_im != null) {
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

//                cur_im?.setOnClickListener {
//                    view.setLayoutParams(RelativeLayout.LayoutParams(px+5,px+5))
//                }

                binding.damaField.addView(cur_im)
            }

//            val myImage = ImageView(this.context)
//            myImage.setImageResource(R.drawable.blackdamapiece)
//            binding.gameFrame.addView(myImage)
//            myImage.setLayoutParams(ViewGroup.LayoutParams(px,px))
        }

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_GameFragment_to_MenuFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}