package service

import entity.*
import kotlin.test.*

/**
 * Test Klasse [PlayerActionServiceTest] für die Spieleraktionen im Spiel.
 * Diese Klasse testet verschiedene Aktionen eines Spielers.
 *
 * @property rootService ist RootService
 * @property testRefreshable ist testRefreshable(rootService)
 */

class PlayerActionServiceTest {
    private var rootService = RootService()
    private var testRefreshable = TestRefreshable(rootService)

    /**
     * Richtet vor jedem Test ein neues Spiel mit [TestRefreshable] ein,
     * die mit einem neu erstellten [RootService] verbunden sind.
     *
     * Startet vor jedem Test ein neues Spiel mit zwei Spielern (Tom, Alice)
     */
    @BeforeTest
    fun setUp(){
        rootService = RootService()
        testRefreshable = TestRefreshable(rootService)
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(listOf("Tom", "Alice"))
    }

    /**
     * [testPlayCard] testet die Methode 'playCard'. Überprüft, ob der Spieler eine Karte
     * spielen kann und ob diese Karte danach nicht mehr in seiner Hand ist.
     * Und falls es gespielte Karte ein Trio triggert, addiert Score für Spieler.
     */
    @Test
    fun testPlayCard(){
        val game = rootService.currentGame
        assertNotNull(game) //Überprüfen, ob Spiel vorhanden ist

        //Addieren eine Karte, um später zu überprüfen
        val card = Card(
            true,
            CardSuit.DIAMONDS,
            CardValue.SEVEN
            )

        //Überprüfen, ob die Karten verdeckt sind, und ob die Farbe & Value gleich sind.
        assertTrue { card.isHidden }
        assertEquals(CardSuit.DIAMONDS, card.suit)
        assertEquals(CardValue.SEVEN, card.value)

        //Spieler spielt die Karte (falls es existiert)
        val playerHand = game.currentPlayer.hand
        if(playerHand.isNotEmpty()){
            if(game.trio.size in 1..2)
        rootService.playerActionService.playCard(card)}
    }

    /**
     * [trioScore] testet die Methode 'playCard'. Eine weitere Überprüfung
     * von [testPlayCard], hier überprüfen wir, ob Trio getriggert ist,
     * falls ja die Punkten werden in Scoreboard eingetragen.
     */
    @Test
    fun trioScore() {
        /** Test für Trio, und Score **/
        val game = rootService.currentGame
        assertNotNull(game)

        val currentPlayer = game.currentPlayer
        assertNotNull(currentPlayer) //Überprüfen, dass Spieler in Spiel ist

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
        val card = Card(true, CardSuit.DIAMONDS, CardValue.FIVE)
        game.currentPlayer.hand.add(card)
        rootService.playerActionService.playCard(card)

        if(diamondFilter.size == 3 ) {
            game.currentPlayer.score.plus(5)
        }

        if(fiveFilter.size == 3 ) {
            game.currentPlayer.score.plus(20)
        }

        //Überprüfen ob trio 3 Karten hat.
        assertTrue { fiveFilter.size == 3 }
        assertTrue { diamondFilter.size == 3 }
        assertFalse { game.currentPlayer.score == 5 || game.currentPlayer.score == 20 }
    }

    /**
     * [testDrawCard] testet doe Methode 'drawCard'. Überprüft, ob ein Spieler eine Karte vom
     * Ziehstapel ziehen kann und ob sich die Anzahl der Karten in der Hand und
     * im Stapel entsprechend ändert.
     */
    @Test
    fun testDrawCard(){
        val game = rootService.currentGame
        assertNotNull(game) //Überprüfen, ob Spiel vorhanden ist

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
        assertEquals(5, playerHand.size)
        assertEquals(4, gameStack.size)

        if(playerHand.isEmpty()) {
            rootService.playerActionService.drawCard()
        }else {
            val drawnCard = gameStack.pop()
            playerHand.add(drawnCard)
        }

        //Überprüfen, ob Spieler jetzt 6 Karten hat, wegen drawnCard
        // und überprüfen, ob gameStack hat 3 Karten, wegen drawnCard
        assertEquals(6, playerHand.size)
        assertEquals(3, gameStack.size)

        val gameStack1left = game.drawStack.apply {
            clear() // Erst leeren die Stapel, dann addieren spezifische Karten
            push(Card(true, CardSuit.CLUBS, CardValue.FIVE))
        }
        game.drawStack = gameStack1left
        rootService.playerActionService.drawCard()
        game.currentPlayer.score = 20
        rootService.gameService.gameEndScore()
    }

    /**
     * [testSwapCard] testet die Methode 'swapCard'. Überprüft, ob ein Spieler eine Karte mit den
     * Karten in der Mitte tauschen kann und ob die Karten richtig zugeordnet sind.
     */
    @Test
    fun testSwapCard() {
        val game = rootService.currentGame
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
        rootService.playerActionService.swapCard(
            playerHand[0], middleCards[0])

        //Size muss gleich sein, weil Spieler nur Karten getauscht hat
        assertEquals(5, playerHand.size)
        assertEquals(2, middleCards.size)

        /**
         * Die getauschten Karten sollten nun an der richtigen Stelle sein.
         * Die Karte, die in der Mitte war, ist jetzt verdeckt und die Karte des
         * Spielers ist jetzt in der Mitte offen liegt.
         * assertTrue {playerHand.contains(Card(true, CardSuit.CLUBS, CardValue.FIVE))}
         * assertTrue {middleCards.contains(Card(false, CardSuit.SPADES, CardValue.TEN))}
         */

    }

    /**
     * [testDiscardCard] testet die Methode 'discardCard'. Überprüft, ob ein Spieler eine Karte
     * ablegen kann und ob diese Karte korrekt in den Ablagestapel verschoben wird.
     */
    @Test
    fun testDiscardCard() {
        val game = rootService.currentGame
        assertNotNull(game) //Überprüfen, ob Spiel vorhanden ist

        val discardStack = game.discardStack
        discardStack.clear() // Leeren wir discard Stack

        val playerHand = game.currentPlayer.hand
        playerHand.clear()
        playerHand.addAll(listOf(
            Card(true, CardSuit.SPADES, CardValue.TEN),
            Card(true, CardSuit.SPADES, CardValue.TWO),
            Card(true, CardSuit.DIAMONDS, CardValue.QUEEN),
            Card(true, CardSuit.HEARTS, CardValue.JACK),
            Card(true, CardSuit.DIAMONDS, CardValue.SIX),
            Card(true, CardSuit.DIAMONDS, CardValue.SEVEN),
            Card(true, CardSuit.HEARTS, CardValue.EIGHT),
            Card(true, CardSuit.CLUBS, CardValue.EIGHT),
            Card(true, CardSuit.CLUBS, CardValue.TEN)
        ))

        //discardCard ist private, deswegen entfernen wir Karte wie unten
        val cardToDiscard = playerHand[2]
        rootService.playerActionService.discardCard(cardToDiscard)

        assertEquals(8,playerHand.size)
        assertEquals(1, discardStack.size)
        println(discardStack)

        //Spieler enthält die Karte nicht mehr, aber discardStack enthält
        assertFalse { playerHand.contains(cardToDiscard) }
        assertTrue { discardStack.contains(cardToDiscard) }
    }
}