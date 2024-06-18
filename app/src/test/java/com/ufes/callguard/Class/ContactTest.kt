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
    fun testContactDefaultConstructor() {
        val contact = Contact()
        assertEquals("", contact.getId())
        assertEquals("", contact.getName())
        assertEquals("", contact.getNumber())
        assertEquals(0, contact.getType())
    }

    @Test
    fun testContactParcelConstructor() {
        val parcel = Parcel.obtain()
        val contact = Contact("1", "John Doe", "123456789")
        contact.setType(1)

        contact.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdFromParcel = Contact.CREATOR.createFromParcel(parcel)
        assertEquals("1", createdFromParcel.getId())
        assertEquals("John Doe", createdFromParcel.getName())
        assertEquals("123456789", createdFromParcel.getNumber())
        assertEquals(1, createdFromParcel.getType())

        parcel.recycle()
    }

    @Test
    fun testContactWithArgumentsConstructor() {
        val contact = Contact("1", "John Doe", "123456789")
        assertEquals("1", contact.getId())
        assertEquals("John Doe", contact.getName())
        assertEquals("123456789", contact.getNumber())
        assertEquals(0, contact.getType())
    }

    @Test
    fun testContactWithTypeConstructor() {
        val contact = Contact(1)
        assertEquals(1, contact.getType())
    }

    @Test
    fun testSettersAndGetters() {
        val contact = Contact()
        contact.setId("1")
        contact.setName("John Doe")
        contact.setNumber("123456789")
        contact.setType(1)

        assertEquals("1", contact.getId())
        assertEquals("John Doe", contact.getName())
        assertEquals("123456789", contact.getNumber())
        assertEquals(1, contact.getType())
    }
}
