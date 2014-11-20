/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.repository;

import java.util.List;
import mutibo.data.MutiboUserResult;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Redacted
 */

@Repository
public interface MutiboUserResultRepository extends CrudRepository<MutiboUserResult, ObjectId>
{
	List<MutiboUserResult>	findByPlayedGamesGreaterThanOrderByBestScoreDescTotalScoreDesc(int playedGames);

	List<MutiboUserResult>	findByRankingBetweenOrderByRankingAsc(int start, int end);

	MutiboUserResult		findByNickName(String nickName);
}
