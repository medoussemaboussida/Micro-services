package com.esprit.microservice.candidat.Controller;

import com.esprit.microservice.candidat.Entity.Candidat;
import com.esprit.microservice.candidat.Service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CandidatRestAPI {
    private String title="Hello, i'm the candidate Micro-Service";

    @RequestMapping("/hello") public String sayHello(){
        System.out.println(title); return title;
    }

    @Autowired
    private CandidatService candidatService;

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE) @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Candidat> createCandidat(@RequestBody Candidat candidat) { return new ResponseEntity<>(candidatService.addCandidat(candidat), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Candidat> updateCandidat(@PathVariable(value = "id") int id,@RequestBody Candidat candidat){ return new ResponseEntity<>(candidatService.updateCandidat(id, candidat), HttpStatus.OK);
    }


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteCandidat(@PathVariable(value = "id") int id){ return new ResponseEntity<>(candidatService.deleteCandidat(id), HttpStatus.OK);
    }


}
