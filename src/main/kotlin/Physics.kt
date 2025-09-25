
/**
 * Represents a point in the arena.
 * The origin (0,0) is the top-left corner.
 * @property x the horizontal coordinate (in pixels)
 * @property y the vertical coordinate (in pixels)
 */
data class Point(val x: Int, val y: Int)

/**
 * Represents a direction.
 * @property dRow the vertical displacement
 * @property dCol the horizontal displacement
 */
enum class Direction(val dRow: Int, val dCol: Int) {
    LEFT(0,-1), RIGHT(0,+1), UP(-1,0), DOWN(+1,0)
}

/**
 * Represents a cell in the arena grid.
 * @property row the vertical coordinate (in cells)
 * @property col the horizontal coordinate (in cells)
 */
data class Cell(val row: Int, val col: Int)


/**
 * Converts a cell to a point.
 * The point is the top-left corner of the cell.
 * @receiver the cell to convert.
 * @return the point corresponding to the cell.
 */
fun Cell.toPoint() = Point(col * CELL_WIDTH, row * CELL_HEIGHT)

/**
 * Converts a point to a cell.
 * Any point inside a cell is converted to that cell.
 * @receiver the point to convert.
 * @return the cell corresponding to the point.
 */
fun Point.toCell() = Cell(y / CELL_HEIGHT, x / CELL_WIDTH)


/**
 * Adds a Speed vector to a Point.
 * As an operator function, it can be used: pos+speed
 * @receiver the current point.
 * @param speed the speed vector to add.
 * @return the new point after adding the speed vector.
 */
operator fun Point.plus(speed: Speed) = Point(x + speed.dX, y + speed.dY)

/**
 * Adds a direction to a cell.
 * As an operator function, it can be used: cell+dir
 * @receiver the current cell.
 * @param dir the direction to add.
 * @return the new cell after adding the direction.
 */
operator fun Cell.plus(dir: Direction) = Cell(row + dir.dRow, col + dir.dCol)

/**
 * Verify if the position is synchronized with one cell.
 * The speed components are zeroed if the position is synchronized with one cell.
 * @param pos the position to verify.
 * @receiver the current speed.
 * @return the new speed with zero components if the position is synchronized with one cell.
 */
fun Speed.stopIfInCell(pos: Point,game: Game): Speed {

    val newDx = if (pos.x % CELL_WIDTH == 0 && !game.man.isJumping) 0 else dX
    val newDy = if (pos.y % CELL_HEIGHT == 0 && !game.man.isJumping) 0 else dY + if (game.man.isJumping) JUMPACCELERATIONSPEED else 0

   return if(newDy>-JUMPSPEED){
        this
    }
    else{
        if ((newDx!=dX || newDy!=dY)) Speed(newDx, newDy)else this

    }
}

/**
 * Verify if the position is synchronized with one cell.
 * The speed components are ze
 *
 * @receiver the current speed.
 * @return the new speed with zero components if the position is synchronized with one cell.
 */
fun Speed.changeSpeed(speed: Speed, game: Game): Man {
    val newDx = speed.dX
    val newDy = speed.dY
    return  Man(game.man.pos,game.man.faced,Speed(newDx, newDy),game.man.isJumping,game.man.isClimbing)
}



/**
 * Returns true if the speed vector is zero.
 */
fun Speed.isZero() = dX == 0 && dY == 0