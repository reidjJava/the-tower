package me.reidj.tower.util

import me.func.protocol.ui.dialog.*

/**
 * @project tower
 * @author Рейдж
 */

object DialogUtil {

    val tournamentDialog = Dialog(
        Entrypoint(
            "tournamentPageOne",
            "Турнир",
            Screen("тут букафы какие то").buttons(
                Button("Играть").actions(Action(Actions.COMMAND).command("/tournament"), Action(Actions.CLOSE)),
                Button("Что это такое?").actions(Action.command("/tournamentInfo")),
                Button("Закрыть").actions(Action(Actions.CLOSE))
            )
        ),
        Entrypoint(
            "tournamentPageTwo",
            "Турнир",
            Screen("тут инфа про турнир").buttons(
                Button("Понятно").actions(Action(Actions.CLOSE)),
                Button("Назад").actions(Action.command("/tournamentDialog"))
            )
        )
    )

    val guideDialog = Dialog(
        Entrypoint(
            "guidePageOne",
            "Обучение",
            Screen("бла бла").buttons(
                Button("ага да").actions(Action(Actions.COMMAND).command("/tournament"), Action(Actions.CLOSE)),
                Button("Уйти").actions(Action(Actions.CLOSE))
            )
        ),
        Entrypoint(
            "guidePageTwo",
            "Обучение",
            Screen("тут инфа про турнир").buttons(
                Button("Понятно").actions(Action(Actions.CLOSE)),
                Button("Назад").actions(Action.command("/tournamentDialog"))
            )
        )
    )
}