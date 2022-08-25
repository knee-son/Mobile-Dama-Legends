package com.kneeson.mobiledamalegends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kneeson.mobiledamalegends.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogout.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_MenuFragment)
        }

        binding.buttonBack2.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_lobbyFragment)
        }

        binding.nextTheme.setOnClickListener {
            var index = ++(activity as MainActivity).theme_index
            if (index == (activity as MainActivity).theme_list.size) {
                index = 0
                (activity as MainActivity).theme_index = 0
            }

            binding.themeName.setText(
                (activity as MainActivity).theme_list[index]["name"] as CharSequence)
        }

        binding.themeName.setText(
            (activity as MainActivity).theme_list
                [(activity as MainActivity).theme_index]
                ["name"] as CharSequence)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}