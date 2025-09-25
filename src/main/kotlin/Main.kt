import pt.isel.canvas.*

private const val FRAME_TIME = 30 // in milliseconds
private const val SPACE_CODE = 32 // Val for jump key

/**
 * The main function of this game
 * Shows a window representing the arena where the game occurs
 * Cursor keys and backspace move the ball in each correspondent direction
 * The 'Escape' key closes the window and the game altogether
 */
fun main() {
    onStart {

        val arena = createCanvas()
        var game = loadGame("level1.txt")
        arena.drawGame(game)

        arena.onKeyPressed { key ->
            if (key.code == ESCAPE_CODE) arena.close()
            game = game.doAction(key.code.toActionOrNull())
            arena.drawGame(game)
        }

        arena.onTimeProgress(FRAME_TIME) {
            game = game.stepFrame()
            arena.drawGame(game)
        }
    }
    onFinish { }
}

fun Int.toActionOrNull(): Action? =
    when(this) {
        LEFT_CODE ->    Action.WALK_LEFT
        RIGHT_CODE ->   Action.WALK_RIGHT
        UP_CODE ->      Action.UP_STAIRS
        DOWN_CODE ->    Action.DOWN_STAIRS
        SPACE_CODE ->   Action.JUMP
        else -> null
    }