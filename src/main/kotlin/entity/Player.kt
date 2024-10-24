package entity

class Player(
    var name : String,
    var score : Int,
    var swapped : Boolean,

    var hand : List<Card>,
    var playerStack : List<Card>
) {



}