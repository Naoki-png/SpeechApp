package com.example.speechapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.data.EncryptedPreferenceManager
import com.example.speechapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var encryptedPreferenceManager: EncryptedPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setOnClickListener {
            encryptedPreferenceManager.writeEncrypted(EncryptedPreferenceManager.USER_ID, binding.userIdText.text.toString())
            encryptedPreferenceManager.writeEncrypted(EncryptedPreferenceManager.PASSWORD, binding.userPasswordText.text.toString())

            Toast.makeText(requireContext(), "${encryptedPreferenceManager.readEncrypted(EncryptedPreferenceManager.USER_ID)}：${encryptedPreferenceManager.readEncrypted(EncryptedPreferenceManager.PASSWORD)}", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_LoginFragment_to_ChatFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}