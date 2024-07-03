package com.ufes.callguard.Class

data class BlockedContactModel(
    val name: String,
    val number: String
) {
    fun toMap(): Map<String, String> {
        return mapOf("name" to name, "number" to number)
    }
}