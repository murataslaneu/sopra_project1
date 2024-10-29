package gui

import tools.aqua.bgw.core.BoardGameApplication
/**
 * [SopraApplication]
 */
class SopraApplication : BoardGameApplication("SoPra Game") {

   private val helloScene = HelloScene()

   init {
        this.showGameScene(helloScene)
    }

}

