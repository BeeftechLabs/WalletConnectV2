package com.beeftechlabs.walletconnectv2.store

import com.beeftechlabs.walletconnectv2.logging.Log
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer

sealed class SettingConfig<T>(
    private val settings: Settings,
    private val key: String,
    private val defaultValue: T,
) {
    protected abstract fun getValue(settings: Settings, key: String, defaultValue: T): T
    protected abstract fun setValue(settings: Settings, key: String, value: T)

    @ExperimentalSettingsApi
    private var listener: SettingsListener? = null

    fun remove() = settings.remove(key)
    fun exists(): Boolean = settings.hasKey(key)

    fun get(): T = getValue(settings, key, defaultValue)
    fun set(value: T): Boolean {
        return try {
            setValue(settings, key, value)
            true
        } catch (exception: Exception) {
            false
        }
    }

    @ExperimentalSettingsApi
    var isLoggingEnabled: Boolean
        get() = listener != null
        set(value) {
            val settings = settings as? ObservableSettings ?: return
            listener = if (value) {
                listener?.deactivate() // just in case
                settings.addListener(key) { Log.d(TAG, "$key = ${get()}") }
            } else {
                listener?.deactivate()
                null
            }
        }

    override fun toString() = key

    companion object {
        private const val TAG = "AppSettings"
    }
}

@ExperimentalSettingsApi
class DataSettingConfig<T>(
    settings: Settings,
    private val serializer: KSerializer<T>,
    key: String,
    defaultValue: T
) : SettingConfig<T>(settings, key, defaultValue) {

    @OptIn(ExperimentalSerializationApi::class)
    override fun getValue(settings: Settings, key: String, defaultValue: T): T =
        settings.decodeValue(serializer, key, defaultValue)

    @OptIn(ExperimentalSerializationApi::class)
    override fun setValue(settings: Settings, key: String, value: T) {
        settings.encodeValue(serializer, key, value)
    }
}