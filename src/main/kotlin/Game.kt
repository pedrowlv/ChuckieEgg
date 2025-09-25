
/**
 * Represents the game action.
 */
enum class Action { WALK_LEFT, WALK_RIGHT, UP_STAIRS, DOWN_STAIRS, JUMP }
enum class State { PLAYING, WINNER, TIMEOUT }

/**
 * Represents all game information.
 * @property man information about man
 * @property floor positions of floor cells
 * @property stairs positions of stairs cells
 * @property eggs positions of the egg cells
 * @property food positions of the food cells
 * @property mobs information about mobs
 * @property time time remaining
 * @property score current score
 * @property state state of the game
 */
data class Game(
    val man: Man,
    val floor: List<Cell>,
    val stairs: List<Cell>,
    val eggs: List<Cell>,
    val food: List<Cell>,
    val mobs: MOB,
    val time: Int,
    val score: Int,
    val state: State
)


/**
 * Loads a game from a file.
 * @param fileName the name of the file with the game information.
 * @return the game loaded.
 */
fun loadGame(fileName: String) :Game {
    val cells: List<CellContent> = loadLevel(fileName)
    return Game(
        man = createMan( cells.first { it.type==CellType.MAN }.cell ),
        floor = cells.ofType(CellType.FLOOR),
        stairs = cells.ofType(CellType.STAIR),
        eggs = cells.ofType(CellType.EGG),
        food = cells.ofType(CellType.FOOD),
        mobs= createMobs(cells.first { it.type==CellType.MOBS}.cell),
        score = 0,
        time = 2666,
        state = State.PLAYING
    )
}


/**
 * Performs an action to the game.
 * If the action is null, returns current game.
 * @param action the action to perform.
 * @receiver the current game.
 * @return the game after the action performed.
 */
fun Game.doAction(action: Action?): Game {
    if (action==null) return this
    val newMan = when(action) {
        Action.WALK_LEFT -> man.moveIn(Direction.LEFT, this)
        Action.WALK_RIGHT -> man.moveIn(Direction.RIGHT,this)
        Action.UP_STAIRS -> man.climbIn(Direction.UP, this)
        Action.DOWN_STAIRS -> man.climbIn(Direction.DOWN, this)
        Action.JUMP -> man.jump(this)
        else -> man
    }
    return if (newMan!=man) copy(man = newMan) else this
}



/**
 * Responsible for the first sixth of the movement when the character is going sideways
 * @receiver direction
 * @receiver current game
 * @receiver current position
 * @return Boolean according to the logic value of the condition
 */
fun Man.moveIn(dir: Direction,game: Game): Man {

    if(game.checkState()== State.TIMEOUT || game.checkState()== State.WINNER || this.isJumping)  return this

    else if (this.speed.isZero()) {
        val newMan = game.man.speed.changeSpeed(Speed(if (dir == Direction.RIGHT) MOVE_SPEED else -MOVE_SPEED, 0), game)

        val newX = pos.x + newMan.speed.dX

        val newPosition = Point(newX, pos.y)

        return if(checkIfCanWalk(dir, newPosition, game)) {
            Man(newPosition, dir, newMan.speed, isJumping, false)
        }
        else {
            this
        }
    }
    else {
        return this
    }
}

/**
 * Changes the parameters of the data class "Man" according to various limits established
 * @receiver direction
 * @receiver current game
 * @return new characteristics of the character(Man)
 */
fun Man.climbIn(dir: Direction,game: Game): Man {

    if(game.checkState()== State.TIMEOUT || game.checkState()== State.WINNER) return this
    else if (this.speed.isZero()){

        if (dir == Direction.UP){
            if (!checkIfCanClimbUP(game)) return this
        }
        else{
            if (!checkIfCanClimbDown(game)) return this
        }

        val newMan = game.man.speed.changeSpeed(Speed(0, if (dir == Direction.UP) -CLIMBING_SPEED else CLIMBING_SPEED), game)

        val newY = pos.y + newMan.speed.dY

        val newPosition = Point(pos.x, newY)

        return Man(newPosition, dir, newMan.speed, isJumping, true)

    }
    else{
        return this
    }
}

/**
 * Changes the parameters of the data class "Man" according to various limits established
 * @receiver current game
 * @return new characteristics of the character(Man)
 */
fun Man.jump(game: Game): Man {

    if(game.checkState()== State.TIMEOUT || game.checkState()== State.WINNER) return this

    else if(this.speed.isZero() && !this.isClimbing && !this.isJumping){

        val newPoint = (game.man.pos.toCell() + if (game.man.faced == Direction.LEFT) Direction.LEFT else Direction.RIGHT).toPoint()

        val newSpeed =
            when {
                !checkArenaLimits(newPoint) -> Speed(0, JUMPSPEED)
                else -> Speed(if (game.man.faced == Direction.RIGHT) MOVE_SPEED else -MOVE_SPEED, JUMPSPEED)
            }

        val newX = pos.x + newSpeed.dX

        val newY = pos.y + newSpeed.dY

        val newPosition = Point(newX, newY)

        return Man(newPosition, faced, newSpeed, true, false)
    }
    else{
        return this
    }
}

/**
 * Verifies if the character is illegible to walk sideways
 * @receiver direction
 * @receiver current game
 * @receiver current position
 * @return Boolean according to the logic value of the condition
 */
fun Man.checkIfCanWalk(dir: Direction, newPosition: Point,game: Game): Boolean {
    return (checkArenaLimits(newPosition)) && checkWalkBarrier(dir,game)
}

/**
 * Verifies if the character is within the limits of the arena
 * @receiver position of character
 * @return Boolean according to the logic value of the condition
 */
fun checkArenaLimits(point: Point): Boolean{
    return point.x in (0..(GRID_WIDTH*CELL_WIDTH-CELL_WIDTH))
}

fun checkWalkBarrier(dir: Direction,game:Game): Boolean
{
    val cell = game.man.pos.toCell()
    return cell + dir !in game.floor
}



/**
 * Verifies if the cell above is a stair and if so, is illegible to climb up
 * @receiver current game
 * @return Boolean according to the logic value of the condition
 */
fun checkIfCanClimbUP(game:Game):Boolean{
    val newY = game.man.pos.y - CELL_HEIGHT/8
    val cell = Point(game.man.pos.x,newY).toCell()
    return cell + Direction.UP in game.stairs
}

/**
 * Verifies if the cell below is a stair and if so, is illegible to climb down
 * @receiver current game
 * @return Boolean according to the logic value of the condition
 */
fun checkIfCanClimbDown(game:Game):Boolean{
    val cell = game.man.pos.toCell()
    return cell + Direction.DOWN in game.stairs
}


/**
 * Computes the next game state.
 * If the man is stopped, returns current game.
 * @receiver the current game.
 * @return the game after the next frame.
 */
fun Game.stepFrame(): Game{

    if(checkState()== State.TIMEOUT || checkState()== State.WINNER){
        return Game(man,floor,stairs,eggs,food,mobs,time,score, checkState())
    }
    else{

        val newSpeed = man.speed.stopIfInCell(man.pos,this)

        val man = man.moveInStepFrame(newSpeed,this)

        val cell = man.pos.toCell()

        return Game(
            man, floor, stairs,
            eggs - cell,
            food - cell,
            mobs,
            if (time - 1 >= 0) time - 1 else time,
            when {
                eggs.contains(cell) -> score + 100
                food.contains(cell) -> score + 50
                else -> score
            },
            checkState()
        )
        // -cell is used to remove the food as the character walks into them
    }
}


/**
 * Verifies if the character is illegible to rebound
 * @receiver direction
 * @receiver current game
 * @receiver current position
 * @return Boolean according to the logic value of the condition
 */
fun checkWalkBack(dir: Direction,game:Game,position: Point): Boolean
{
    return when (dir) {
        Direction.RIGHT -> Point(position.x,position.y+31).toCell() in game.floor

        Direction.LEFT -> Point(position.x,position.y+31).toCell() in game.floor

        else -> false
    }
}

/**
 * Moves the man from frame time to frame time
 * @receiver Speed
 * @receiver current game
 * @return new characteristics of data class "Man"
 */
fun Man.moveInStepFrame(speed: Speed,game: Game): Man {

    val canFall = checkCanFall(game.man.pos,game)

    if (game.man.isJumping){ // jump

        val newPoint = Point(game.man.pos.x + speed.dX, game.man.pos.y + speed.dY)

        val newSpeed=
            when {
                (!checkArenaLimits(newPoint) ) -> Speed(0, speed.dY) // limits of x
                (checkWalkBack(game.man.faced,game,newPoint)) -> Speed(-speed.dX,speed.dY) //looks for a change in its direction
                else -> speed
            }

        val newX = pos.x + newSpeed.dX
        val newY = pos.y + newSpeed.dY

        var newPosition = Point(newX,newY)

        val inFlor = Point(newPosition.x,(newPosition.y +(CELL_HEIGHT-1))).toCell() in game.floor

        newPosition = if(inFlor) newPosition.toCell().toPoint() else newPosition

        val cell = newPosition.toCell()

        val inJump = cell + Direction.DOWN !in game.floor

        return Man(
            newPosition,
            faced,
            newSpeed,
            inJump,
            isClimbing)

    }
    else if(speed.dX!=0) // walk
    {
        val newX = pos.x + speed.dX

        val newPosition = Point(newX,pos.y)

        return  Man(newPosition, faced, speed, isJumping, isClimbing)
    }
    else if(speed.dY!=0 && game.man.isClimbing) // climb
    {
        val newY = pos.y + speed.dY

        val newPosition = Point(pos.x,newY)

        return  Man(newPosition, faced, speed, isJumping, isClimbing)
    }
    else //fall
    {
        return if (canFall) {

            val NewMan = game.man.speed.changeSpeed(Speed(0, CLIMBING_SPEED), game)

            val newY = pos.y + NewMan.speed.dY

            val newPosition = Point(pos.x, newY)

            Man(newPosition, faced, NewMan.speed, isJumping, isClimbing)
        } else {//stopped
            Man(pos, faced, Speed(0, 0), isJumping, isClimbing)
        }
    }
}

/**
 * Verifies if the character is able to enter the state of free-falling
 * @return Boolean according to the logic value of the condition
 */
fun checkCanFall(point:Point,game:Game): Boolean
{
    val cell = point.toCell()
    return cell + Direction.DOWN !in game.floor+game.stairs
}


//Checks the state of the game
fun Game.checkState(): State =
    when {
        time == 0 -> State.TIMEOUT
        eggs.isEmpty() -> State.WINNER
        else -> State.PLAYING
    }


