/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.repository;

import java.util.List;
import mutibo.data.MutiboSetResult;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Redacted
 */

@Repository
public interface MutiboSetResultRepository extends CrudRepository<MutiboSetResult, ObjectId>
{
	List<MutiboSetResult> findByIsCumulated(Boolean isCumulated);	
}
