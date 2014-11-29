package mutibo.repository;

import java.util.List;
import mutibo.data.MutiboMatch;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Redacted
 */
@Repository
public interface MutiboMatchRepository extends MongoRepository<MutiboMatch, ObjectId>
{
	List<MutiboMatch> findByState(int state);
}
