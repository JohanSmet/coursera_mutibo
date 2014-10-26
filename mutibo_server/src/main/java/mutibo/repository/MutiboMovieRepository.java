/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.repository;

import java.util.List;
import mutibo.data.MutiboMovie;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository that stores MutiboMovies
 * @author Redacted
 */
public interface MutiboMovieRepository extends CrudRepository<MutiboMovie, String>
{
 	List<MutiboMovie> findByNameLike(String pattern);
}

