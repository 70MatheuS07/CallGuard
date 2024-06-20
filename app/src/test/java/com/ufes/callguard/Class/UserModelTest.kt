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
        val userModel = UserModel()
        assertNotNull(userModel)
        assertEquals("", userModel.getId())
        assertEquals("", userModel.getName())
        assertEquals("", userModel.getPhone())
        assertTrue(userModel.getBlockList().isEmpty())
    }

    @Test
    fun testParameterizedConstructor() {
        val userModel = UserModel("1", "John Doe", "123456789")
        assertEquals("1", userModel.getId())
        assertEquals("John Doe", userModel.getName())
        assertEquals("123456789", userModel.getPhone())
    }

    @Test
    fun testParcelConstructor() {
        val parcel = Parcel.obtain()
        parcel.writeString("1")
        parcel.writeString("John Doe")
        parcel.writeString("123456789")
        parcel.writeStringList(mutableListOf("block1", "block2"))
        parcel.setDataPosition(0)

        val userModel = UserModel(parcel)
        assertEquals("1", userModel.getId())
        assertEquals("John Doe", userModel.getName())
        assertEquals("123456789", userModel.getPhone())
        assertEquals(mutableListOf("block1", "block2"), userModel.getBlockList())
        parcel.recycle()
    }

    @Test
    fun testWriteToParcel() {
        val userModel = UserModel("1", "John Doe", "123456789")
        userModel.setBlockList(mutableListOf("block1", "block2"))
        val parcel = Parcel.obtain()
        userModel.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdFromParcel = UserModel.CREATOR.createFromParcel(parcel)
        assertEquals("1", createdFromParcel.getId())
        assertEquals("John Doe", createdFromParcel.getName())
        assertEquals("123456789", createdFromParcel.getPhone())
        assertEquals(mutableListOf("block1", "block2"), createdFromParcel.getBlockList())
        parcel.recycle()
    }

    @Test
    fun testSettersAndGetters() {
        val userModel = UserModel()
        userModel.setId("1")
        userModel.setName("Jane Doe")
        userModel.setPhone("987654321")
        userModel.setBlockList(mutableListOf("block1", "block2"))

        assertEquals("1", userModel.getId())
        assertEquals("Jane Doe", userModel.getName())
        assertEquals("987654321", userModel.getPhone())
        assertEquals(mutableListOf("block1", "block2"), userModel.getBlockList())
    }
}
