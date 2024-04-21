package com.ifs21035.lostfounds.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ifs21035.lostfounds.data.repository.AuthRepository
import com.ifs21035.lostfounds.data.repository.LostFoundRepository
import com.ifs21035.lostfounds.data.repository.UserRepository
import com.ifs21035.lostfounds.di.Injection
import com.ifs21035.lostfounds.presentation.login.LoginViewModel
import com.ifs21035.lostfounds.presentation.main.MainViewModel
import com.ifs21035.lostfounds.presentation.profile.ProfileViewModel
import com.ifs21035.lostfounds.presentation.register.RegisterViewModel
import com.ifs21035.lostfounds.presentation.lostfound.LostFoundViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val lostFoundRepository: LostFoundRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel
                    .getInstance(authRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel
                    .getInstance(authRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel
                    .getInstance(authRepository, lostFoundRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel
                    .getInstance(authRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(LostFoundViewModel::class.java) -> {
                LostFoundViewModel
                    .getInstance(lostFoundRepository) as T
            }
            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: " + modelClass.name
            )
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideUserRepository(context),
                    Injection.provideTodoRepository(context)
                )
            }
            return INSTANCE as ViewModelFactory
        }
    }
}