
data class MOB(val pos: Point,
               val faced: Direction,
               val speed: Speed,
)// data class used for the mobs

fun createMobs(cell: Cell) = MOB(
    pos = cell.toPoint(),
    faced= Direction.LEFT,
    speed=Speed(0,0)
)//creates the mobs