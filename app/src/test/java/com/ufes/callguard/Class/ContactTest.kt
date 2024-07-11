import android.os.Parcel
import com.ufes.callguard.Class.Contact
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ContactTest {

    @Test
    fun testDefaultConstructor() {
        val contact = Contact()
        assertEquals("", contact.name)
        assertEquals("", contact.number)
    }

    @Test
    fun testParameterizedConstructor() {
        val contact = Contact("Maria", "123456789")
        assertEquals("Maria", contact.name)
        assertEquals("123456789", contact.number)
    }

    @Test
    fun testSetGetName() {
        val contact = Contact()
        contact.setContactName("Maria")
        assertEquals("Maria", contact.getContactName())
    }

    @Test
    fun testSetGetNumber() {
        val contact = Contact()
        contact.setContactNumber("987654321")
        assertEquals("987654321", contact.getContactNumber())
    }

    @Test
    fun testParcel() {
        val contact = Contact("Maria", "123456789")
        val parcel = Parcel.obtain()
        contact.writeToParcel(parcel, contact.describeContents())
        parcel.setDataPosition(0)

        val createdFromParcel = Contact.CREATOR.createFromParcel(parcel)
        assertEquals(contact.name, createdFromParcel.name)
        assertEquals(contact.number, createdFromParcel.number)

        parcel.recycle()
    }
}