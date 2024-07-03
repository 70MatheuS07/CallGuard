package com.ufes.callguard.Class

data class Amigo(val userName: String, var isSelected: Boolean) {
    // Default constructor
    constructor() : this("", false)
}
