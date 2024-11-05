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
        onAllRefreshables { refreshAfterGameStart() }
        startTurn()
    }

    /**
     * [startTurn] Startet den Zug des aktuellen Spielers und spielt die Karte
     * aus der Hand.
     */
    fun startTurn() {
        val game = rootService.currentGame
        checkNotNull(game)
        onAllRefreshables {refreshAfterGameStart() }
    }

    /**
     * [endTurn] Beendet den Zug des aktuellen Spielers.
     */
    fun endTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        //Tauschen Players
        val currentPlayer = game.currentPlayer
        if(game.playerList[0] == currentPlayer){
            game.playerList[1]
        }

        //Wenn runde für aktuelle Spieler endet, müssen die Karten verdeckt sein
        game.currentPlayer.hand.forEach { cards ->
            cards?.isHidden = true
        }

        onAllRefreshables { refreshAfterTurnEnds() }
        startTurn()
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

    companion object fun isGameEnded(): Boolean {
        val game = rootService.currentGame
        checkNotNull(game)

        //Wenn es keine Karten mehr in der Mitte gibt und
        // Spieler haben keine Karten, endet das Spiel
        val drawStackEmpty = game.drawStack.empty() //Can be isEmpty()
        val bothNoDrawn = !game.playerList[0].hand.isEmpty() &&
                !game.playerList[1].hand.isEmpty()

        onAllRefreshables { refreshAfterGameEnds() }
        return drawStackEmpty && bothNoDrawn
    }
}