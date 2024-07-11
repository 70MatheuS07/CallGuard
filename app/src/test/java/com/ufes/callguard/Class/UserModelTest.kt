package com.ufes.callguard.Class

import android.os.Parcel
import com.ufes.callguard.Class.UserModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UserModelTest {

    @Test
    fun testDefaultConstructor() {
        val user = UserModel()
        assertEquals("", user.getId())
        assertEquals("", user.getName())
        assertEquals("", user.getPhone())
        assertTrue(user.getBlockList().isEmpty())
        assertTrue(user.getAmigoList().isEmpty())
        assertFalse(user.getHighReports())
    }

    @Test
    fun testParameterizedConstructor() {
        val user = UserModel("1", "Andre", "123456789")
        assertEquals("1", user.getId())
        assertEquals("Andre", user.getName())
        assertEquals("123456789", user.getPhone())
    }

    @Test
    fun testSetGetId() {
        val user = UserModel()
        user.setId("2")
        assertEquals("2", user.getId())
    }

    @Test
    fun testSetGetName() {
        val user = UserModel()
        user.setName("Maria")
        assertEquals("Maria", user.getName())
    }

    @Test
    fun testSetGetPhone() {
        val user = UserModel()
        user.setPhone("987654321")
        assertEquals("987654321", user.getPhone())
    }

    @Test
    fun testSetGetBlockList() {
        val user = UserModel()
        val contact = Contact("Maria", "123456789")
        user.setBlockList(mutableListOf(contact))
        assertEquals(1, user.getBlockList().size)
        assertEquals("Maria", user.getBlockList()[0].getContactName())
    }

    @Test
    fun testSetGetHighReports() {
        val user = UserModel()
        user.setHighReports(true)
        assertTrue(user.getHighReports())
    }

    @Test
    fun testAddGetAmigoList() {
        val user = UserModel()
        val friend = Friend("Maria", true)
        user.addAmigo(friend)
        assertEquals(1, user.getAmigoList().size)
        assertEquals("Maria", user.getAmigoList()[0].userName)
    }

    @Test
    fun testParcel() {
        val user = UserModel("1", "Maria", "123456789")
        val parcel = Parcel.obtain()
        user.writeToParcel(parcel, user.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = UserModel.CREATOR.createFromParcel(parcel)
        assertEquals(user.getId(), createdFromParcel.getId())
        assertEquals(user.getName(), createdFromParcel.getName())
        assertEquals(user.getPhone(), createdFromParcel.getPhone())

        parcel.recycle()
    }
}