package entity
import kotlin.test.*
/**
 * Diese Klasse enthält Testfälle für die [Player] Klasse, um sicherzustellen, dass die
 * Spieler Eigenschaften korrekt initialisiert und geprüft werden.
 */
class PlayerTest {
    @Test
    fun testPlayer() {
        val player = Player(
            name = "Tom",                   //für name "Tom" eingegeben
            score = 30,                     //für score "30" eingegeben
            swapped = true,                 //für swapped ist true eingegeben
            hand = mutableListOf(),         //für hand ein mutableList eingegeben
            playerStack = mutableListOf()   //für playerStack ein mutableList eingegeben
        )
        assertEquals("Tom", player.name)   //Geprüft, ob Player name "Tom" ist.
        assertEquals(30,player.score)      //Geprüft, ob Player score "30" ist.
        assertTrue(player.swapped)                  //Geprüft, ob Player swapped action True ist.
        assertTrue(player.hand.isEmpty())           //Geprüft, ob Player's Hand leer ist.
        assertTrue(player.playerStack.isEmpty())    //Geprüft, ob Player's Stack leer ist.
    }
}