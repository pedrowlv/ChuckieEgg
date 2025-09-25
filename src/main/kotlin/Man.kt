// Speed of man in pixels per frame, in horizontal and vertical directions
const val MOVE_SPEED = CELL_WIDTH / 6
const val CLIMBING_SPEED = CELL_HEIGHT / 4
const val JUMPSPEED = -CELL_HEIGHT/2
const val JUMPACCELERATIONSPEED = CELL_HEIGHT/SPRITE_HEIGHT


data class Speed(val dX:Int, val dY:Int)

/**
 * Represents the Man in the game.
 * @property pos is the position in the board.
 * @property faced the direction the man is facing
 */
data class Man(
    val pos: Point, //x e y das grids
    val faced: Direction,
    val speed: Speed,
    val isJumping: Boolean,
    val isClimbing: Boolean
)

/**
 * Creates the Man in the cell
 */
fun createMan(cell: Cell) = Man(
    pos = cell.toPoint(),
    faced = Direction.LEFT,
    speed = Speed(0,0),
    isJumping = false,
    isClimbing=false
)