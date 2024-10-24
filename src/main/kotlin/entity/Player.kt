package entity

class Player(
    var name : String,
    var score : Int,
    var swapped : Boolean,
    var hand : MutableList<Card?>,
    var playerStack : MutableList<Card?>
) {

}