package com.ufes.callguard.Class

import android.os.Parcel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ContactReportTest {

    @Test
    fun testDefaultConstructor() {
        val contactReport = ContactReport()
        assertEquals("", contactReport.name)
        assertEquals("", contactReport.number)
        assertEquals(mutableListOf(0, 0, 0, 0), contactReport.type)
    }

    @Test
    fun testParameterizedConstructor() {
        val typeList = mutableListOf(1, 2, 3, 4)
        val contactReport = ContactReport("Maria", "123456789", typeList)
        assertEquals("Maria", contactReport.name)
        assertEquals("123456789", contactReport.number)
        assertEquals(typeList, contactReport.type)
    }

    @Test
    fun testSetGetType() {
        val contactReport = ContactReport()
        val typeList = mutableListOf(1, 1, 1, 1)
        contactReport.type = typeList
        assertEquals(typeList, contactReport.type)
    }

    @Test
    fun testParcel() {
        val typeList = mutableListOf(1, 2, 3, 4)
        val contactReport = ContactReport("Maria", "123456789", typeList)
        val parcel = Parcel.obtain()
        contactReport.writeToParcel(parcel, contactReport.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = ContactReport.CREATOR.createFromParcel(parcel)
        assertEquals(contactReport.name, createdFromParcel.name)
        assertEquals(contactReport.number, createdFromParcel.number)
        assertEquals(contactReport.type, createdFromParcel.type)

        parcel.recycle()
    }

    @Test
    fun testToHashMap() {
        val typeList = mutableListOf(1, 2, 3, 4)
        val contactReport = ContactReport("Maria", "123456789", typeList)
        val hashMap = contactReport.toHashMap()

        assertEquals("Maria", hashMap["name"])
        assertEquals("123456789", hashMap["number"])
        assertEquals(typeList, hashMap["type"])
    }
}
