package sk.tuke.gamestudio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core.Field;
import sk.tuke.gamestudio.game.jigsawsudoku.adamcak.core.Quadrants;
import sk.tuke.gamestudio.service.*;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = { "sk.tuke.gamestudio" },
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "sk.tuke.gamestudio.server.*"))
public class SpringClient {
    public static void main(String[] args) {
        
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);
        
        //SpringApplication.run(Springclient_main.class, args);
    }

    @Bean
    public CommandLineRunner runner(ConsoleUI ui, Field field, Quadrants quadrants){
        return (args) -> ui.play(field,quadrants);
    }

    @Bean
    public ConsoleUI consoleUI(){
        return new ConsoleUI();
    }

    @Bean
    public Quadrants quadrants(){
        return new Quadrants();
    }

    @Bean
    public Field field(Quadrants quadrants){
        return new Field(quadrants);
    }

    @Bean
    public ScoreService scoreService(){
        return new ScoreServiceRestClient();
    }

    @Bean
    public CommentService commentService(){
        return new CommentServiceRestClient();
    }
    @Bean
    public RatingService ratingService(){
        return new RatingServiceRestClient();
    }


/*
    public static void main(String[] args){
        Quadrants quadrants = new Quadrants();
        quadrants.updateQuadrants();

        ConsoleUI consoleUI = new ConsoleUI();

        Field sg = new Field(quadrants);
        consoleUI.play(sg,quadrants);
    }

*/

}
