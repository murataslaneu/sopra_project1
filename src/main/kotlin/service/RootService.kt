package service

import entity.*
import gui.Refreshables

class RootService {
    /**
     *
     * Hauptklasse der Dienstebene für das DiveGame. Ermöglicht den Zugriff
     * auf alle anderen Dienstklassen und enthält den [currentGame] Status,
     * auf den diese Dienste zugreifen können.
     *
     * [gameService] gameService
     * [playerActionService] Player action service
     * [currentGame] Aktuelles Spiel kann Null sein, weil nicht gestartet ist.
     */
    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)
    var currentGame : DiveGame? = null //Private??

    /**
     * Fügt den angegebenen [newRefreshable] zu allen
     * mit diesem Stammdienst verbundenen Diensten hinzu.
     */
    fun addRefreshable(newRefreshable: Refreshables) {
        gameService.addRefreshable(newRefreshable)          // Check !
        playerActionService.addRefreshable(newRefreshable)  // Check !
    }
    /**
     * Führt eine angegebene Methode auf allen [Refreshables]-Objekten aus,
     * die mit diesem [RootService] verbunden sind.
     *
     * Diese Methode wird verwendet, um eine bestimmte Funktionalität
     * oder Aktualisierung auf alle "Refreshable" Instanzen anzuwenden,
     * die im Kontext der Dienste des Spiels vorhanden sind.
     *
     * @param newRefreshables, die [Refreshables], die hinzugefügt werden sollen
     */
    fun addRefreshables(vararg newRefreshables: Refreshables) {
        newRefreshables.forEach { addRefreshable(it) }
    }
}