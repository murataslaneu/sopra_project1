package service

import entity.*
import kotlin.test.*
import org.junit.jupiter.api.assertDoesNotThrow

/**
 * Klasse zum Testen verschiedener Methoden des [GameService].
 * Stellt sicher, dass grundlegende Spielaktionen wie das Starten eines neuen Spiels,
 * das Starten und Beenden eines Zuges sowie das Erstellen des Ziehstapels korrekt funktionieren.
 *
 * @property service ist RootService
 * @property testRefreshable ist TestRefreshable(service)
 */
class GameServiceTest {
    private var service = RootService()
    private var testRefreshable = TestRefreshable(service)

    /**
     *Richtet vor jedem Test ein neues Spiel mit [TestRefreshable]s ein,
     *  die mit einem neu erstellten [RootService] verbunden sind.
     * Startet vor jedem Test ein neues Spiel mit zwei Spielern, (Tom, Alice) und jeweils 5 Karten.
     */

    @BeforeTest
    fun setUp() {
        service = RootService()
        testRefreshable = TestRefreshable(service)
        service.addRefreshable(testRefreshable)
        service.gameService.startNewGame(listOf("Tom", "Alice"))
    }

    /**
     * [testStartNewGame] testet die Methode 'startNewGame', in dem ein neues Spiel gestartet wird.
     * Überprüft, ob das Spiel korrekt initialisiert ist, einschließlich des leeren Trios (mitte Karten)
     * und Ablagestapels sowie der Anzahl der Spieler und deren Namen. Spiel starten mit 52 Karten.
     */
    @Test
    fun testStartNewGame(){
        val game = service.currentGame
        assertNotNull(game)

        /** Überprüfen ob playerStack 52 hat **/
        val player1Hand = game.playerList[0].hand
        val player2Hand = game.playerList[1].hand

        val playerStack = player1Hand + player2Hand + game.drawStack
        assertEquals(52, playerStack.size)
                        /****/

        val drawStack = game.drawStack
        //Überprüfung, ob Stack 42 Karten hat
        assertEquals(42, drawStack.size)

        //Hier erst löschen wir, weil in StartGame steht, dass Spieler bekommen 5 Karten
        game.currentPlayer.hand.clear()
        repeat(5) {
            if(drawStack.isNotEmpty()) {
                val drawnCard = drawStack.pop()
                game.currentPlayer.hand.add(drawnCard)
            }
        }
        assertEquals(5, game.currentPlayer.hand.size)

        //Erstellen empty Lists
        game.trio = emptyList<Card>().toMutableList()
        game.discardStack = emptyList<Card>().toMutableList()

        //Überprüfung, ob Stack 42-5=37 Karten gibt und nicht leer ist
        assertEquals(37, drawStack.size)
        assertFalse{drawStack.isEmpty()}

        //Test: Überprüfung, ob die startNewGame Methode ohne Probleme funktioniert
        assertDoesNotThrow { service.gameService.startNewGame(listOf("Tom", "Alice")) }

        //Test: Überprüfung der Spielinitialisierung und des Listenstatus
        assertTrue { testRefreshable.refreshAfterGameStart }
        assertEquals(0, game.trio.size)
        assertEquals(0, game.discardStack.size)

        //Test: Überprüfung der Spieler
        assertEquals(2, game.playerList.size)
        assertEquals("Tom", game.playerList[0].name)
        assertEquals("Alice", game.playerList[1].name)

        /** Überprüft, ob das Refreshable Objekt aktualisiert wird. **/
        assertTrue { testRefreshable.refreshAfterGameStart }
    }

    /**
     * [testStartTurn] testet die Methode 'startTurn', die einen neuen Spielzug für den aktuellen Spieler startet.
     * Überprüft, ob der aktuelle Spieler am Zug ist und ob die Methode ohne Fehler ausgeführt wird.
     * Überprüft, ob das Refreshable Objekt aktualisiert wird.
     */
    @Test
    fun testStartTurn(){
        val game = service.currentGame
        assertNotNull(game)

        //Nehmen wir der aktuelle Spieler, der am Anfang an der Reihe ist
        val firstPLayer = game.currentPlayer
        assertNotNull(firstPLayer) //Überprüfung, ob es erste Spieler dran ist.

        //Test: Überprüfung, ob die startTurn Methode ohne Probleme funktioniert
        assertDoesNotThrow { service.gameService.startTurn() }

        //Test: Prüfen, ob der aktuelle Spieler am Zug ist
        val currentPLayer = game.currentPlayer
        assertNotNull(currentPLayer) //currentPlayer kann nicht null sein.
        assertEquals(firstPLayer, currentPLayer) //Beide müssen gleich sein.
    }



    /**
     * [testEndTurn] testet die Methode 'endTurn', die den Zug des aktuellen Spielers beendet.
     * Überprüft, ob der Spieler wechselt, die Karten verdeckt werden, und das c
     */
    @Test
    fun testEndTurn(){
        val game = service.currentGame
        assertNotNull(game) //Spiel muss noch vorhanden sein.

        //Überprüfen, ob erste ausgewählte Spieler im Spiel dran ist.
        val firstPlayer = game.currentPlayer
        assertEquals(game.currentPlayer, firstPlayer)

        //Karten verdecken nach EndTurn für aktuelle Spieler, damit gegen Spieler, die Karte nicht sieht
        service.gameService.endTurn()
        val playerCards = game.currentPlayer.hand.all { it!!.isHidden }
        assertTrue{ playerCards }

        //Test für refreshable Turn Ends
        assertTrue(testRefreshable.refreshAfterTurnEnds)
        assertDoesNotThrow {service.gameService.endTurn()}
    }

    /**
     * [testCreateDrawStack] testet das Erstellen des Ziehstapels 'drawStack', indem sichergestellt wird,
     * dass dieser Stapel zu Beginn leer ist und anschließend Karten enthält.
     */
    @Test
    fun testCreateDrawStack(){
        val game = service.currentGame
        assertNotNull(game) //Spiel muss noch vorhanden sein.

        val drawStack = game.drawStack

        //Überprüfung, ob in Stack 42 Karten gibt und nicht leer ist
        assertTrue(drawStack.isNotEmpty())
        assertEquals(42, drawStack.size)

        //Überprüfung, ob drawStack gelehrt werden
        game.drawStack.clear()
        assertEquals(0, game.drawStack.size)

        //Weil, drawStack leer ist, muss isGameEnded true sein
        service.gameService.isGameEnded()
        assertTrue { service.gameService.isGameEnded() }

    }
}
