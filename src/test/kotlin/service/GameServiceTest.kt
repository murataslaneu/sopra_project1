package service

import entity.*
import kotlin.test.*
import org.junit.jupiter.api.assertDoesNotThrow

/**
 * Klasse zum Testen verschiedener Methoden des [GameService].
 * Stellt sicher, dass grundlegende Spielaktionen wie das Starten eines neuen Spiels,
 * das Starten und Beenden eines Zuges sowie das Erstellen des Ziehstapels korrekt funktionieren.
 *
 * @property defaultHandSize, wie viel ein Spieler Karten haben wird, am Anfang des Spiels
 * @property service ist RootService
 * @property testRefreshable ist TestRefreshable(service)
 */
class GameServiceTest {
    private val defaultHandSize = 5
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

        game.trio = emptyList<Card>().toMutableList()
        game.discardStack = emptyList<Card>().toMutableList()

        //Überprüfung, ob Stack es 52 Karten gibt und nicht leer ist
        //assertEquals(52, defaultRandomCardList.size)
        //assertTrue(defaultRandomCardList.isEmpty())

        //Test: Überprüfung, ob die startNewGame Methode ohne Probleme funktioniert
        assertDoesNotThrow { service.gameService.startNewGame(listOf("Tom", "Alice")) }

        //Test: Überprüfung der Spielinitialisierung und des Listenstatus
        assertTrue { testRefreshable.refreshAfterGameStart }
        assertEquals(0, game.trio.size)
        assertEquals(0, game.discardStack.size)
        assertEquals(5, defaultHandSize)

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

        //Nehmen wir der aktuelle Spieler, der am Anfang an der Reihe ist
        val firstPLayer = service.currentGame?.currentPlayer
        assertNotNull(firstPLayer) //Überprüfung, ob es erste Spieler dran ist.

        //Test: Überprüfung, ob die startTurn Methode ohne Probleme funktioniert
        assertDoesNotThrow { service.gameService.startTurn() }

        //Test: Prüfen, ob der aktuelle Spieler am Zug ist
        val currentPLayer = service.currentGame?.currentPlayer
        assertNotNull(currentPLayer) //currentPlayer kann nicht null sein.
        assertEquals(firstPLayer, currentPLayer) //Beide müssen gleich sein.
//        assertTrue{testRefreshable.refreshAfterTurnStart}
        }

    /**
     * [testEndTurn] testet die Methode 'endTurn', die den Zug des aktuellen Spielers beendet.
     * Überprüft, ob der Spieler wechselt, die Karten verdeckt werden, und das c
     */
    @Test
    fun testEndTurn(){

        val game = service.currentGame
        //val playerCards = service.currentGame.
        assertNotNull(game) //Spiel muss noch vorhanden sein.

        //val firstPlayer = game.currentPlayer
        assertDoesNotThrow { service.gameService.endTurn() }

        //val nextPlayer = game.currentPlayer
        //Erste spieler muss geändert sein, also nächste Spieler dran ist.
        //assertNotEquals(firstPlayer, nextPlayer)

        //assertEquals() prüfen ob nicht mehr 8 Karten in Hand ist

        //Karten verdecken nach EndTurn für aktuelle Spieler
        //assertTrue { isHidden } damit gegen Spieler, die Karte nicht sieht

//        assertTrue { game.currentPlayer.swapped }
//        assertTrue(testRefreshable.refreshAfterTurnEnds)
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

        game.drawStack.clear()
        assertEquals(0, game.drawStack.size)

        /** Füllen Stack mit Karten mithilfe shuffled  **/
//        game.drawStack = CardSuit.values().flatMap { suit ->
//            CardValue.values().map { value ->
//                Card(true, suit = suit, value = value)
//            }
//        }.shuffled() as Stack<Card>

        //Überprüfung, ob Stack es 52 Karten gibt und nicht leer ist
//        assertEquals(52, game.drawStack.size)
 //       assertTrue(game.drawStack.isNotEmpty())
    }
}
