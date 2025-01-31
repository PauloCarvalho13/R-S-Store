import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.rs.Announcer

class AnnouncerTests {

    @Test
    fun `create announcer with no email should throw a IllegalArgument Exception `(){
        assertThrows<IllegalArgumentException> { Announcer(1, "Greg","") }
    }

    @Test
    fun `create announcer with no name should throw a IllegalArgument Exception `() {
        assertThrows<IllegalArgumentException> { Announcer(1, "", "greg@gmail.com") }
    }

    @Test
    fun `create announcer with no name and no email should throw a IllegalArgument Exception `() {
        assertThrows<IllegalArgumentException> { Announcer(1, "", "") }
    }

    @Test
    fun `create announcer with name and email`() {
        val sut = Announcer(1, "Greg", "greg@gmail.com")
        assert(sut.userId == 1)
    }
}