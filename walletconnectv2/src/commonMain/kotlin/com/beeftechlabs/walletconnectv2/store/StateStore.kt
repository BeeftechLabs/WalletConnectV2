package com.beeftechlabs.walletconnectv2.store

import com.beeftechlabs.walletconnectv2.WCPairings
import com.beeftechlabs.walletconnectv2.WCSessions
import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean

internal class StateStore {

    private val settings: Settings = Settings()

    var isPaired: Boolean by settings.boolean(IS_PAIRED)

    private val _pairings = DataSettingConfig(settings, WCPairings.serializer(), PAIRINGS, WCPairings())
    var pairings: WCPairings
        get() = _pairings.get()
        set(value) {
            _pairings.set(value)
        }

    private val _sessions = DataSettingConfig(settings, WCSessions.serializer(), SESSIONS, WCSessions())
    var sessions: WCSessions
        get() = _sessions.get()
        set(value) {
            _sessions.set(value)
        }

    companion object {
        private const val IS_PAIRED = "wc.is_paired"
        private const val PAIRINGS = "wc.pairings"
        private const val SESSIONS = "wc.sessions"
    }
}