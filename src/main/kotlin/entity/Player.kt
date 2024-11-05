package entity
/**
 * Diese Kalsse repräsenentiert einen Spieler [Player] im Spiel.
 *
 * @property name, Name des Spielers.
 * @property score, aktuell Punktestand des Spielers.
 * @property swapped, ist ein Boolean Wert, der anzeigt, ob der Spieler Karten getauscht hat.
 * @property hand, die aktuelle Hand des Spielers.
 * @property playerStack, der Stapel der Karten des Spielers.
 *
 * @constructor Player, erstellt ein Player mit den eingegebenen Parametern
 */
class Player(
    val name : String,
    var score : Int,
    var swapped : Boolean,
    var hand : MutableList<Card?>,
    val playerStack : MutableList<Card?>
)