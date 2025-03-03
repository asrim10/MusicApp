package com.example.musicapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.example.musicapp.R
import com.example.musicapp.repository.UserRepository
import com.example.musicapp.repository.UserRepositoryImpl
import com.example.musicapp.model.UserModel
import com.example.musicapp.ui.activity.LoginActivity

class ProfileFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userRepository: UserRepository
    private lateinit var gmailDetailText: TextView
    private lateinit var homeDetailText: TextView
    private lateinit var contactDetailText: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var passwordLogEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRepository = UserRepositoryImpl()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        gmailDetailText = view.findViewById(R.id.gmailDetail)
        homeDetailText = view.findViewById(R.id.homeDetail)
        contactDetailText = view.findViewById(R.id.contactDetail)
        logoutButton = view.findViewById(R.id.logout)
        editProfileButton = view.findViewById(R.id.editProfile)
        passwordLogEditText = view.findViewById(R.id.passwordLog)

        // Fetch user data from Firebase
        val currentUser = userRepository.getCurrentUser()
        if (currentUser != null) {
            userRepository.getUSerFromDatabase(currentUser.uid) { user, success, message ->
                if (success && user != null) {
                    // Set Gmail, Home, and Contact details
                    gmailDetailText.text = user.email
                    homeDetailText.text = user.homeAddress ?: ""
                    contactDetailText.text = user.phoneNumber ?: ""
                } else {
                    // Handle errors if necessary
                    showToast(message)
                }
            }
        }

        // Implement logout with password validation
        logoutButton.setOnClickListener {
            val password = passwordLogEditText.text.toString()

            if (password.isNotEmpty()) {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser != null) {
                    // Try logging in with the entered password
                    userRepository.login(currentUser.email ?: "", password) { success, message ->
                        if (success) {
                            // If login is successful, perform logout
                            userRepository.logout { loggedOut, logoutMessage ->
                                if (loggedOut) {
                                    // Redirect to login page after logout
                                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                                    requireActivity().finish()
                                } else {
                                    showToast(logoutMessage)
                                }
                            }
                        } else {
                            // Show toast message if password is incorrect
                            showToast(message)
                        }
                    }
                }
            } else {
                showToast("Please enter your password")
            }
        }

        return view
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
