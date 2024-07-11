package com.ufes.callguard.Class

import org.junit.Assert.*
import org.junit.Test

class BlockedContactModelTest {

    @Test
    fun testConstructor() {
        val blockedContact = BlockedContactModel("Maria", "123456789")
        assertEquals("Maria", blockedContact.name)
        assertEquals("123456789", blockedContact.number)
    }

    @Test
    fun testToMap() {
        val blockedContact = BlockedContactModel("Maria", "123456789")
        val map = blockedContact.toMap()

        assertEquals(2, map.size)
        assertEquals("Maria", map["name"])
        assertEquals("123456789", map["number"])
    }
}
