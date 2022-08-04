package com.example.mobiledamalegends

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobiledamalegends.databinding.FragmentLoginBinding
import java.nio.IntBuffer

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signup.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_SignupFragment)
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_lobbyFragment)
        }


        // You are using RGBA that's why Config is ARGB.8888
        var bitmap = Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888)
        // vector is your int[] of ARGB
        bitmap.copyPixelsFromBuffer(
            IntBuffer.wrap(
            arrayListOf <IntArray> (
                intArrayOf(-1,-1,-1, 4),intArrayOf(-1,-1, 4, 5),intArrayOf(-1,-1, 5, 6),intArrayOf(-1,-1, 6, 7),
                intArrayOf( 0, 1, 8, 9),intArrayOf( 1, 2, 9,10),intArrayOf( 2, 3,10,11),intArrayOf( 3,-1,11,-1),
                intArrayOf( 4,-1,12,-1),intArrayOf( 4, 5,12,13),intArrayOf( 5, 6,13,14),intArrayOf( 6, 7,14,15),
                intArrayOf( 8, 9,16,17),intArrayOf( 9,10,17,18),intArrayOf(10,11,18,19),intArrayOf(11,-1,19,-1),
                intArrayOf(12,-1,20,-1),intArrayOf(12,13,20,21),intArrayOf(13,14,21,22),intArrayOf(14,15,22,23),
                intArrayOf(16,17,24,25),intArrayOf(17,18,25,26),intArrayOf(18,19,26,27),intArrayOf(19,-1,27,-1),
                intArrayOf(20,-1,28,-1),intArrayOf(20,21,28,29),intArrayOf(21,22,29,30),intArrayOf(22,23,30,31),
                intArrayOf(24,25,-1,-1),intArrayOf(25,26,-1,-1),intArrayOf(26,27,-1,-1),intArrayOf(27,-1,-1,-1)
            ).reduce { x, y -> x + y }
        ))

        println("bitmap:\n${bitmap}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}