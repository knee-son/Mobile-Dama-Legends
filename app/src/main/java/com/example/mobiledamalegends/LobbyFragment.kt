package com.example.mobiledamalegends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobiledamalegends.databinding.FragmentLobbyBinding


class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPlay.setOnClickListener {
            findNavController().navigate(R.id.action_lobbyFragment_to_GameFragment)
        }

        binding.buttonProfile.setOnClickListener {
            findNavController().navigate(R.id.action_lobbyFragment_to_profileFragment)
        }

        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_lobbyFragment_to_settingsFragment)
        }

//        val rootSet = AnimationSet(true)
//        rootSet.interpolator = CycleInterpolator(7f)
//        val trans2 =
//            TranslateAnimation(
//            0, 0, 0,
//            100
//        )
//        trans2.duration = 800
//        rootSet.addAnimation(trans2)
//
//        rootSet.setRepeatCount(Animation.INFINITE)

        binding.eyeCatcher.animate().scaleX(1.05f).scaleY(1.05f).setDuration(5000).setInterpolator(
            CycleInterpolator (-1f)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}