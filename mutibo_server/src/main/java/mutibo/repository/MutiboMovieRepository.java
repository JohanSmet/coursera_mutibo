package mutibo.repository;

import java.util.List;
import mutibo.data.MutiboMovie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository that stores MutiboMovies
 * @author Redacted
 */

@Repository
public interface MutiboMovieRepository extends CrudRepository<MutiboMovie, String>
{
 	List<MutiboMovie> findByNameLike(String pattern);
}

