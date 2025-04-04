package com.esprit.microservice.forum.Controller;

import com.esprit.microservice.forum.Entity.Forum;
import com.esprit.microservice.forum.Service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum")
public class ForumRestAPI {

    @Autowired
    private ForumService forumService;

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Forum> createForum(@RequestBody Forum forum) {
        return new ResponseEntity<>(forumService.addForum(forum), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Forum> updateForum(@PathVariable(value = "id") int id, @RequestBody Forum forum){
        return new ResponseEntity<>(forumService.updateForum(id, forum), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteForum(@PathVariable(value = "id") int id){
        return new ResponseEntity<>(forumService.deleteForum(id), HttpStatus.OK);
    }

    @GetMapping("/forums")
    public ResponseEntity<List<Forum>> getAllForums(){
        return new ResponseEntity<>(forumService.getAllForums(), HttpStatus.OK);
    }
    // la recherche
    @GetMapping("/search")
    public ResponseEntity<List<Forum>> searchForums(
            @RequestParam(value = "query", required = false) String query) {
        List<Forum> forums = forumService.searchForums(query);
        return new ResponseEntity<>(forums, HttpStatus.OK);
    }
}