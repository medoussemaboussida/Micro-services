package com.esprit.microservice.forum.Service;

import com.esprit.microservice.forum.Entity.Forum;
import com.esprit.microservice.forum.Repository.ForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {
    @Autowired
    private ForumRepository forumRepository;

    //ajout
    public Forum addForum(Forum forum) {
        return forumRepository.save(forum);
    }

    //update
    public Forum updateForum(int id, Forum newForum) {
        if (forumRepository.findById(id).isPresent()) {
            Forum existingForum = forumRepository.findById(id).get();
            existingForum.setTitle(newForum.getTitle());
            existingForum.setSubject(newForum.getSubject());
            existingForum.setTags(newForum.getTags());

            return forumRepository.save(existingForum);
        } else {
            return null;
        }
    }

    //delete
    public String deleteForum(int id) {
        if (forumRepository.findById(id).isPresent()) {
            forumRepository.deleteById(id);
            return "forum supprimé";
        } else {
            return "forum non supprimé";
        }
    }

    //get
    public List<Forum> getAllForums() {
        return forumRepository.findAll();
    }
}