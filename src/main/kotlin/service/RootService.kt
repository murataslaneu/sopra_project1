package service

import entity.*
import gui.Refreshables
import javax.security.auth.Refreshable

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
    private var currentGame : DiveGame? = null

    /**
     * Fügt den angegebenen [newRefreshable] zu allen
     * mit diesem Stammdienst verbundenen Diensten hinzu.
     */
    fun addRefreshable(newRefreshable: Refreshables) {
        gameService.addRefreshable(newRefreshable)          // Check !
        playerActionService.addRefreshable(newRefreshable)  // Check !
    }
    /**
     * Führt eine angegebene Methode auf allen [Refreshable]-Objekten aus,
     * die mit diesem [RootService] verbunden sind.
     *
     * Diese Methode wird verwendet, um eine bestimmte Funktionalität
     * oder Aktualisierung auf alle "Refreshable" Instanzen anzuwenden,
     * die im Kontext der Dienste des Spiels vorhanden sind.
     *
     * @param method auf jedem "Refreshable" Objekt angewendet werden soll.
     *
     * @throws IllegalStateException wenn method sein Befehl nicht erfüllt hat // ?
     */
    fun onAllRefreshables(method : Refreshable.() -> Unit){}
}