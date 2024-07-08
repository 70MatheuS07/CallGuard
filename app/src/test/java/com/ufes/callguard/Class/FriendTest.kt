package com.ufes.callguard.Class


import org.junit.Assert.assertEquals
import org.junit.Test

class FriendTest {

    @Test
    fun testFriendConstructorWithParameters() {
        val userName = "Maria"
        val isSelected = true

        val friend = Friend(userName, isSelected)

        assertEquals(userName, friend.userName)
        assertEquals(isSelected, friend.isSelected)
    }

    @Test
    fun testFriendDefaultConstructor() {
        val friend = Friend()

        assertEquals("", friend.userName)
        assertEquals(false, friend.isSelected)
    }
}
