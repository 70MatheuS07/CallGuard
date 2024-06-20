package com.ufes.callguard.Class

import android.os.Parcel
import com.ufes.callguard.Class.Contact
import com.ufes.callguard.Class.ListaBloqueio
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ListaBloqueioTest {

    @Test
    fun testDefaultConstructor() {
        val listaBloqueio = ListaBloqueio()
        assertNotNull(listaBloqueio)
        assertEquals("", listaBloqueio.getId())
        assertTrue(listaBloqueio.getListaBlock().isEmpty())
    }

    @Test
    fun testParameterizedConstructor() {
        val contacts = mutableListOf(Contact("1", "John Doe", "123456789"))
        val listaBloqueio = ListaBloqueio("testId", contacts)
        assertEquals("testId", listaBloqueio.getId())
        assertEquals(contacts, listaBloqueio.getListaBlock())
    }

    @Test
    fun testParcelConstructor() {
        val parcel = Parcel.obtain()
        parcel.writeString("testId")
        parcel.setDataPosition(0)

        val listaBloqueio = ListaBloqueio(parcel)
        assertEquals("testId", listaBloqueio.getId())
        parcel.recycle()
    }

    @Test
    fun testWriteToParcel() {
        val contacts = mutableListOf(Contact("1", "John Doe", "123456789"))
        val listaBloqueio = ListaBloqueio("testId", contacts)
        val parcel = Parcel.obtain()
        listaBloqueio.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdFromParcel = ListaBloqueio.CREATOR.createFromParcel(parcel)
        assertEquals("testId", createdFromParcel.getId())
        parcel.recycle()
    }

    @Test
    fun testSettersAndGetters() {
        val listaBloqueio = ListaBloqueio()
        listaBloqueio.setId("newId")
        assertEquals("newId", listaBloqueio.getId())

        val contacts = mutableListOf(Contact("1", "Jane Doe", "987654321"))
        listaBloqueio.setListaBlock(contacts)
        assertEquals(contacts, listaBloqueio.getListaBlock())
    }
}
