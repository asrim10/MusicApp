package com.example.musicapp.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityForgotPasswordBinding
import com.example.musicapp.repository.UserRepositoryImpl
import com.example.musicapp.utils.LoadingUtils
import com.example.musicapp.viewModel.UserViewModel

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var forgetPasswordBinding: ActivityForgotPasswordBinding
    lateinit var userViewModel: UserViewModel
    lateinit var loadingUtils: LoadingUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        forgetPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(forgetPasswordBinding.root)

        //initializing auth viewmodel
        var repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        //initializing loading
        loadingUtils = LoadingUtils(this)
        forgetPasswordBinding.btnReset.setOnClickListener {
            loadingUtils.show()
            var email: String = forgetPasswordBinding.editEmail.text.toString()

            userViewModel.forgetPassword(email) { success, message ->
                if (success) {
                    loadingUtils.dismiss()
                    Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    loadingUtils.dismiss()
                    Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()

                }
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}