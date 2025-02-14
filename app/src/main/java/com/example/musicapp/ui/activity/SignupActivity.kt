package com.example.musicapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivitySignupBinding
import com.example.musicapp.model.UserModel
import com.example.musicapp.repository.UserRepositoryImpl
import com.example.musicapp.utils.LoadingUtils
import com.example.musicapp.viewModel.UserViewModel

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding

    lateinit var userViewModel: UserViewModel

    lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingUtils = LoadingUtils(this)

        val userRepository = UserRepositoryImpl()

        userViewModel = UserViewModel(userRepository)


        binding.signUp.setOnClickListener {
            loadingUtils.show()
            var email: String = binding.registerEmail.text.toString()
            var password: String = binding.registerPassword.text.toString()
            var name: String = binding.registerName.text.toString()

            userViewModel.signup(email,password){
                    success,message,userId ->
                if(success){
                    val userModel = UserModel(
                        userId,
                        email, name,
                    )
                    addUser(userModel)
                }else{
                    loadingUtils.dismiss()
                    Toast.makeText(this@SignupActivity,
                        message, Toast.LENGTH_SHORT).show()
                }


            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }



    fun addUser(userModel: UserModel){
        userViewModel.addUserToDatabase(userModel.userId,userModel){
                success,message ->
            if(success){
                loadingUtils.dismiss()
                Toast.makeText(this@SignupActivity
                    ,message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            }else{
                loadingUtils.dismiss()
                Toast.makeText(this@SignupActivity
                    ,message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}