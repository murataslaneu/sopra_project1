package service

import entity.*
import java.util.Stack

/**
 * Der [GameService] verwaltet das Spiel und die Spielabläufe im DiveGame.
 * Diese Klasse ist für die Verwaltung der Spieler, der Spielzustände und
 * der Aktionen während des Spiels zuständig.
 *
 * @param rootService, Hauptdienst [RootService], der Zugriff auf andere
 * Dienstklassen und den aktuellen Spielstatus bietet.
 */
class GameService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * [startNewGame] startet ein neues Spiel mit den angegebenen Spielernamen.
     *
     * @param playerNames, die Namen der Spieler, die am Spiel sind.
     */
    fun startNewGame(playerNames: List<String>) {
        //Erstellen players
        val playerList = playerNames.map {
            Player(
                it, 0, false, mutableListOf(), mutableListOf())
        }.toMutableList()
        require(playerList.isNotEmpty()) // Überprüfen, ob Spieler in Spiel sind
        check(playerNames[0] != playerNames[1])

        //Erstellen game
        val game = DiveGame(playerList.random(), playerList, mutableListOf(), mutableListOf(), Stack())

        //Erstellen Spiel Stack
        val drawStack = createDrawStack()
        game.drawStack = drawStack

        //Für jede Spieler 5 Karten geben
        playerList.forEach { player ->
            repeat(5) {
                player.hand.add(drawStack.pop())
            }
        }
        rootService.currentGame = game

        if(game.playerList[0] == game.currentPlayer){
            game.currentPlayer = game.playerList[0]
        } else {
            game.currentPlayer = game.playerList[1]
        }

        onAllRefreshables { refreshAfterGameStart() }
    }

    /**
     * [startTurn] Startet den Zug des aktuellen Spielers und spielt die Karte
     * aus der Hand.
     */
    fun startTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        onAllRefreshables { refreshAfterTurnStart() }
    }

    /**
     * [endTurn] Beendet den Zug des aktuellen Spielers.
     */
    fun endTurn() {
        val game = rootService.currentGame
        checkNotNull(game)
        checkNotNull(game.currentPlayer)

        //Tauschen Players
        game.currentPlayer = if (game.currentPlayer == game.playerList[0]) game.playerList[1] else game.playerList[0]

        onAllRefreshables { refreshAfterTurnEnds() }
    }

    /**
     * Erstellt und gibt einen neuen Nachziehstapel zurück (shuffled!).
     *
     * @return ein neuer [Stack] von [Card], der als Nachziehstapel dient.
     */
    private fun createDrawStack(): Stack<Card> {
        val drawStack = Stack<Card>()

        for(value in CardValue.fullDeck()) {
            for(suit in CardSuit.values()) {
                drawStack.push(Card(false,suit,value))
            }
        }
        drawStack.shuffle()
        return drawStack
    }

    /**
     * [isGameEnded] überprüft, ob game beendet ist.
     *
     * Diese Funktion prüft, ob das aktuelle Spiel beendet ist, und gibt einen entsprechenden
     * booleschen Wert zurück.
     */
    companion object fun isGameEnded(): Boolean {
        val game = rootService.currentGame
        checkNotNull(game)

        //Wenn es keine Karten mehr in der Mitte gibt und
        val drawStackEmpty = game.drawStack.empty()

        onAllRefreshables { refreshAfterGameEnds() }
        return drawStackEmpty

    }

    /**
     * [gameEndScore] Berechnet und gibt die Endstände des Spiels aus.
     * Wie zum beispiel, winner oder looser, oder namen mit Punkten.
     */
    fun gameEndScore() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player1 = game.playerList[0]
        val player2 = game.playerList[1]
        lateinit var winner: Player
        lateinit var looser: Player
        if(isGameEnded()) {
            if(player1.score > player2.score) {
                winner = player1
                looser = player2
            } else {
                winner = player2
                looser = player1
            }
        } else {
            println("The game has not endet yet.")
        }
        println("Winner is: " + winner.name + " with Score: " + winner.score)
        println("Looser is: " + looser.name + " with Score: " + looser.score)
    }
}