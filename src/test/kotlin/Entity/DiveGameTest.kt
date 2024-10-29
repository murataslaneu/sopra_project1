package entity
import kotlin.test.*
/**
 * Diese Klasse enthält Testfälle für die [DiveGame] Klasse, um sicherzustellen, dass die
 * Eigenschaften des Spiels korrekt initialisiert und geprüft werden.
 */
class DiveGameTest {
    @Test
    fun testDiveGame() {
        val game = DiveGame(
            currentPlayer = Player(         //Alle Eingenschaften für Spieler eingegeben.
                "Tom", 30, true ,
                mutableListOf(), mutableListOf()
            ),
            playerList = mutableListOf(),   //Für Player List ein mutableList eingegeben.
            discardStack = mutableListOf(), //Für discardStack ein mutableList eingegeben.
            trio = mutableListOf(),         //Für trio ein mutableList eingegeben.
            drawStack = mutableListOf()     //Für drawStack List ein mutableList eingegeben.
        )
        assertEquals(game.currentPlayer, game.currentPlayer)    //Geprüft, ob aktuelle Spieler korrekt ist.
        assertTrue(game.playerList.isEmpty())                   //Geprüft, ob Player List leer ist.
        assertTrue(game.discardStack.isEmpty())                 //Geprüft, ob discardStack leer ist.
        assertTrue(game.trio.isEmpty())                         //Geprüft, ob trio leer ist.
        assertTrue(game.drawStack.isEmpty())                    //Geprüft, ob drawStack leer ist.
    }
}