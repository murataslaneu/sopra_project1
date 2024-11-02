package service

import gui.Refreshables
import javax.security.auth.Refreshable

/**
 * Die abstrakte Klasse [AbstractRefreshingService] stellt einen Dienst bereit,
 * der die Aktualisierung einer Liste von [Refreshables] verwaltet.
 *
 * Sie ermöglicht das Hinzufügen von "Refreshables" zur internen Liste und
 * bietet eine Methode, um eine gegebene Funktion auf alle hinzufügten
 * "Refreshables" anzuwenden.
 */
abstract class AbstractRefreshingService {
    private val refreshables = mutableListOf<Refreshables?>()

    /**
     * fügt ein [Refreshable] zu der Liste hinzu,
     * die immer dann aufgerufen wird, wenn [onAllRefreshables] verwendet wird.
     */
    fun addRefreshable(newRefreshable: Refreshables) {
        refreshables.add(newRefreshable)
    }
    /**
     * Führt die übergebene Methode (normalerweise ein Lambda) auf allen
     * [Refreshables] aus, die bei der Dienstklasse registriert sind,
     * die diesen [AbstractRefreshingService] erweitert.
     */
    fun onAllRefreshables(method: Refreshables.() -> Unit) {
        refreshables.forEach() {it!!.method()} // Check for "!!"
    }
}