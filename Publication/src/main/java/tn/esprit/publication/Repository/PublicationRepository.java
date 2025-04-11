package tn.esprit.publication.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.publication.Entity.Publication;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    List<Publication> findByTitle(String title);
    List<Publication> findByAuthor(String author);  // Add this line
    @Query("select c from Publication c where c.title like :title")
    public Page<Publication> publicationByTitle(@Param("title") String n, Pageable pageable);


}
