package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Comment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class CommentServiceRestClient implements CommentService {
    private static final String URL = "http://localhost:8080/rest/comment";

    @Override
    public void addComment(Comment comment) throws ScoreException {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(URL)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(comment, MediaType.APPLICATION_JSON), Response.class);
        } catch (Exception e) {
            throw new ScoreException("Error saving comment", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws ScoreException {
        try {
            Client client = ClientBuilder.newClient();
            return client.target(URL)
                    .path("/" + game)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Comment>>() {
                    });
        } catch (Exception e) {
            throw new ScoreException("Error loading comment", e);
        }
    }
}
