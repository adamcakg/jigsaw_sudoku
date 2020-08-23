package sk.tuke.gamestudio.service;


import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";

    public static final String INSERT_RATING =
            "INSERT INTO rating(game, player, rating, ratedon) VALUES (?, ?, ?, ?)";

    public static final String SELECT_RATING =
            "SELECT game, player, rating, ratedon FROM rating WHERE game = ? ";

    public static final String SELECT_PLAYER =
            "SELECT game, player, rating, ratedon FROM rating WHERE game = ? AND player = ? ";

    public static final String UPDATE_RATING =
            "UPDATE rating SET rating = ?, ratedon = ? WHERE game = ? AND player = ? ";
    @Override
    public void setRating(Rating rating) throws RatingException {
        if(getRating(rating.getGame(),rating.getPlayer()) != 0) {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                try(PreparedStatement ps = connection.prepareStatement(UPDATE_RATING)){
                    ps.setInt(1,rating.getRating());
                    ps.setDate(2,new Date(rating.getRatedon().getTime()));
                    ps.setString(3, rating.getGame());
                    ps.setString(4, rating.getPlayer());
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RatingException("Error saving Rating as existing", e);
            }

        }else{
                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                try(PreparedStatement ps = connection.prepareStatement(INSERT_RATING)){
                    ps.setString(1, rating.getGame());
                    ps.setString(2, rating.getPlayer());
                    ps.setInt(3, rating.getRating());
                    ps.setDate(4, new Date(rating.getRatedon().getTime()));

                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RatingException("Error saving Rating", e);
            }
        }

    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        int sumOfRatings = 0;
        int countOfRatings = 0;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(SELECT_RATING)){
                ps.setString(1, game);
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        Rating rating = new Rating(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getTimestamp(4)
                        );
                        sumOfRatings += rating.getRating();
                        countOfRatings++;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ScoreException("Error loading Rating", e);
        }

        return sumOfRatings/countOfRatings;
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        Rating rating = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(SELECT_PLAYER)){
                ps.setString(1, game);
                ps.setString(2, player);
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        rating = new Rating(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getTimestamp(4)
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new ScoreException("Error loading Rating of player", e);
        }

        if (rating == null)
            return 0;

        return rating.getRating();
    }
}
