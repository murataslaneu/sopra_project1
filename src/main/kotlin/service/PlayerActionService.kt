package service

import entity.*

/**
 * [PlayerActionService] ist verantwortlich für die verwaltung der
 * Spieler Aktionen im DiveGame. Diese Klasse ermöglicht es den Spielern,
 * Karten zu spielen, zu ziehen und zu tauschen.
 *
 * @param rootService, Hauptdienst, der Zugriff auf alle andere Dienstklassen
 * und den aktuellen Spielstatus bietet.
 *
 * @property currentPlayer, zeigt uns der aktuelle Spieler, der Aktionen durchführt.
 * @property trio, trio Sammlung von Karten, die in der Mitte des Spiels liegt.
 * @property drawStack, Stapel, von dem die Spieler Karten ziehen können.
 * @property swapped, Boolean, das angibt, ob eine Karte getauscht wurde (für jede Spieler).
 */
class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {
    var currentPlayer = rootService.gameService.currentPlayer
    val trio = rootService.gameService.currentGame.trio
    val drawStack = rootService.gameService.drawStack
    var swapped = rootService.gameService.currentGame.currentPlayer.swapped

    /**
     * [playCard] erlaubt es einem Spieler, eine Karte aus seiner Hand zu spielen.
     * Diese Karte wird in die Mitte gelegt, sofern sie den Spielregeln entspricht.
     * Die Karte muss sich auf der Hand des Spielers befinden oder der Nachziehstapel darf nicht leer sein.
     *
     * @param card, die Karte, die gespielt werden soll.
     *
     * @throws IllegalArgumentException, wenn das Spiel nicht läuft oder wenn der Spieler nicht an der Reihe ist.
     * @throws IllegalArgumentException, wenn die Karte nicht gültig ist.
     */
    fun playCard(card: Card){
        checkNotNull(rootService.currentGame)

        //Prüfen, ob alle Karten gleiche Farbe haben
        val allSameSuit = trio.all { it.suit == trio[0].suit }

        //Prüfen, ob alle Karten gleiche Zahl haben
        val allSameValue = trio.all { it.value == trio[0].value }

        /**
         * Wenn Spieler hat Karten in Hand, dann darf Spieler eine Karte spielen in der Mitte
         * dann wird diese Karte gelöscht (von Hand des Spielers) und wird in der Mitte gelegt
         */
        if(currentPlayer.hand.size > 0){
            currentPlayer.hand.remove(card)
            trio.add(card)
            onAllRefreshables { refreshAfterCardPlayed(card) }
                if(trio.size < 3 && trio.size != 0){
                    val middleCards = trio.last()
                    if(card.suit != middleCards.suit && card.value != middleCards.value){
                        throw IllegalArgumentException("Die Karte ist ungültig")
                    }
                }

                /**
                 * Wenn trio getriggert ist (trio == 3), wird Score für currentPlayer geändert
                 * und Mitte wir gelehrt.
                 */
                if(trio.size == 3){
                    if(allSameSuit || allSameValue){
                        currentPlayer.score += if(allSameValue) 20 else 5
                        //Addieren trio Karten in Stack des Spielers
                        currentPlayer.playerStack.addAll(trio)
                        trio.clear()
                        onAllRefreshables { refreshAfterCardPlayed(card) }
                    }
                }
                rootService.gameService.endTurn()
                onAllRefreshables { refreshAfterTurnEnds() }
            }else{
                drawCard()
                onAllRefreshables { refreshAfterCardDrawn(card) }
            }

        /**
         * Überprüft, ob der Spieler nicht an der Reihe ist.
         */
        if(currentPlayer != rootService.gameService.currentPlayer){
            throw IllegalArgumentException("Spieler ist nicht an der Reihe")
        }

    }

    /**
     * [drawCard] hilft Spieler, eine Karte von drawStack zu ziehen
     */
    fun drawCard(){
        if(drawStack.isNotEmpty()){
            val drawnCard = drawStack.pop()
            currentPlayer.hand.add(drawnCard)
            onAllRefreshables { refreshAfterCardDrawn(drawnCard) }
            //if spieler dieses Kart spielen möchte Anweisung addieren!
        }/*else{
            //endGame
            //return
        }*/

        if(currentPlayer.hand.size > 8){
            discardCard(currentPlayer.hand.last()!!)
        }
        onAllRefreshables { refreshAfterCardDiscarded(currentPlayer.hand.last()!!) }
    }

    /**
     * [swapCard] hilft Spieler, von Hand des Spielers (aktuellen) eine Karte zu ändern.
     *
     * @param playerCard, die Karte, die getauscht werden soll (von Hand des Spielers).
     * @param trioCard, die Karte im Trio, die der Spieler erhalten möchte.
     */
    fun swapCard(playerCard: Card, trioCard: Card){
        if(currentPlayer.hand.contains(playerCard) && trio.contains(trioCard)){
            currentPlayer.hand.remove(playerCard)
            currentPlayer.hand.add(trioCard)
            trio.remove(trioCard)
            trio.add(playerCard)
            swapped = true
            onAllRefreshables { refreshAfterCardSwap(playerCard,trioCard) }
        }
        /*if(swapped == true) {
            rootService.gameService.endTurn()
        }*/
    }

    /**
     * Entfernt eine angegebene Karte aus der Hand des Spielers und
     * fügt sie zum Ablagestapel.
     *
     * @param playerCard, die Karte, die abgeworfen werden soll.
     */
    private fun discardCard(playerCard: Card){
        if(currentPlayer.hand.contains(playerCard)){
            currentPlayer.hand.remove(playerCard)
            rootService.gameService.currentGame.discardStack.add(playerCard)
        }
        onAllRefreshables { refreshAfterCardDiscarded(playerCard) }
    }
}