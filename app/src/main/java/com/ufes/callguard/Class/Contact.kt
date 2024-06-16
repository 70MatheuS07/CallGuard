package com.ufes.callguard.Class

data class Contact(
    val id: String,
    val name: String,
    val thumbnailUri: String?,
    val phoneNumbers: MutableList<String> = mutableListOf() // List to store multiple phone numbers
)