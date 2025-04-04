package com.esprit.microservice.forum.Repository;

import com.esprit.microservice.forum.Entity.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Integer> {
    @Query("select c from Forum c where c.title like :title")
    public Page<Forum> forumByTitle(@Param("title") String n, Pageable pageable);
    @Query("SELECT f FROM Forum f WHERE " +
            "(:query IS NULL OR " +
            "LOWER(f.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.subject) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.tags) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Forum> searchForums(@Param("query") String query);

}
