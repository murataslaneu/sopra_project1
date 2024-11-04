package service

import entity.Player
import entity.Card
import entity.DiveGame
import java.util.*


/**
 * Der [GameService] verwaltet das Spiel und die Spielabläufe im DiveGame.
 * Diese Klasse ist für die Verwaltung der Spieler, der Spielzustände und
 * der Aktionen während des Spiels zuständig.
 *
 * @param service, Haptdienst [RootService], der Zugriff auf andere
 * Dienstklassen und den aktuellen Spielstatus bietet.
 *
 * @property currentPlayer, aktueller Spieler, der am Zug ist.
 * @property drawStack, Nachzugstapel, von dem Spieler karten ziehen.
 * @property currentGame, aktuelle Spiel, das gespielt wird.
 * @property discardStack, Ablagestapel, auf den Karten abgelegt werden.
 * @property cardToDiscard, die letzte Karte(die gezogen ist) in der Hand des aktuellen Spielers,
 * die abgeworfen werden soll (weil 8+ Karten nicht erlaubt)
 */
class GameService(private val service: RootService) : AbstractRefreshingService() {
    lateinit var currentPlayer: Player
    val drawStack = createDrawStack()
    lateinit var currentGame: DiveGame
    private val discardStack = Stack<Card>()
    private val cardToDiscard = currentPlayer.hand.last() //Letzte addierte Karte 8 + 1
    //val playerCards = mutableListOf<Card>()
    //val playerCardsIsHidden =

    /**
     * [startNewGame] startet ein neues Spiel mit den angegebenen Spielernamen.
     *
     * @param playerNames, die Namen der Spieler, die am Spiel sind.
     * @throws IllegalArgumentException, wenn die Spielernamen gleich sind, enthält Lücken oder leer sind.
     */
    fun startNewGame(playerNames: List<String>) {
        val player1 = playerNames[0]
        val player2 = playerNames[1]

        if(player1 == player2 || listOf(playerNames).isEmpty() ){
            throw IllegalArgumentException("playerNames are same or empty(or have blanks)")
        }
        if(playerNames.size != 2){
            throw IllegalArgumentException("There are more or less than two players")
        }

        val players = playerNames.map { Player(it, 0, false,
            mutableListOf(), mutableListOf()) }

        currentPlayer = players.shuffled().first()
        drawStack.shuffle()

        players.forEach { player -> repeat(5) {
                player.hand.add(drawStack.pop())
            }
        }

        //das aktuelle Spiel des Stammdienstes auf das erstellte Spiel setzen
        service.currentGame = currentGame

        //trigger refresh für GUI damit neu Spiel startet
        onAllRefreshables { refreshAfterGameStart() }

        //Die erste Runde des Spiels wird automatisch gestartet.
        startTurn()
    }
    /**
     * [startTurn] Startet den Zug des aktuellen Spielers und spielt die Karte
     * aus der Hand. Wenn der Spieler mehr als 8 Karten hat, wird eine Karte
     * von der Hand des Spielers abgeworfen
     *
     *      ( BELIEBIGE KARTE WERFEN ODER SPIELEN ADDIEREN ! )
     */
    fun startTurn(){
        val cardToPlay = currentPlayer.hand.first()
        checkNotNull(currentGame)
        onAllRefreshables { refreshAfterTurnStart() }
        /*if(currentPlayer.hand.size <= 8 && !service.playerActionService.swapped ){
            service.playerActionService.swapCard()
        } else {
            if(currentPlayer.hand.isNotEmpty()){
                service.playerActionService.playCard(cardToPlay!!)
            }
        }*/

        if(currentPlayer.hand.isNotEmpty()){
            service.playerActionService.playCard(cardToPlay!!)
            onAllRefreshables { refreshAfterCardPlayed(cardToPlay) }
        }

        if(currentPlayer.hand.size > 8){
            discardStack.push(cardToDiscard)
            currentPlayer.hand.remove(cardToDiscard)
            onAllRefreshables { refreshAfterCardDiscarded(cardToDiscard!!) }
        }
    }
    /**
     * [endTurn] Beendet den Zug des aktuellen Spielers und überprüft, ob
     * Karten abgeworfen werden müssen, falls der Spieler 8+ Karten hat.
     */
    fun endTurn(){
        checkNotNull(currentGame)

        if(currentPlayer.hand.size > 8){
            discardStack.push(cardToDiscard)
            currentPlayer.hand.remove(cardToDiscard)
            onAllRefreshables { refreshAfterTurnEnds() }
        }
        startTurn()
    }

    /**
     * Erstellt und gibt einen neuen Nachziehstapel zurück.
     *
     * @return ein neuer [Stack] von [Card], der als Nachziehstapel dient.
     */
    private fun createDrawStack(): Stack<Card>{
            return Stack()
        }
    /**
     * vlt addieren @override functions ?!
     */
    }

