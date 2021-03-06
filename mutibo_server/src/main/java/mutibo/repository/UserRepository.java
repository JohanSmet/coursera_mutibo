/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.repository;

import mutibo.data.User;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Redacted
 */

@Repository
public interface UserRepository extends CrudRepository<User, ObjectId>
{
	User findByUsername(String username);
	User findByGoogleId(String googleId);
	User findByFacebookId(String facebookId);
}
