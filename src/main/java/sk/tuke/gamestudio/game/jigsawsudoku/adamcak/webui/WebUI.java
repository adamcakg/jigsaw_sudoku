package sk.tuke.gamestudio.game.jigsawsudoku.adamcak.webui;

import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core.Color;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core.Field;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core.Quadrants;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core.Tile;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;

import java.util.Formatter;

public class WebUI {

    private Quadrants quadrants = new Quadrants();
    private Field field = new Field(quadrants);
    private String nameOfPlayer = null;
    private int row = 0;
    private int collum = 0;
    private int points = 0;
    private int difficulty = 1;
    private int controldiff;
    private ScoreService scoreService;
    private CommentService commentService;
    private RatingService ratingService;
    
    public WebUI(ScoreService scoreService, CommentService commentService, RatingService ratingService) {
        this.scoreService = scoreService;
        this.commentService = commentService;
        this.ratingService = ratingService;
    }

    public void processCommand(String rowString, String columnString, String valueString) {
        if(field.getBoard()==null)
            generateSudoku(field,1);

        if(!field.isSolved()) {
            int value = 0;
            try {
                this.row = Integer.parseInt(rowString);
                this.collum = Integer.parseInt(columnString);
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException e) {

            }
            if (field.getBoard()[row][collum].getState())
                if (value < 10 && value > 0) {
                    if (field.legalMove(row, collum, value))
                        field.getBoard()[row][collum].setUsableColor(Color.WHITE);
                    else field.getBoard()[row][collum].setUsableColor(Color.RED);

                    field.getBoard()[row][collum].setValue(value);
                }
        }
    }

    public String renderAsHtml() {
        if(field.getBoard() == null){
            generateSudoku(field,difficulty);
            controldiff = difficulty;
        }

        if(difficulty != controldiff){
            generateSudoku(field,difficulty);
            controldiff=difficulty;
        }
        Formatter sb = new Formatter();
        if (field.isSolved())
            sb.format(winningScreen());
        else {
            sb.format("<table class='field mytable'>\n");
            for (int row = 0; row < 9; row++) {
                sb.format("<tr>\n");
                for (int column = 0; column < 9; column++) {
                    Tile tile = field.getBoard()[row][column];
                    sb.format("<td>\n");
                    if (tile.getState()) {
                        sb.format("<p class= 'size " + getColor(tile) + "'>");
                        if (tile.getValue() != 0) {
                            sb.format("<a href='game?row=%d&column=%d' class='" + getUsableColor(tile) + "'>", row, column);
                            sb.format("" + tile.getValue());
                        } else {
                            sb.format("<a href='game?row=%d&column=%d' class='link'>", row, column);
                        }
                        sb.format("</a>");
                        sb.format("</p>");
                    } else {
                        sb.format("<p  class='" + getColor(tile) + " size'>");
                        sb.format("" + tile.getValue());
                        sb.format("</p>");
                    }
                }
            }
            sb.format("</table>\n");
            sb.format("<div>");
            sb.format("<div class='menu'>");
            for (int i = 1; i < 10; i++) {
                sb.format("<a href='game?row=%d&column=%d&value=%d'>", this.row, this.collum, i);
                sb.format(i + "</a>");
            }
            sb.format("</div>");
            sb.format("<div>");
            if (field.isSolved())
                sb.format("<div class='winnerscreen'> <p> you have won</p></div>");
        }
        return sb.toString();
    }

    private void generateSudoku(Field field, int difficulty){
        quadrants.updateQuadrants();
        field.generate(difficulty);
        points = 10 * difficulty;
    }

    private String getUsableColor(Tile tile){
        if(tile.getUsableColor() == Color.WHITE)
            return "white";
        return "";
    }

    private String getColor(Tile tile){
        Color color = tile.getUsableColor();
        if(color == Color.RED)
            return "red";

        color = tile.getDefaultColor();
        switch (color){
            case CYAN:
                return "color1";
            case PURPLE:
                return "color2";
            case GREEN:
                return "color3";
            case YELLOW:
                return "color4";
            case BRIGHT_CYAN:
                return "color5";
            case BRIGHT_PURPLE:
                return "color6";
            case BRIGHT_YELLOW:
                return "color7";
            case BRIGHT_GREEN:
                return "color8";
            case BLUE:
                return "color9";
        }
        return "";
    }

    public void setDifficulty(String difficulty) {
        try {
            this.difficulty = Integer.parseInt(difficulty) * 10;
        }catch (NumberFormatException e){
        }
    }

    public void setName(String name){
        if (name == null) return;
        this.nameOfPlayer = name;
    }

    public void setComment(String string){
        if(nameOfPlayer == null )return;
        if(string == null)return;
        if(string.equals("")) return;
        Comment comment = new Comment(nameOfPlayer, "JigsawSudoku", string, new java.util.Date());
        commentService.addComment(comment);

    }

    public void setRating(String ratingString){
        if(nameOfPlayer == null || ratingString == null)return;
        int number;
        try{
            number = Integer.parseInt(ratingString);
        }catch (NumberFormatException e){
            return;
        }
        if(number>5||number<1){
            return;
        }
        Rating rating = new Rating(nameOfPlayer, "JigsawSudoku", number, new java.util.Date());
        ratingService.setRating(rating);
        nameOfPlayer = null; // only in this sort of things
        generateSudoku(field,difficulty);
    }

    private void setScore(String name){
        if(name == null) return;
        Score score = new Score("JigsawSudoku",name,points,new java.util.Date());
        scoreService.addScore(score);
    }


    private String winningScreen(){
        StringBuilder sb = new StringBuilder();
        if(nameOfPlayer == null){
            sb.append("<div class=\"container\">\n" +
                    "        <div class=\"row\">\n" +
                    "            <div class=\"col-md-6 mx-auto \">\n" +
                    "                <div class=\"center big\">\n" +
                    "                    <p>You have won, congrats</p>\n" +
                    "                </div>\n" +
                    "                <div class=\"center\">\n" +
                    "                   <p>Your score is " + points + "</p>" +
                    "                    <p>Please enter your name</p>\n" +
                    "                </div>\n" +
                    "                <div>\n" +
                    "                    <form method=\"post\" >\n" +
                    "                        <div class=\"form-group\">\n" +
                    "                            <input type=\"text\" name=\"player\" class=\"form-control\" aria-describedby=\"Name\" placeholder=\"Name\" required>\n" +
                    "                            <div class=\"buttonCenter\">\n" +
                    "                                <button type=\"submit\" class=\"btn btn-outline-light \">Submit</button>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </form>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>");
        }
        else {
            setScore(nameOfPlayer);
            ratingService.getRating("JigsawSudoku",nameOfPlayer);
            sb.append(" <div class=\"container\">\n" +
                    "        <div class=\"row\">\n" +
                    "            <div class=\"col-md-6 mx-auto \">\n" +
                    "                <div class=\"center\">\n" +
                    "                    <p>Please leave a comment and rating :)</p>\n" +
                    "                </div>\n" +
                    "                <form class=\"rating-form\" method=\"post\">\n" +
                    "                    <div class=\"form-group\">\n" +
                    "                        <input type=\"text\" name=\"comment\" class=\"form-control\" aria-describedby=\"Leave a comment\" placeholder=\"Leave a comment\">\n" +
                    "                        <div class=\"margin\">\n" +
                    "                            <div class=\"value\">3</div>\n" +
                    "                            <input type=\"range\" min=\"1\" max=\"5\" step=\"1\" name=\"rating\" value=\"3\">\n" +
                    "                        </div>\n" +
                    "                        <div class=\"buttonCenter marginToSubmit\">\n" +
                    "                            <button type=\"submit\" class=\"btn btn-outline-light \">Submit</button>\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                </form>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>");
        }
        return sb.toString();
    }
}
