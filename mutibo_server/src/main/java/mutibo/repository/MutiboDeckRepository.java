/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.repository;

import java.util.List;
import mutibo.data.MutiboDeck;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository that store MutiboDecks
 * @author Redacted
 */

@Repository
public interface MutiboDeckRepository extends CrudRepository<MutiboDeck, Long>
{
	List<MutiboDeck> findByReleased(boolean released);
}
