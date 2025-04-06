package tn.esprit.publication.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.publication.Entity.Publication;
import tn.esprit.publication.Service.PublicationService;

import java.util.List;
@RestController
@RequestMapping("/publication")
public class PublicationRestApi {
    @Autowired
    private PublicationService publicationService;

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Publication> createPublication(@RequestBody Publication publication) {
        return new ResponseEntity<>(publicationService.addPublication(publication), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Publication> updatePublication(@PathVariable(value = "id") int id, @RequestBody Publication publication) {
        return new ResponseEntity<>(publicationService.updatePublication(id, publication), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deletePublication(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(publicationService.deletePublication(id), HttpStatus.OK);
    }

    @GetMapping("/publications")
    public ResponseEntity<List<Publication>> getAllPublications() {
        return new ResponseEntity<>(publicationService.getAllPublications(), HttpStatus.OK);
    }

    @GetMapping("/by-title/{title}")
    public ResponseEntity<List<Publication>> getPublicationsByTitle(@PathVariable("title") String title) {
        List<Publication> publications = publicationService.getByTitle(title);
        return new ResponseEntity<>(publications, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublicationById(@PathVariable("id") int id) {
        Publication publication = publicationService.getPublicationById(id);
        if (publication != null) {
            return new ResponseEntity<>(publication, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-author/{author}")
    public ResponseEntity<List<Publication>> getPublicationsByAuthor(@PathVariable("author") String author) {
        List<Publication> publications = publicationService.getByAuthor(author);
        return new ResponseEntity<>(publications, HttpStatus.OK);
    }

    @GetMapping("/publications/recent-first")
    public ResponseEntity<List<Publication>> getPublicationsRecentFirst() {
        return new ResponseEntity<>(publicationService.getAllPublicationsRecentFirst(), HttpStatus.OK);
    }

    @GetMapping("/publications/oldest-first")
    public ResponseEntity<List<Publication>> getPublicationsOldestFirst() {
        return new ResponseEntity<>(publicationService.getAllPublicationsOldestFirst(), HttpStatus.OK);
    }


}
