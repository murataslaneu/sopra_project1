package entity

class DiveGame(
    var currentPlayer: Player,

    var playerList: List<Player>,
    var discradCard : List<Card>,
    var trio : List<Card>,
    var drawStack : List<Card>

)