package sk.tuke.gamestudio.server.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.webui.WebUI;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;

import java.util.List;

//http://localhost:8080/
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class JigsawsudokuAdamcakController {

    private final WebUI webUI;

    private final ScoreService scoreService;
    private final CommentService commentService;
    private final RatingService ratingService;

    public JigsawsudokuAdamcakController(ScoreService scoreService, CommentService commentService, RatingService ratingService) {
        this.scoreService = scoreService;
        this.commentService = commentService;
        this.ratingService = ratingService;
        this.webUI = new WebUI(scoreService,commentService, ratingService);

    }
    @RequestMapping("JigsawSudoku/main")
    public String diff(){
        return "jigsawsudoku-adamcak";
    }

    @RequestMapping("JigsawSudoku/game")
    public String JigsawSudoku(@RequestParam(name = "row", required = false) String rowString,
                               @RequestParam(name = "column", required = false) String columnString,
                               @RequestParam(name = "value", required = false) String valueString,
                               @RequestParam(name = "player", required = false) String playersName,
                               @RequestParam(name = "diff", required = false) String diffString,
                               @RequestParam(name = "comment", required = false) String commentString,
                               @RequestParam(name = "rating", required = false) String ratingString,

                               Model model)
    {
        webUI.setDifficulty(diffString);
        webUI.setName(playersName);
        webUI.setComment(commentString);
        webUI.setRating(ratingString);

        webUI.processCommand(rowString, columnString, valueString);
        model.addAttribute("webUI", webUI);

        return "jigsawsudoku-adamcak-game"; //same name as the template
    }

    @RequestMapping("JigsawSudoku/comment")
    public String comment(Model model){
        List<Comment> comments = commentService.getComments("JigsawSudoku");
        model.addAttribute("comments", comments);

        return "jigsawsudoku-adamcak-comment-site";
    }

    @RequestMapping("JigsawSudoku/score")
    public String score(Model model){
        List<Score> bestScores = scoreService.getBestScores("JigsawSudoku");
        model.addAttribute("scores", bestScores);
        return "jigsawsudoku-adamcak-score-rating";
    }

    @RequestMapping("JigsawSudoku/rating")
    public String rating(Model model){
        int avg = ratingService.getAverageRating("JigsawSudoku");
        model.addAttribute("avgRating",avg);
        return "jigsawsudoku-adamcak-rating-page";
    }


}
