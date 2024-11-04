package service

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/**
 * [RootServiceTest], Klasse die Tests für [GameService] und [PlayerActionService] bereitstellt.
 * [TestRefreshable] [TestRefreshable] wird verwendet, um das korrekte Aktualisierungsverhalten
 * zu überprüfen, auch wenn keine GUI vorhanden ist.
 */
class RootServiceTest {

    /**
     * Test des Hinzufügens eines einzelnen Refreshable zum RootService.
     */
    @Test
    fun testAddRefreshable(){
        val service = RootService()
        val testRefreshable = TestRefreshable(service)

        //Test: Die Refreshable werden ohne Fehler hinzugefügt
        assertDoesNotThrow { service.addRefreshable(testRefreshable) }
    }

    /**
     * Test des Hinzufügens mehrerer Refreshables zum RootService.
     */
    @Test
    fun testOnAllRefreshables(){
        val service = RootService()
        val testRefreshable1 = TestRefreshable(service)
        val testRefreshable2 = TestRefreshable(service)

        //Test: Die Refreshables werden ohne Fehler hinzugefügt
        assertDoesNotThrow { service.addRefreshables(testRefreshable1, testRefreshable2) }


    }
}