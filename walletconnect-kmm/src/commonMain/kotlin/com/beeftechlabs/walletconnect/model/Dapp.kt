package com.beeftechlabs.walletconnect.model

data class Dapp(
    val deepLinkUri: String,
    val name: String,
    val description: String,
    val url: String,
    val iconUris: List<String>,
)
