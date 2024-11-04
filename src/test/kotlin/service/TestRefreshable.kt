package service

import entity.*
import gui.Refreshables

/**
 * [Refreshables] Implementierung, die nichts auffrischt, sich aber merkt,
 * wenn eine Refresh-Methode aufgerufen wurde, seit dem letztem [reset]
 *
 * @param service zu dem dieser Dienst gehört.
 *
 * @property refreshAfterGameStart, Boolean Wert zeigt einfach, ob funktioniert
 * @property refreshAfterTurnStart, Boolean Wert zeigt einfach, ob funktioniert
 * @property refreshAfterTurnEnds, Boolean Wert zeigt einfach, ob funktioniert
 * @property refreshAfterCardPlayed, Boolean Wert zeigt einfach, ob funktioniert
 * @property refreshAfterCardDrawn, Boolean Wert zeigt einfach, ob funktioniert
 * @property refreshAfterCardSwap, Boolean Wert zeigt einfach, ob funktioniert
 * @property refreshAfterCardDiscarded, Boolean Wert zeigt einfach, ob funktioniert
 * @property refreshAfterGameEnds, Boolean Wert zeigt einfach, ob funktioniert
 */
class TestRefreshable(val service: RootService): Refreshables {
    var refreshAfterGameStart: Boolean = false
        private set

    var refreshAfterTurnStart: Boolean = false
    private set

    var refreshAfterTurnEnds: Boolean = false
    private set

    var refreshAfterCardPlayed: Boolean = false
    private set

    var refreshAfterCardDrawn: Boolean = false
    private set

    var refreshAfterCardSwap: Boolean = false
    private set

    var refreshAfterCardDiscarded: Boolean = false
    private set

    var refreshAfterGameEnds: Boolean = false
    private set

    /**
     * Hilft uns alles zu zurücksetzen
     */
    fun reset(){
        refreshAfterGameStart = false
        refreshAfterTurnStart = false
        refreshAfterTurnEnds = false
        refreshAfterCardPlayed = false
        refreshAfterCardDrawn = false
        refreshAfterCardDiscarded = false
        refreshAfterCardSwap = false
        refreshAfterGameEnds = false
    }

    override fun refreshAfterGameStart() {
        refreshAfterGameStart = true
    }

    override fun refreshAfterTurnStart() {
        refreshAfterTurnStart = true
    }

    override fun refreshAfterTurnEnds() {
        refreshAfterTurnEnds = true
    }

    override fun refreshAfterCardPlayed(playedCard: Card) {
        refreshAfterCardPlayed = true
    }

    override fun refreshAfterCardDrawn(card: Card) {
        refreshAfterCardDrawn = true
    }

    override fun refreshAfterCardDiscarded(handCard: Card) {
        refreshAfterCardDiscarded = true
    }

    override fun refreshAfterCardSwap(trioCard: Card, handCard: Card) {
        refreshAfterCardSwap = true
    }

    override fun refreshAfterGameEnds() {
        refreshAfterGameEnds = true
    }
}
