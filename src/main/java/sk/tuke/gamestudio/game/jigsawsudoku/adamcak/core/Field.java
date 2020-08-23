package sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core;

import java.util.Random;

public class Field {
    private final int BOARD_WIDTH = 9;
    private final int BOARD_HEIGHT = 9;
    private Tile[][] board;
    private Tile[][] completedBoard;
    private Quadrants quadrants;

    public Field(Quadrants quadrants) {
        this.quadrants = quadrants;
    }

    private void nullTheField(Tile[][] fieldToNull){
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++)
                fieldToNull[i][j] = new Tile();
    }

    public Tile[][] getBoard(){
        return board;
    }

    public void generate(int difficulty) {
        board = new Tile[BOARD_WIDTH][BOARD_HEIGHT];
        completedBoard = new Tile[BOARD_WIDTH][BOARD_HEIGHT];
        nullTheField(getBoard());
        nullTheField(this.completedBoard);
        nextCell(0,0);
        copyField();
        setDefColor();
        makeHoles(difficulty);
    }

    private void copyField(){
        for (int row = 0 ; row < BOARD_WIDTH ; row++)
            for (int collum = 0 ; collum < BOARD_HEIGHT ; collum++)
                completedBoard[row][collum].setValue(board[row][collum].getValue());
    }

    public boolean isSolved(){
        for (int row = 0 ; row < BOARD_WIDTH ; row++)
            for (int collum = 0 ; collum < BOARD_HEIGHT ; collum++)
               if(completedBoard[row][collum].getValue() != board[row][collum].getValue())
                   return false;
        return true;
    }

    private boolean nextCell(int row, int col) {
        int[] toCheck = {1,2,3,4,5,6,7,8,9};
        Random random = new Random();
        int tmp;
        int current;
        int top = toCheck.length;

        for(int i=top-1;i>0;i--) {
            current = random.nextInt(i);
            tmp = toCheck[current];
            toCheck[current] = toCheck[i];
            toCheck[i] = tmp;
        }
        int nextCol;
        int nextRow = row;
        for (int i1 : toCheck) {
            if (legalMove(row, col, i1)) {
                board[row][col].setValue(i1);
                if (col == 8) {
                    if (row == 8)
                        return true;//We're done!
                    else {
                        nextCol = 0;
                        nextRow = row + 1;
                    }
                } else {
                    nextCol = col + 1;
                }
                if (nextCell(nextRow, nextCol))
                    return true;
            }
        }
        board[row][col].setValue(0);
        return false;
    }

    public boolean legalMove(int row, int collum, int currentNumber) {        //checking if move is possible...
        for(int i=0;i<9;i++) {
            if(currentNumber == board[row][i].getValue())
                return false;
        }
        for(int i=0;i<9;i++) {
            if(currentNumber == board[i][collum].getValue())
                return false;
        }
        if(isInQuadrant(row,collum,currentNumber))
            return false;
        return true;
    }

    private void makeHoles(double holesToMake) {    //making holes to the field...
        double remainingSquares = BOARD_HEIGHT * BOARD_WIDTH;
        double remainingHoles = holesToMake;

        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++) {
                double holeChance = remainingHoles/remainingSquares;
                if(Math.random() <= holeChance){
                    board[i][j].setValue(0);
                    board[i][j].setState(true);
                    board[i][j].setUsableColor(Color.WHITE);
                    remainingHoles--;
                }
                remainingSquares--;
            }
    }

    private boolean isInQuadrant(int col ,int row, int number){     //checking if number is in quadrant...
        int[] quadrant = quadrants.getQuadrant(row,col);
        for(int i = 0 ; i < 9 ; i++ ){
            int position = quadrant[i];
            int tempCol = position%10;
            position = position/10;
            int tempRow = position%10;
            if(number == board[tempRow][tempCol].getValue())
                return true;
        }
        return false;
    }

    private void setDefColor(){
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++) {
               int[]quadrantToColor = quadrants.getQuadrant(i,j);
               if(quadrantToColor == quadrants.getTotalQuadrants()[0]){
                    this.board[j][i].setDefaultColor(Color.CYAN);
                }
                else if(quadrantToColor == quadrants.getTotalQuadrants()[1]){
                    this.board[j][i].setDefaultColor(Color.PURPLE);
                }
                else if(quadrantToColor == quadrants.getTotalQuadrants()[2]){
                    this.board[j][i].setDefaultColor(Color.YELLOW);
                }
                else if(quadrantToColor == quadrants.getTotalQuadrants()[3]){
                    this.board[j][i].setDefaultColor(Color.GREEN);
                }
                else if(quadrantToColor == quadrants.getTotalQuadrants()[4]){
                    this.board[j][i].setDefaultColor(Color.BRIGHT_GREEN);
                }
                else if(quadrantToColor == quadrants.getTotalQuadrants()[5]){
                    this.board[j][i].setDefaultColor(Color.BRIGHT_PURPLE);
                }
                else if(quadrantToColor == quadrants.getTotalQuadrants()[6]){
                    this.board[j][i].setDefaultColor(Color.BRIGHT_YELLOW);
                }
                else if(quadrantToColor == quadrants.getTotalQuadrants()[7]){
                    this.board[j][i].setDefaultColor(Color.BRIGHT_CYAN);
                }
                else if(quadrantToColor == quadrants.getTotalQuadrants()[8]){
                    this.board[j][i].setDefaultColor(Color.BLUE);
                }
            }
    }
}