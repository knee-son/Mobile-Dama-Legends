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

        binding.eyeCatcher.animate().scaleX(1.05f).scaleY(1.05f).setDuration(5000).setInterpolator(
            CycleInterpolator (-1f)
        )

        val trans = TranslateAnimation(800f, 0f, 0f,0f)
        trans.duration = 700
        trans.setInterpolator(AccelerateDecelerateInterpolator())

        binding.buttonPlay.setAnimation(trans)
        binding.buttonProfile.setAnimation(trans)
        binding.buttonSettings.setAnimation(trans)

        val set = AnimationSet(true)

        val rot = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        rot.duration = 1200
        rot.startOffset = 3000
        rot.setInterpolator(DecelerateInterpolator (2.5f))
        rot.setRepeatCount(Animation.INFINITE)

        val scale = ScaleAnimation (0f, 50f,
            0f, 50f)
//        set.addAnimation(scale)
        set.addAnimation(rot)

        binding.fidgetSpinner.setAnimation(set)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}