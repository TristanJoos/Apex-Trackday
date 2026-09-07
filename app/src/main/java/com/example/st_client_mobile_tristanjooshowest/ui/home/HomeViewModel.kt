package com.example.st_client_mobile_tristanjooshowest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.st_client_mobile_tristanjooshowest.domain.usecase.GetProfilePictureUriUseCase
import com.example.st_client_mobile_tristanjooshowest.domain.usecase.GetSubscriptionStatusUseCase
import com.example.st_client_mobile_tristanjooshowest.domain.usecase.SubscribeToNewsletterUseCase
import com.example.st_client_mobile_tristanjooshowest.domain.usecase.UpdateProfilePictureUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSubscriptionStatus: GetSubscriptionStatusUseCase,
    private val subscribeToNewsletter: SubscribeToNewsletterUseCase,
    private val getProfilePictureUri: GetProfilePictureUriUseCase,
    private val updateProfilePicture: UpdateProfilePictureUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            getSubscriptionStatus().collect { subscribed ->
                _uiState.update {
                    it.copy(isSubscribed = subscribed)
                }
            }
        }
        viewModelScope.launch {
            getProfilePictureUri().collect { uri ->
                _uiState.update {
                    it.copy(profilePictureUri = uri)
                }
            }
        }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update {it.copy(emailAddress = newEmail)}
    }

    fun onSubscribeClicked() {
        viewModelScope.launch {
            subscribeToNewsletter()
        }
    }

    fun onProfilePictureChanged(uri: String) {
        viewModelScope.launch {
            updateProfilePicture(uri)
        }
    }
}
