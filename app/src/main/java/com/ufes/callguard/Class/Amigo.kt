package com.ufes.callguard.Class

data class Amigo(val userName: String, val isSelected: Boolean) {
    // Default constructor
    constructor() : this("", false)
}
