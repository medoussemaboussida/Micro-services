package com.esprit.microservice.forum.Repository;

import com.esprit.microservice.forum.Entity.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ForumRepository extends JpaRepository<Forum, Integer> {
    @Query("select c from Forum c where c.title like :title")
    public Page<Forum> forumByTitle(@Param("title") String n, Pageable pageable);

}
