package CG.zuulsecurity.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import CG.zuulsecurity.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    //TO find a User with his/her email
    User findByEmail(String email);
}