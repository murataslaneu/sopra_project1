package entity

class Card(
    var isHidden : Boolean,

    var suit : CardSuit,
    var cValue : CardValue,

    var diveGame: DiveGame?

) {
}