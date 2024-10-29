package entity
/**
 * Diese Klasse [Card] repräsenentiert eine Spielkarte.
 *
 * @property isHidden, ist ein Boolean Wert, der anzeigt, ob die Karte verdeckt ist.
 * @property suit, die Farbe der Karte, represäntiert durch CardSuit.
 * @property value, der Wert der Karte, represäntiert durch CardValue.
 *
 * @constructor Card, erstellt ein Card mit den eingegebenen Parametern.
 */
class Card(
    var isHidden : Boolean,
    val suit : CardSuit,
    val value : CardValue
)