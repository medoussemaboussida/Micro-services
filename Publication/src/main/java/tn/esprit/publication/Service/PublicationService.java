package tn.esprit.publication.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import tn.esprit.publication.Entity.Publication;
import tn.esprit.publication.Repository.PublicationRepository;
import org.springframework.data.domain.Sort;
import java.util.List;


@Service
public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;

    // ajout
    public Publication addPublication(Publication publication) {
        return publicationRepository.save(publication);
    }

    // update
    public Publication updatePublication(int id, Publication newPublication) {
        if (publicationRepository.findById(id).isPresent()) {
            Publication existingPublication = publicationRepository.findById(id).get();
            existingPublication.setTitle(newPublication.getTitle());
            existingPublication.setContent(newPublication.getContent());
            existingPublication.setAuthor(newPublication.getAuthor());
            existingPublication.setCreationDate(newPublication.getCreationDate());

            return publicationRepository.save(existingPublication);
        } else {
            return null;
        }
    }

    // delete
    public String deletePublication(int id) {
        if (publicationRepository.findById(id).isPresent()) {
            publicationRepository.deleteById(id);
            return "publication supprimée";
        } else {
            return "publication non supprimée";
        }
    }

    // get
    public List<Publication> getAllPublications() {
        return publicationRepository.findAll();
    }

    // get by title
    public List<Publication> getByTitle(String title) {
        return publicationRepository.findByTitle(title);
    }

    //getbyid
    public Publication getPublicationById(int id) {
        return publicationRepository.findById(id).orElse(null);
    }
    //par auteur
    public List<Publication> getByAuthor(String author) {
        return publicationRepository.findByAuthor(author);
    }

    // Publications du plus récent au plus ancien
    public List<Publication> getAllPublicationsRecentFirst() {
        return publicationRepository.findAll(Sort.by(Sort.Direction.DESC, "creationDate"));
    }

    // Publications du plus ancien au plus récent
    public List<Publication> getAllPublicationsOldestFirst() {
        return publicationRepository.findAll(Sort.by(Sort.Direction.ASC, "creationDate"));
    }

}