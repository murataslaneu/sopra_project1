package service

import entity.*

/**
 * [PlayerActionService] ist verantwortlich für die verwaltung der
 * Spieler Aktionen im DiveGame. Diese Klasse ermöglicht es den Spielern,
 * Karten zu spielen, zu ziehen und zu tauschen.
 *
 * @param rootService, Hauptdienst, der Zugriff auf alle andere Dienstklassen
 * und den aktuellen Spielstatus bietet.
 */
class PlayerActionService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * [playCard] erlaubt es einem Spieler, eine Karte aus seiner Hand zu spielen.
     * Diese Karte wird in die Mitte gelegt, sofern sie den Spielregeln entspricht.
     * Die Karte muss sich auf der Hand des Spielers befinden oder der Nachziehstapel darf nicht leer sein.
     *
     * @param card, die Karte, die gespielt werden soll.
     */
    fun playCard(card: Card) {
        val game = rootService.currentGame
        checkNotNull(game)
        checkNotNull(game.currentPlayer)
        if (!game.currentPlayer.hand.contains(card)) { throw IllegalArgumentException("Card not found in hand") }
        if(!isValidTrio(playerCard = card)) throw IllegalArgumentException("Nicht valide Card")

        val playArea = game.trio
        if(playArea.isEmpty() || playArea.any { it.suit == card.suit || it.value == card.value }) {
            game.currentPlayer.hand.remove(card)
            playArea.add(card)

            val trioValueScore = 20
            val trioSuitScore = 5
            if(game.trio.size == 3) {
                val firstSuit = game.trio[0].suit
                val firstValue = game.trio[0].value

                if(game.trio.all { it.suit == firstSuit }) {
                    game.currentPlayer.score += trioSuitScore
                } else if (game.trio.all { it.value == firstValue }) {
                    game.currentPlayer.score += trioValueScore
                } else {
                    throw IllegalArgumentException("Trio is neither valid suit nor value")
                }
                game.currentPlayer.playerStack.addAll(playArea)
                playArea.clear()
                game.playerList.forEach {it.swapped = false}
            }
        } else { throw IllegalArgumentException("Trio is neither valid suit nor value")}
        onAllRefreshables { refreshAfterCardPlayed(card) }
    }

    /**
     * [drawCard] hilft Spieler, eine Karte von drawStack zu ziehen
     */
    fun drawCard() {
        val game = rootService.currentGame
        checkNotNull(game)
        checkNotNull(game.currentPlayer)

        if(game.drawStack.isNotEmpty()) {
            val drawnCard = game.drawStack.pop()
            val currentPlayer = game.currentPlayer
            currentPlayer.hand.add(drawnCard)
            onAllRefreshables { refreshAfterCardDrawn(drawnCard) }
            //Nach Ziehen eine Karte, falls es letzte Karte ist, Spiel endet.
            if(game.drawStack.isEmpty()) { rootService.gameService.isGameEnded() }
        }else if(game.currentPlayer.hand.size > 8) {
            throw IllegalArgumentException("You need to discard 1 card or play the last card before end the Turn!")
        }else { rootService.gameService.isGameEnded() }
    }

    /**
     * [swapCard] hilft Spieler, von Hand des Spielers (aktuellen) eine Karte zu ändern.
     *
     * @param playerCard, die Karte, die getauscht werden soll (von Hand des Spielers).
     * @param trioCard, die Karte im Trio, die der Spieler erhalten möchte.
     */
    fun swapCard(playerCard: Card, trioCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game)
        checkNotNull(game.currentPlayer)
        require(!game.currentPlayer.swapped) {"You already swapped!"}

        val playArea = game.trio
        if(!game.currentPlayer.swapped) {
            //Wenn der Spieler eine Karte der gleichen Serie oder des gleichen Wertes hat
            if (playArea.all { it.suit == trioCard.suit || it.value == trioCard.value &&
                        (playArea.size in 1..2) }) {
                //Tauschen Karten und setzen Boolean Werte im gegenteil
                game.trio.add(playerCard)
                game.currentPlayer.hand.remove(playerCard)
                game.currentPlayer.hand.add(trioCard)
                game.trio.remove(trioCard)
                game.currentPlayer.swapped = true
                playerCard.isHidden = true
                trioCard.isHidden = false
                onAllRefreshables { refreshAfterCardSwap(playerCard, trioCard) }
            } else { throw IllegalArgumentException("Trio is neither valid suit nor value") }
        }
    }

    /**
     * Entfernt eine angegebene Karte aus der Hand des Spielers und
     * fügt sie zum Ablagestapel.
     *
     * @param playerCard, die Karte, die abgeworfen werden soll.
     */
    fun discardCard(playerCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game)
        checkNotNull(game.currentPlayer)
        if (!game.currentPlayer.hand.contains(playerCard)) { throw IllegalArgumentException("Card not found in hand") }

        if(game.currentPlayer.hand.size > 8){
            game.currentPlayer.hand.remove(playerCard)
            game.discardStack.add(playerCard)
            onAllRefreshables { refreshAfterCardDiscarded(playerCard) }
        }
    }

    /**
     * [isValidTrio] überprüft ob die Karten gleiche Suit oder Value haben, damit
     * Spieler die Karte, die in der Hand hat, spielen kann.
     *
     * @param playerCard nimmt die Karte von player und überprüft es mit Trio Karten
     */
    private fun isValidTrio(playerCard: Card): Boolean {
        val game = rootService.currentGame
        checkNotNull(game)

        val validSuitValue: (Card, Card) -> Boolean = {card1, card2 ->
            card1.suit == card2.suit || card1.value == card2.value
        }
        return game.trio.all { validSuitValue(it, playerCard) }
    }
}