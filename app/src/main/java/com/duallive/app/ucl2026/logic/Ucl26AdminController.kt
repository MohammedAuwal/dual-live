package com.duallive.app.ucl2026.logic

enum class UclPhase {
    LEAGUE, PLAYOFFS, ROUND_OF_16, QUARTER_FINALS, SEMI_FINALS, FINAL, COMPLETED
}

class Ucl26AdminController {
    var currentPhase = UclPhase.LEAGUE
        private set

    fun proceedToNextPhase() {
        currentPhase = when (currentPhase) {
            UclPhase.LEAGUE -> UclPhase.PLAYOFFS
            UclPhase.PLAYOFFS -> UclPhase.ROUND_OF_16
            UclPhase.ROUND_OF_16 -> UclPhase.QUARTER_FINALS
            UclPhase.QUARTER_FINALS -> UclPhase.SEMI_FINALS
            UclPhase.SEMI_FINALS -> UclPhase.FINAL
            UclPhase.FINAL -> UclPhase.COMPLETED
            else -> currentPhase
        }
    }
}
