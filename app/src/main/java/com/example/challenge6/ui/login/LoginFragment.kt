package com.example.challenge6.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.challenge6.R
import com.example.challenge6.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAccount.setOnClickListener{ toRegistPage() }
        binding.btnLogin.setOnClickListener { toLoggingIn() }

        val option = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()

        loginViewModel.getLoginStatus().observe(viewLifecycleOwner) {
            Log.d("name", it.toString())
            if (it == true) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment, null, option)
            }
        }
    }

    private fun toLoggingIn() {
        val username = binding.etLoginUsername.text.toString()
        val password = binding.etLoginPassword.text.toString()

        var usernameAccount: String? = ""
        var passwordAccount: String? = ""

        loginViewModel.getUsername().observe(viewLifecycleOwner) {
            usernameAccount = it.toString()
        }

        loginViewModel.getPassword().observe(viewLifecycleOwner) {
            passwordAccount = it.toString()
        }

        if (username == usernameAccount && password == passwordAccount) {
            val option = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()

            loginViewModel.statusLogin(true)
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment, null, option)
        } else {
            Toast.makeText(requireContext(), "Akun tidak sesuai", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toRegistPage() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}