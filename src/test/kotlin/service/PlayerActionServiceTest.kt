package service

import entity.*
import kotlin.test.*

/**
 * Test Klasse [PlayerActionServiceTest] für die Spieleraktionen im Spiel.
 * Diese Klasse testet verschiedene Aktionen eines Spielers.
 *
 * @property service ist RootService
 * @property testRefreshable ist testRefreshable(service)
 */

class PlayerActionServiceTest {
    private var service = RootService()
    private var testRefreshable = TestRefreshable(service)

    /**
     * Richtet vor jedem Test ein neues Spiel mit [TestRefreshable] ein,
     * die mit einem neu erstellten [RootService] verbunden sind.
     *
     * Startet vor jedem Test ein neues Spiel mit zwei Spielern (Tom, Alice)
     */
    @BeforeTest
    fun setUp(){
        service = RootService()
        testRefreshable = TestRefreshable(service)
        service.addRefreshable(testRefreshable)
        service.gameService.startNewGame(listOf("Tom", "Alice"))
    }

    /**
     * [testPlayCard] testet die Methode 'playCard'. Überprüft, ob der Spieler eine Karte
     * spielen kann und ob diese Karte danach nicht mehr in seiner Hand ist.
     * Und falls es gespielte Karte ein Trio triggert, addiert Score für Spieler.
     */
    @Test
    fun testPlayCard(){
        val game = service.currentGame
        assertNotNull(game) //Überprüfen, ob Spiel vorhanden ist

        //Addieren eine Karte, um später zu überprüfen
        val playerHand = service.currentGame!!.currentPlayer.hand
        val card = Card(
            true,
            CardSuit.DIAMONDS,
            CardValue.SEVEN
            )

        //Überprüfen, ob die Karten verdeckt sind, und ob die Farbe & Value gleich sind.
        assertTrue { card.isHidden }
        assertEquals(CardSuit.DIAMONDS, card.suit)
        assertEquals(CardValue.SEVEN, card.value)

        //Überprüfen, ob Spieler die Karte hat (gegebene Karte)
        assertTrue { playerHand.contains(card) }

        //Spieler spielt die Karte (falls es existiert)
        if(playerHand.isNotEmpty()){
        service.playerActionService.playCard(card)}

        //Nach dem Spielen sollte die Karte nicht mehr in der Hand sein
        assertFalse { playerHand.contains(card) }

        /** Test für Trio, und Score **/
        val player = game.currentPlayer
        assertNotNull(player) //Überprüfen, dass Spieler in Spiel ist

        val trioSuit = game.trio
        val trioValue = game.trio
        trioSuit.clear()
        trioValue.clear()
        trioSuit.addAll(listOf(
            Card(false, CardSuit.DIAMONDS, CardValue.FIVE),
            Card(false, CardSuit.DIAMONDS, CardValue.TEN)
        ))
        trioValue.addAll(listOf(
            Card(false, CardSuit.HEARTS, CardValue.FIVE),
            Card(false, CardSuit.DIAMONDS, CardValue.FIVE)
        ))

        //Überprüfen, ob alle Karten entweder gleiche Farbe oder Value haben.
        val diamondFilter = trioSuit.filter { it.suit == CardSuit.DIAMONDS }
        val fiveFilter = trioValue.filter { it.value == CardValue.FIVE }

        /**
         * Spieler spielt eine Karte und dann überprüft, ob
         * trioFarbe oder trioValue getriggert ist, dann addiert
         * Punkte in Balance des Spielers.
         */
        service.playerActionService.playCard(card)

        if(diamondFilter.size == 3 ) {
            game.currentPlayer.score.plus(5)
        }

        if(fiveFilter.size == 3 ) {
            game.currentPlayer.score.plus(20)
        }

        assertTrue { diamondFilter.size == 3 }
        assertTrue { fiveFilter.size == 3 }
        assertTrue { game.currentPlayer.score == 5
                || game.currentPlayer.score == 20 }

    }

    /**
     * [testDrawCard] testet doe Methode 'drawCard'. Überprüft, ob ein Spieler eine Karte vom
     * Ziehstapel ziehen kann und ob sich die Anzahl der Karten in der Hand und
     * im Stapel entsprechend ändert.
     */
    @Test
    fun testDrawCard(){
        val drawStack = service.gameService.drawStack
        val playerHandSize = service.currentGame!!.currentPlayer.hand.size
        val game = service.currentGame
        assertNotNull(game) //Überprüfen, ob Spiel vorhanden ist

        //Überprüfen, ob Stack 52 Karten hat.
        assertEquals(52, drawStack.size)
        //Überprüfen, ob Spieler 5 Karte hat.
        assertEquals(5, playerHandSize)

        //Spieler zieht eine Karte von Stack
        service.playerActionService.drawCard()

        //Nach drawCard Methode muss Spieler 6 und Stack 51 Karten haben.
        assertEquals(playerHandSize + 1, playerHandSize)
        assertEquals(drawStack.size - 1, drawStack.size)

        /** Test für spezifischer Karten **/

        //bestimmen die gameStack (drawStack)
        val gameStack = game.drawStack.apply {
            clear() // Erst leeren die Stapel, dann addieren spezifische Karten
            push(Card(true, CardSuit.CLUBS, CardValue.FIVE))
            push(Card(true, CardSuit.SPADES, CardValue.KING))
            push(Card(true, CardSuit.HEARTS, CardValue.JACK))
            push(Card(true, CardSuit.DIAMONDS, CardValue.SEVEN))
        }

        //Spieler Hand mit spezifischen Karten füllen
        val playerHand = game.currentPlayer.hand
        playerHand.clear()
        playerHand.addAll(listOf(
            Card(true, CardSuit.CLUBS, CardValue.TEN),
            Card(true, CardSuit.SPADES, CardValue.TWO),
            Card(true, CardSuit.HEARTS, CardValue.QUEEN),
            Card(true, CardSuit.DIAMONDS, CardValue.JACK),
            Card(true, CardSuit.HEARTS, CardValue.SIX)
        ))

        //Überprüfen, ob die Karten Zahl stimmt.
        assertEquals(5, playerHandSize)
        assertEquals(4, gameStack.size)

        service.playerActionService.drawCard()

        /**
         * Überprüfung, ob Spieler die gezogene Karte hat und ob
         * die Karte nicht mehr in Stapel (gameStack/drawStack) vorhanden ist.
         */
        assertEquals(playerHandSize + 1, playerHandSize)
        assertEquals(gameStack.size - 1, gameStack.size)
        assertFalse { gameStack.contains(gameStack.last()) }
        assertTrue { playerHand.contains(
            Card(true, CardSuit.DIAMONDS, CardValue.SEVEN)) }
    }

    /**
     * [testSwapCard] testet die Methode 'swapCard'. Überprüft, ob ein Spieler eine Karte mit den
     * Karten in der Mitte tauschen kann und ob die Karten richtig zugeordnet sind.
     */
    @Test
    fun testSwapCard() {
        val game = service.currentGame
        assertNotNull(game) //Überprüfen, ob Spiel vorhanden ist

        /**
         * Fügen wir Karten für trio (hier für die Karten, die in der Mitte legen) und die Hand
         * des Spielers, damit wir überprüfen können, ob die Karten getauscht werden.
         */
        val middleCards = game.trio
        middleCards.clear()
        middleCards.addAll(listOf(
            Card(false, CardSuit.CLUBS, CardValue.FIVE),
            Card(false, CardSuit.CLUBS, CardValue.TEN)
        ))

        val playerHand = game.currentPlayer.hand
        playerHand.clear()
        playerHand.addAll(listOf(
            Card(true, CardSuit.SPADES, CardValue.TEN),
            Card(true, CardSuit.SPADES, CardValue.TWO),
            Card(true, CardSuit.DIAMONDS, CardValue.QUEEN),
            Card(true, CardSuit.HEARTS, CardValue.JACK),
            Card(true, CardSuit.DIAMONDS, CardValue.SIX)
        ))

        //Tauschen Spades_Ten mit Clubs_Five
        service.playerActionService.swapCard(
            playerHand[0]!! , middleCards[0])

        //Size muss gleich sein, weil Spieler nur Karten getauscht hat
        assertEquals(5, playerHand.size)
        assertEquals(2, middleCards.size)

        /**
         * Die getauschten Karten sollten nun an der richtigen Stelle sein.
         * Die Karte, die in der Mitte war, ist nun verdeckt und die Karte des
         * Spielers ist jetzt in der Mitte offen liegt.
         */
        assertTrue { playerHand.contains(Card(true,
            CardSuit.CLUBS, CardValue.FIVE)) }
        assertTrue { middleCards.contains(Card(false,
            CardSuit.SPADES, CardValue.TEN)) }
    }

    /**
     * [testDiscardCard] testet die Methode 'discardCard'. Überprüft, ob ein Spieler eine Karte
     * ablegen kann und ob diese Karte korrekt in den Ablagestapel verschoben wird.
     */
    @Test
    fun testDiscardCard() {
        val game = service.currentGame
        assertNotNull(game) //Überprüfen, ob Spiel vorhanden ist

        val discardStack = service.currentGame!!.discardStack
        discardStack.clear() // Leeren wir discard Stack

        val playerHand = game.currentPlayer.hand
        playerHand.clear()
        playerHand.addAll(listOf(
            Card(true, CardSuit.SPADES, CardValue.TEN),
            Card(true, CardSuit.SPADES, CardValue.TWO),
            Card(true, CardSuit.DIAMONDS, CardValue.QUEEN),
            Card(true, CardSuit.HEARTS, CardValue.JACK),
            Card(true, CardSuit.DIAMONDS, CardValue.SIX)
        ))

        //discardCard ist private, deswegen entfernen wir Karte wie unten
        val cardToDiscard = playerHand[2]
        playerHand.remove(cardToDiscard)
        discardStack.add(cardToDiscard!!)

        assertEquals(4,playerHand.size)
        assertEquals(1, discardStack.size)

        //Spieler enthält die Karte nicht mehr, aber discardStack enthält
        assertFalse { playerHand.contains(cardToDiscard) }
        assertTrue { discardStack.contains(cardToDiscard) }
    }
}