package com.example.mobiledamalegends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobiledamalegends.databinding.FragmentGameBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameFragment : Fragment() {

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

        println( "successfully made gamefragment" )

        var wpiece = binding.whitepiece
        var wpiece_params = wpiece.layoutParams

        wpiece.isFocusable = true
        println ("is the piece focusable? " + wpiece.isFocusable)

        wpiece.setOnClickListener{ wpiece.requestFocus(); println("clicked!")}

        wpiece.setOnFocusChangeListener { view, b ->
            wpiece.requestLayout()
            when (b){
                true -> { view.layoutParams.height = (wpiece_params.height * 1.2).toInt()
                        view.layoutParams.width = (wpiece_params.width * 1.2).toInt() }
                false -> { view.layoutParams.height = wpiece_params.height
                        view.layoutParams.width = wpiece_params.width }
            }
            println( view.layoutParams )
        }

//        wpiece.setOnClickListener {
//
//            wpiece_clicked = !wpiece_clicked
//            wpiece.requestLayout()
//            when (wpiece_clicked){
//                true -> wpiece.layoutParams.height =
//                false -> wpiece.height = wpiece.height*1.2
//            }
//        }

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}