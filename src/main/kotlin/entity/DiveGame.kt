package entity

import java.util.*

/**
 * Diese Kalsse [DiveGame] repräsenentiert eine Spielkarte.
 *
 * @property currentPlayer, der Spieler, der gerade am Zug ist.
 * @property playerList, die Liste aller Spieler, die am Spiel teilnehmen.
 * @property discardStack, der Ablagestapel für Karten, die vom Spiel verworfen werden.
 * @property trio, eine Sammlung von Karten, die ein Trio darstellen.
 * @property drawStack, der Stapel, von dem die Spieler Karten ziehen
 *
 * @constructor DiveGame, erstellt ein neues DiveGame mit den angegebenen Spielern, Karten und Stapeln.
 */
class DiveGame(
    var currentPlayer: Player,
    var playerList: MutableList<Player>,
    var discardStack : MutableList<Card>,
    var trio : MutableList<Card>,
    var drawStack : Stack<Card> // kann auch mutableList sein
)