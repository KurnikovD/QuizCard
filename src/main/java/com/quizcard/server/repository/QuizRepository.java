package com.quizcard.server.repository;

import com.quizcard.server.entity.Quiz;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, String>, JpaSpecificationExecutor<Quiz> {

    List<Quiz> findAllByUser_Id(String id);

    Quiz findByIdAndUser_Id(String id, String userId);

    List<Quiz> findByTitleContainsIgnoreCase(String title);

    List<Quiz> findByUser_IdOrIsProtect(String id, boolean isProtect);

    static Specification<Quiz> byUser(String userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    static Specification<Quiz> byIsProtect(boolean isProtect) {
        return (root, query, cb) -> cb.equal(root.get("isProtect"), isProtect);
    }


}
