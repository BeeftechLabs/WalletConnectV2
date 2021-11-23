package com.beeftechlabs.walletconnectv2

import com.beeftechlabs.walletconnectv2.model.SessionProposeRequest

sealed class WCState {

    object Initial : WCState()

    object Pairing : WCState()

    object Paired : WCState()

    object PairingFailed : WCState()

    data class SessionProposed(
        val name: String,
        val description: String,
        val url: String,
        val icons: List<String>,

        internal val request: SessionProposeRequest
    ) : WCState()

    data class SessionApproved(
        val accounts: List<String>
    ) : WCState()

    data class SessionRejected(
        val reason: String
    ) : WCState()
}
