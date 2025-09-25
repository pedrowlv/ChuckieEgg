import pt.isel.canvas.*

// Dimensions of the sprites in the images files
// Floor, Egg, Food and Stair are 1x1 ; Man is 1x2 ; Hen is 1x3 or 2x2
const val SPRITE_WIDTH = 24  // [pixels in image file]
const val SPRITE_HEIGHT = 16 // [pixels in image file]

// Dimensions of the Arena grid
const val GRID_WIDTH = 20
const val GRID_HEIGHT = 24

// Dimensions of each cell of the Arena grid
const val VIEW_FACTOR = 2 // each cell is VIEW_FACTOR x sprite
const val CELL_WIDTH = VIEW_FACTOR * SPRITE_WIDTH   // [pixels]
const val CELL_HEIGHT = VIEW_FACTOR * SPRITE_HEIGHT  // [pixels]

//Dimensions of the game result
const val SCORE_WIDHT = CELL_WIDTH*4
const val SCORE_HEIGHT = CELL_HEIGHT*12

/**
 * Creates a canvas with the dimensions of the arena.
 */
fun createCanvas() = Canvas(GRID_WIDTH * CELL_WIDTH, GRID_HEIGHT * CELL_HEIGHT, BLACK)



/**
 * Draw all the elements of the game.
 */
fun Canvas.drawGame(game: Game) {
    erase()
    //drawGridLines()
    game.floor.forEach { drawSprite(it.toPoint(), Sprite(0,0)) }
    game.stairs.forEach { drawSprite(it.toPoint(), Sprite(0,1)) }
    game.eggs.forEach { drawSprite(it.toPoint(), Sprite(1,1)) }
    game.food.forEach { drawSprite(it.toPoint(), Sprite(1,0)) }
    drawMan(game.man)
    drawMobs(game.mobs)
    drawText(CELL_WIDTH, CELL_HEIGHT, "Score: ${game.score}", YELLOW, 40)
    drawText(CELL_WIDTH*14, CELL_HEIGHT, "Time: ${game.time}", YELLOW, 40)
    if(game.time == 0) drawText(SCORE_WIDHT, SCORE_HEIGHT, "YOU LOSE", RED, 120)
    if(game.eggs.isEmpty())drawText(SCORE_WIDHT, SCORE_HEIGHT, "YOU WIN", GREEN, 120)
}
/*
Draws the mobs
 */
fun Canvas.drawMobs(m: MOB){
    val sprite = Sprite(0, 5, 3)
    drawSprite(m.pos,sprite)
}

/**
 * Draw horizontal and vertical lines of the grid in arena.
 */
/*fun Canvas.drawGridLines() {
    (0 ..< width step CELL_WIDTH).forEach { x -> drawLine(x, 0, x, height, WHITE, 1) }
    (0 ..< height step CELL_HEIGHT).forEach { y -> drawLine(0, y, width, y, WHITE, 1) }
}
Not used anymore but was provided at the start of the project
 */


/**
 * Draws the man in canvas according to the direction he is facing and the changes the image used according to the current frame
 */
fun Canvas.drawMan(m: Man) {

    val leftSprite = listOf(
        Sprite (2, 3, 2),
        Sprite(2, 2, 2),
        Sprite(2, 4, 2))


    val rightSprite = listOf(
        Sprite (0, 3, 2),
        Sprite(0, 2, 2),
        Sprite(0, 4, 2))


    val verticalSprite = listOf(
        Sprite (4, 0, 2),
        Sprite(4, 1, 2),
        Sprite(4, 2, 2),
        Sprite(4, 3, 2),
        Sprite(4, 4, 2))



    val sprite : Sprite
    sprite = if(m.speed.dX != 0 && !m.isJumping) {


        when (m.faced) {
            Direction.LEFT ->
                when (m.pos.x % CELL_WIDTH / m.speed.dX) {
                    0 -> leftSprite[0]
                    -1 -> leftSprite[1]
                    -2 -> leftSprite[0]
                    -3 -> leftSprite[2]
                    -4 -> leftSprite[0]
                    -5 -> leftSprite[1]
                    else -> leftSprite[0]
                }

            Direction.RIGHT ->
                when (m.pos.x % CELL_WIDTH / m.speed.dX) {
                    0 -> rightSprite[0]
                    1 -> rightSprite[2]
                    2 -> rightSprite[0]
                    3 -> rightSprite[1]
                    4 -> rightSprite[0]
                    5 -> rightSprite[2]
                    else -> rightSprite[0]
                }
            else ->rightSprite[0] //condition used to complement all the other cases
        }
    }
    else if(m.speed.dY != 0 && m.isClimbing){
        when(m.faced) {
            Direction.UP, Direction.DOWN ->

                when(m.pos.y % CELL_HEIGHT / m.speed.dY){
                    -3 -> verticalSprite[1]
                    -2 -> verticalSprite[2]
                    -1 -> verticalSprite[1]
                    0 -> verticalSprite[0]
                    1 -> verticalSprite[3]
                    2 -> verticalSprite[4]
                    3 -> verticalSprite[3]

                    else -> verticalSprite[0]
                }
            else -> verticalSprite[0] //condition used to complement all the extra cases
        }


    }
    else{
        when(m.faced) {
            Direction.LEFT -> Sprite(2,3,2)
            Direction.RIGHT -> Sprite(0,3,2)
            Direction.UP, Direction.DOWN -> Sprite(4,0,2)
        }
    }

    drawSprite(m.pos, sprite)
}



/**
 * Draw a sprite in a position of the canvas.
 * @param pos the position in the canvas (top-left of base cell).
 * @param spriteRow the row of the sprite in the image.
 * @param spriteCol the column of the sprite in the image.
 * @param spriteHeight the height of the sprite in the image.
 * @param spriteWidth the width of the sprite in the image.
 */
fun Canvas.drawSprite(pos: Point, s: Sprite) {
    val x = s.col * SPRITE_WIDTH + s.col + 1  // in pixels
    val y = s.row * SPRITE_HEIGHT + s.row + s.height
    val h = s.height * SPRITE_HEIGHT
    val w = s.width * SPRITE_WIDTH
    drawImage(
        fileName = "chuckieEgg|$x,$y,$w,$h",
        xLeft = pos.x,
        yTop = pos.y - (s.height-1) * CELL_HEIGHT,
        width = CELL_WIDTH * s.width,
        height = CELL_HEIGHT * s.height
    )
}

/**
 * Represents a sprite in the image.
 * Example: Sprite(2,3,2,1) is the man facing left.
 * @property row the row of the sprite in the image. (in sprites)
 * @property col the column of the sprite in the image. (in sprites)
 * @property height the height of the sprite in the image. (in sprites)
 * @property width the width of the sprite in the image. (in sprites)
 */
data class Sprite(val row: Int, val col: Int, val height: Int = 1, val width: Int = 1)
