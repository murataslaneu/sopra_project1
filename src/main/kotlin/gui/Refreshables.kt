package gui

import entity.Card

/**
 * Die [Refreshables] Interface definiert Methoden, die von Klassen implementiert
 * werden müssen, die aktualisiert werden sollen, wenn sich der Spielzustand ändert.
 *
 * Dieses Interface stellt sicher, dass alle relevante Komponenten des Spiels
 * über die verschiedenen Ereignisse während des Spiels informiert werden und
 * ihre Anzeigen entsprechend aktualisieren können.
 */
interface Refreshables {
    /**
     *  Wird aufgerufen, nachdem Spiel gestartet wurde.
     *  Verwendet für die Benutzeroberfläche oder andere
     *  Anzeigen für den Spielstart zu aktualisieren.
     */
    fun refreshAfterGameStart()

    /**
     * Wird aufgerufen, wenn ein neuer Zug beginnt.
     * Verwendet für die Benutzeroberfläche für den
     * Start eines neuen Zugs zu aktualisieren.
     */
    fun refreshAfterTurnStart()

    /**
     * Wird aufgerufen, wenn ein Zug endet.
     * Verwendet für die Benutzeroberfläche für das
     * Ende eines Zugs zu aktualisieren.
     */
    fun refreshAfterTurnEnds()

    /**
     * Wird aufgerufen, nachdem ein Spieler eine Karte gespielt hat.
     *
     * @param playedCard, Karte, die vom Spieler gespielt wurde.
     */
    fun refreshAfterCardPlayed(playedCard: Card)

    /**
     * Wird aufgerufen, nachdem eine Karte gezogen wurde.
     *
     * @param card, Karte, die vom Nachziehstapel gezogen wurde.
     */
    fun refreshAfterCardDrawn(card: Card)

    /**
     * Wird aufgerufen, nachdem zwei Karten getauscht wurden.
     *
     * @param trioCard, Karte, die im Trio ist.
     * @param handCard, Karte, die aus der Hand des Spielers stammt.
     */
    fun refreshAfterCardSwap(trioCard: Card, handCard: Card)

    /**
     * Wird aufgerufen, nachdem eine Karte aus der Hand des
     * Spielers abgeworfen wurde.
     *
     * @param handCard, Karte, die abgeworfen wurde.
     */
    fun refreshAfterCardDiscarded(handCard: Card)

    /**
     * Wird aufgerufen, nachdem das Spiel endet.
     * Verwendet für die Benutzeroberfläche für
     * das Spielende zu aktualisieren.
     */
    fun refreshAfterGameEnds()

}