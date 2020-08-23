package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Score;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class ScoreServiceJPA implements ScoreService {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void addScore(Score score) throws ScoreException {
        entityManager.persist(score);
    }

    @Override
    public List<Score> getBestScores(String game) throws ScoreException {
        return entityManager.createQuery("SELECT s from Score s where s.game=:game order by s.points desc")
                .setParameter("game",game).setMaxResults(10).getResultList();
    }
}
