package entity

class DiveGame(
    var currentPlayer: Player,
    var playerList: MutableList<Player>,
    var discradCard : MutableList<Card>,
    var trio : MutableList<Card>,
    var drawStack : MutableList<Card>

){
}