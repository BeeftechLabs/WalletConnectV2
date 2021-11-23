package com.beeftechlabs.androidwallet.ui.main

import androidx.lifecycle.*
import com.beeftechlabs.walletconnectv2.WCState
import com.beeftechlabs.walletconnectv2.WalletConnect
import com.beeftechlabs.walletconnectv2.pairing.WalletConnectUri
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainWalletViewModel : ViewModel() {

    private val walletConnect = WalletConnect()

    private val _sessionRequest = MutableLiveData<WCState.SessionProposed>()
    val sessionRequest: LiveData<String> = _sessionRequest.map { it.name }

    init {
        viewModelScope.launch {
            walletConnect.state.collect { state ->
                when (state) {
                    WCState.Initial -> {}
                    WCState.Paired -> {}
                    WCState.Pairing -> {}
                    WCState.PairingFailed -> {}
                    is WCState.SessionApproved -> {}
                    is WCState.SessionProposed -> {
                        _sessionRequest.postValue(state)
                    }
                    is WCState.SessionRejected -> {}
                }
            }
        }
    }

    fun onWcUriReceived(uri: String) {
        viewModelScope.launch {
            val dappUri = WalletConnectUri.parse(uri)
            walletConnect.pair(dappUri)
        }
    }

    fun approveSession() {
        viewModelScope.launch {
            walletConnect.approve(
                _sessionRequest.value!!,
                listOf("erd1hfw5v4u9ank5h8ms35nx8pvvd8um9jyxr9809htdku8gdtnkzras4w4zmr")
            )
        }
    }

    fun rejectSession() {
        viewModelScope.launch {
            walletConnect.reject(
                _sessionRequest.value!!,
                "User denied"
            )
        }
    }
}