package com.beeftechlabs.androiddapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeftechlabs.walletconnectv2.WCState
import com.beeftechlabs.walletconnectv2.WalletConnect
import com.beeftechlabs.walletconnectv2.model.AppMetadata
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainDappViewModel : ViewModel() {

    private val walletConnect = WalletConnect(
        AppMetadata(
            name = "BTL Dapp",
            description = "BTL Sample Dapp",
            url = "https://beeftechlabs.com",
            icons = emptyList()
        )
    )

    private val _uri = MutableLiveData<String?>()
    val uri: LiveData<String?> = _uri

    private val _sessionApproved = MutableLiveData<Pair<Boolean, List<String>>>()
    val sessionApproved: LiveData<Pair<Boolean, List<String>>> = _sessionApproved

    init {
        viewModelScope.launch {
            walletConnect.state.collect { state ->
                when (state) {
                    WCState.Initial -> {}
                    WCState.Paired -> {}
                    WCState.Pairing -> {}
                    WCState.PairingFailed -> {}
                    is WCState.SessionApproved -> {
                        _sessionApproved.postValue(true to state.accounts)
                    }
                    is WCState.SessionProposed -> {}
                    is WCState.SessionRejected -> {
                        _sessionApproved.postValue(false to emptyList())
                    }
                }
            }
        }
    }

    fun connectToWallet() {
       viewModelScope.launch {
           val uri = walletConnect.newConnection(
               relay = "https://relay.walletconnect.com/?apiKey=5ff02a7817f5b2466723b75c23742530",
               controller = false
           )
           _uri.postValue(uri.pair)
       }
    }
}