package entity

class Player(
    var name : String,
    var score : Int,
    var swapped : Boolean,
    var hand : List<Card>,
    var playerStack : List<Card>
) {
    /*fun addCard(card : Card) {}

    fun removeCard(card : Card) {}

    fun playCard(card : Card) {}*/
}