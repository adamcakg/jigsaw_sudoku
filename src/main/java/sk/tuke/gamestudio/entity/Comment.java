package sk.tuke.gamestudio.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private int ident;

    private String player;
    private String game;
    private String comment;
    private Date commentedOn;

    public Comment() {}

    public Comment(String player, String game, String comment, Date commentedOn) {
        this.player = player;
        this.game = game;
        this.comment = comment;
        this.commentedOn = commentedOn;
    }

    public int getIdent() { return ident; }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCommentedOn() {
        return commentedOn;
    }

    public void setCommentedOn(Date commentedOn) {
        this.commentedOn = commentedOn;
    }

    @Override
    public String toString() {
//        final StringBuilder sb = new StringBuilder("Comment{");
//        sb.append("player='").append(player).append('\'');
////        sb.append(", game='").append(game).append('\'');
////        sb.append(", comment='").append(comment).append('\'');
////        sb.append(", commentedOn=").append(commentedOn);
////        sb.append('}');

        return "| Score of " +
                game  +
                " | Player: " + player +
                ", Comment: " + comment +
                ", Date: " + commentedOn;


        //return sb.toString();
    }
}
