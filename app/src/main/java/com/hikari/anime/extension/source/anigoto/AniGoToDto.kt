package com.hikari.anime.extension.source.anigoto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AjaxResponse(
    val source: List<SourceDto> = emptyList(),
    @SerialName("source_bk") val backupSource: List<SourceDto> = emptyList()
)

@Serializable
data class SourceDto(
    val file: String,
    val label: String? = null,
    val type: String? = null
)

@Serializable
data class EncryptedResponse(val data: String)

data class GogoKeyConfig(
    val iv: ByteArray,
    val secretKey: ByteArray,
    val secondKey: ByteArray
) {
    val isValid: Boolean
        get() = iv.size == 16 && secretKey.size in setOf(16, 24, 32) && secondKey.size in setOf(16, 24, 32)
}
