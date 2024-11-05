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

        val playStack = game.trio
        game.currentPlayer.hand.remove(card)
        playStack.add(card)
        onAllRefreshables { refreshAfterCardPlayed(card) }

        if(game.currentPlayer.hand.isEmpty()) {
            drawCard()
            return
        }
        if(game.currentPlayer.hand.size > 8) {
            discardCard(card)
            onAllRefreshables { refreshAfterCardDiscarded(card) }
        }
        onAllRefreshables { refreshAfterTurnEnds() }
    }

    /**
     * [drawCard] hilft Spieler, eine Karte von drawStack zu ziehen
     */
    fun drawCard() {
        val game = rootService.currentGame
        checkNotNull(game)

        if(game.drawStack.isNotEmpty()) {
            val drawnCard = game.drawStack.pop()
            game.currentPlayer.hand.add(drawnCard)

            onAllRefreshables {
                refreshAfterCardDrawn(drawnCard)
                refreshAfterTurnEnds()
            }
        }else {
            if(game.playerList[0].hand.isEmpty() && game.playerList[1].hand.isEmpty() ) {
                rootService.gameService.isGameEnded()
            }
        }
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

        //Wenn der Spieler eine Karte der gleichen Serie oder des gleichen Wertes hat
        if(playerCard.suit == trioCard.suit || playerCard.value == trioCard.value) {
            playerCard.isHidden = true
            trioCard.isHidden = false

            //Tauschen Karten
            game.currentPlayer.hand.remove(playerCard)
            game.trio.add(playerCard)
            game.trio.remove(trioCard)
            game.currentPlayer.hand.add(trioCard)
            onAllRefreshables { refreshAfterCardSwap(playerCard, trioCard) }
        } else {
            onAllRefreshables { refreshAfterTurnEnds() }
        }
    }

    /**
     * Entfernt eine angegebene Karte aus der Hand des Spielers und
     * fügt sie zum Ablagestapel.
     *
     * @param playerCard, die Karte, die abgeworfen werden soll.
     */
    private fun discardCard(playerCard: Card) {
        val game = rootService.currentGame
        checkNotNull(game)

        val discardStack = game.discardStack
        val discardedCard = discardStack.last()
        if(game.currentPlayer.hand.size > 8){
            game.currentPlayer.hand.remove(playerCard)
            onAllRefreshables { refreshAfterCardDiscarded(discardedCard) }
        } else {
            onAllRefreshables { refreshAfterTurnEnds() }
        }
    }
}