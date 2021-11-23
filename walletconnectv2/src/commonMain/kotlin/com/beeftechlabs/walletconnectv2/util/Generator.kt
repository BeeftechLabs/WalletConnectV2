package com.beeftechlabs.walletconnectv2.util

import io.ktor.util.date.*

object Generator {

    fun newLongId() = getTimeMillis()
}