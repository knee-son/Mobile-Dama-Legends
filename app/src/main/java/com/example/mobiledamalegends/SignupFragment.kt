package com.example.mobiledamalegends

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.mobiledamalegends.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textAlreadyHaveAcc.setOnClickListener {
            findNavController().navigate(R.id.action_SignupFragment_to_LoginFragment)
        }

        binding.btnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_SignupFragment_to_lobbyFragment)
        }

        binding.btnSignup.setOnClickListener {
//            when {
//                TextUtils.isEmpty(binding.inputEmail.text.toString().trim { it <= ' '}) -> {
//                    Toast.makeText(
//                        getActivity(),
//                        "Please enter email.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                TextUtils.isEmpty(binding.inputPassword.text.toString().trim { it <= ' '}) -> {
//                    Toast.makeText(
//                        getActivity(),
//                        "Please enter password.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                else -> {
//                    val email: String = binding.inputEmail.text.toString().trim { it <= ' '}
//                    val password: String = binding.inputPassword.text.toString().trim { it <= ' '}
//
//                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(
//                            OnCompleteListener<AuthResult> { task ->
//                                if (task.isSuccessful) {
//                                    val firebaseUser: FirebaseUser = task.result!!.user!!
//
//                                    Toast.makeText(
//                                        getActivity(),
//                                        "You have been registered successfully!",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//
//                                    val intent =
//                                        Intent(getActivity(), SignupFragment::class.java)
//                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    intent.putExtra("user_id", firebaseUser.uid)
//                                    intent.putExtra("email_id", email)
//                                    startActivity(intent)
//
//                                } else{
//                                    Toast.makeText(
//                                        getActivity(),
//                                        task.exception!!.message.toString(),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        )
//                }
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}