package sk.tuke.gamestudio.game.jigsawsudoku.adamcak.consoleui;


import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core.*;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.*;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleUI {
    private Field field;
    private GameState state;
    private Scanner scanner = new Scanner(System.in);

    private int difficulty;
    private int points;
    private long time;

    private static final String RED    = "\u001B[31m";

    private static final String BLUE   = "\u001B[34m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BRIGHT_GREEN  = "\u001B[92m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    private static final String BRIGHT_PURPLE = "\u001B[95m";
    private static final String BRIGHT_CYAN   = "\u001B[96m";

    @Autowired
    private ScoreService scoreService;
    @Autowired
    RatingService ratingService;
    @Autowired
    CommentService commentService;


    private static final String NONE = "\u001B[0m";

    public void play(Field field, Quadrants quadrants){
        this.state = GameState.PLAYING;
        this.field = field;
        this.points = 0;
        this.time = System.currentTimeMillis();
        displayLogo();
        startingQuote();
        field.generate(difficulty);
        gameLoop(quadrants);
        display(field.getBoard(),quadrants);
        System.out.println(BLUE + "Well you have won!!!! :( \n" + BLUE);
        endingQuote(true);
        scanner.close();
    }

    private void displayLogo(){
        System.out.print(BLUE + "\n" +
                "    _____  _____   ______    ______        _  ____      ____   ______  _____  _____  ______      ___   ___  ____  _____  _____  \n" +
                "   |_   _||_   _|.' ___  | .' ____ \\      / \\|_  _|    |_  _|.' ____ \\|_   _||_   _||_   _ `.  .'   `.|_  ||_  _||_   _||_   _| \n" +
                "     | |    | | / .'   \\_| | (___ \\_|    / _ \\ \\ \\  /\\  / /  | (___ \\_| | |    | |    | | `. \\/  .-.  \\ | |_/ /    | |    | |   \n" +
                " _   | |    | | | |   ____  _.____`.    / ___ \\ \\ \\/  \\/ /    _.____`.  | '    ' |    | |  | || |   | | |  __'.    | '    ' |   \n" +
                "| |__' |   _| |_\\ `.___]  || \\____) | _/ /   \\ \\_\\  /\\  /    | \\____) |  \\ \\__/ /    _| |_.' /\\  `-'  /_| |  \\ \\_   \\ \\__/ /    \n" +
                "`.____.'  |_____|`._____.'  \\______.'|____| |____|\\/  \\/      \\______.'   `.__.'    |______.'  `.___.'|____||____|   `.__.'     \n" +
                "                                                                                                                                \n" + NONE);
    }

//Starting quote method
    private void startingQuote(){
        System.out.println(BLUE + "Hello sir/ma'am/something else... Welcome to my game!..." + NONE);
        do {
            System.out.println(BLUE + "Please choose your difficulty " + RED + "(Number from 1 to 54) not something else!... " + NONE);
            try {
                difficulty = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(BLUE + "Something went wrong" + NONE);
            }
        }while (difficulty<1 || difficulty>54);
        setScoreByDifficulty(difficulty);
        System.out.println(BLUE +"And there's one more thing..." + RED + " The format is AB1 -> A is row, B is collum and 1 is number that you choose..." + NONE);
        System.out.println(BLUE + "If you understood type " + CYAN + "(yes/no)"+ BLUE + " of course :D..."+ NONE);
        String decision = scanner.nextLine().toUpperCase().replaceAll("\\s","");
        if(decision.equals("NO")){
            do{
                System.out.println(RED + "Okey ... ONE MORE TIME!" + NONE);
                System.out.println(BLUE + "Horizontally is a row and vertically is a column... So in format " + RED +
                        "AB1 'A' stands for a row, 'B' for a column and '1' for the number" + BLUE + " you want to insert... " + NONE);
                System.out.println(BLUE + "Type " + CYAN + "yes" +BLUE+" if you understand..." + NONE);
                decision = scanner.nextLine().toUpperCase().replaceAll("\\s","");
            }while(!decision.equals("YES"));
        }
        else if(!decision.equals("YES")) {
            System.out.println(BLUE + "See ya next time" + NONE);
            System.exit(0);
        }
        System.out.println(BLUE + "And the mighty game begins now...\n" + NONE);
        System.out.println(BLUE + "This is your Field...\n" + NONE);
    }
//GameLOOP
    private void gameLoop(Quadrants quadrants){
        do {
            display(this.field.getBoard(),quadrants);
            handleInput();
            if(field.isSolved()){
                state = GameState.SOLVED;
            }
        } while(state == GameState.PLAYING);
    }
//method to display sudoku on the console
    private void display(Tile[][] board, Quadrants quadrants) {
        char[][] quadrantsToDisplay = quadrants.getTotalFieldToPrint();
        int rowOfBoard = 0;
        displayCollumCharactersOfPosition();

        char rowCharacterToDisplay = 'A';
        for(int i=0;i<19;i++) {
            int collumOfBoard = 0;
            if (i%2 != 0) {
                System.out.print(rowCharacterToDisplay + " ");
                rowCharacterToDisplay++;
            }
            else System.out.print("  ");
            for(int j=0;j<19;j++){
                if(quadrantsToDisplay[i][j] == '0'){
                    if(board[rowOfBoard][collumOfBoard].getUsableColor()== Color.RED) {  // if there's bad move
                        System.out.print(RED + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                    }
                    else if(board[rowOfBoard][collumOfBoard].getUsableColor()== Color.WHITE) {
                        System.out.print(NONE + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                        }
                        else switch (board[rowOfBoard][collumOfBoard].getDefaultColor()){
                            case CYAN:
                                System.out.print(CYAN + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                            case PURPLE:
                                System.out.print(PURPLE + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                            case YELLOW:
                                System.out.print(YELLOW + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                            case GREEN:
                                System.out.print(GREEN + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                            case BRIGHT_CYAN:
                                System.out.print(BRIGHT_CYAN + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                            case BRIGHT_PURPLE:
                                System.out.print(BRIGHT_PURPLE + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                            case BRIGHT_YELLOW:
                                System.out.print(BRIGHT_YELLOW + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                            case BRIGHT_GREEN:
                                System.out.print(BRIGHT_GREEN + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                            case BLUE:
                                System.out.print(BLUE + board[rowOfBoard][collumOfBoard].getValue() + NONE + " ");
                                break;
                        }
                    collumOfBoard++;
                }
                else System.out.print(quadrantsToDisplay[i][j] + " ");
            }
            if(collumOfBoard == 9)
                rowOfBoard++;
            System.out.println();
        }
    }

    private void displayCollumCharactersOfPosition(){
        char collumCharacterToDisplay = 'A';
        System.out.print("  ");
        for(int i = 2 ; i < 21 ; i++ ){
            if(i%2 != 0 ){
                System.out.print(collumCharacterToDisplay + " ");
                collumCharacterToDisplay++;
            }
            else System.out.print("  ");
        }
        System.out.println();
    }
//handle input method
    private void handleInput(){
        String stringInput = scanner.nextLine().toUpperCase().replaceAll("\\s","");
        patternCompile(stringInput);
    }

    private void patternCompile(String input){
        if(input.equals("EXIT"))       //in case you're bored
            endingQuote(false);
        if ( Pattern.compile("([A-I])([A-I])([0-9])").matcher(input).matches() ) {
            int row = (int) input.charAt(0) -65;       //A in ASCII is 65
            int col = (int) input.charAt(1) -65 ;      // the same as above
            int numberToWrite = (int) input.charAt(2) -48; //0 in ASCII is 48
            if( field.getBoard()[row][col].getState()) {    // write only if it is possible
//                if (field.legalMove(row, col, numberToWrite)) {
//                    field.getBoard()[row][col].setUsableColor(Color.WHITE);
//                }
//                else field.getBoard()[row][col].setUsableColor(Color.RED);
//                field.getBoard()[row][col].setValue(numberToWrite);
            }
            else System.out.println(RED + "This can't be replaced" + NONE);  //else can't be replaced
        }
        else System.out.println("Try again...");    //if we type wrong input string
    }
//ending quote and adding score if isWon
    private void endingQuote(boolean won){
        String name;

        setScoreByTime(time);
        if(won){
            System.out.println(BLUE + "Your points -> " + this.points + NONE);
            System.out.println();
            System.out.println("Enter your name...");
            name = scanner.nextLine();
            Score score = new Score("Jigsawsudoku", name, this.points, new java.util.Date());

            scoreService.addScore(score);

            showScoreBoard(scoreService);

            leaveARating(name,"Jigsawsudoku");

            leaveAComment(name, "Jigsawsudoku");
        }else{
            System.out.println(RED + "Well... You can see the ScoreBoard, but you're looser" + NONE);
            showScoreBoard(scoreService);
        }
        System.out.println(GREEN + "Bye :)..." + NONE);
        System.exit(0);
    }
//setting and showing score...

    private void showScoreBoard(ScoreService scoreService){
        System.out.println(BLUE + "And this is a scoreboard...\n" + NONE);
        List<Score> bestScores = scoreService.getBestScores("Jigsawsudoku");
        for (Score score: bestScores) {
            System.out.println(score);
        }
        System.out.println();
    }

    private void setScoreByDifficulty(int difficulty){
        this.points = difficulty*10;
    }

    private void setScoreByTime(long time){
        time = System.currentTimeMillis() - time;
        time = time / 1000L;
        if(time<300) this.points += 50;
        else if (time<600) this.points += 30;
        else if (time<900) this.points += 20;
    }
//adding rating

    private void leaveARating(String name,String game){
        System.out.println(BLUE + "Would you like to rate my game?... (yes/no)" + NONE);
        String decision = scanner.nextLine().toUpperCase().replaceAll("\\s","");
        if(!"YES".equals(decision))
            return;
        int rating = 0;
        while (rating<1 || rating>5 ){
            System.out.println(BLUE + "Type from 1 to 5 how much you're satisfied with the game..." + NONE);
            try {
                rating = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(RED + "Only numbers..." + NONE);
            }
        }
        Rating wholeRatingToLeave = new Rating(name, game, rating, new java.util.Date());
        ratingService.setRating(wholeRatingToLeave);
        System.out.println( BLUE + "Your rating ->" + ratingService.getRating(game,name) + NONE);
        System.out.println(YELLOW + "And the average rating is -> " + ratingService.getAverageRating(game) + NONE);
    }

    private void leaveAComment( String name, String game){
        System.out.println(BLUE + "Would you like to add a comment?... (yes/no)" + NONE);
        String decision = scanner.nextLine().toUpperCase().replaceAll("\\s","");
        if(!"YES".equals(decision))
            return;
        System.out.println(BLUE + "Leave a comment here :)..." + BLUE);
        String text = scanner.nextLine();
        Comment comment = new Comment(name, game,text, new java.util.Date());

        commentService.addComment(comment);

        System.out.println(BLUE + "And this is a comment board...\n" + NONE);
        List<Comment> commentList = commentService.getComments("Jigsawsudoku");

        for (Comment c: commentList) {
            System.out.println(c);
        }
        System.out.println();
    }
}