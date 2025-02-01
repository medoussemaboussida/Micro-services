package tn.esprit.job.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.job.entity.Job;
import tn.esprit.job.service.IJobService;

import java.util.List;

@Tag(name = "Gestion Job")
@RestController
@RequestMapping("api/job")
public class JobController {

    @Autowired
    IJobService jobService;

    @Operation(summary = "get all entities", description = "get all entities")
    @GetMapping("getAll")
    public List<Job> readAll(){
        return jobService.readAll();
    }
    @Operation(summary = "get entity by id" , description = "get entity by id")
    @GetMapping("getById/{id}")
    Job readById(@PathVariable int id){
        return jobService.readById(id);
    }


    @PostMapping("add")
    @Operation(summary = "add entity" , description = "add entity")
    Job create(@RequestBody Job job){
        return jobService.create(job);
    }
    @PutMapping("update")
    @Operation(summary ="update entity" ,description = "update entity")
    Job update (@RequestBody Job job){
        return jobService.update(job);
    }
    @DeleteMapping("delete/{id}")
    @Operation(summary ="delete entity" , description = "delete entity")
    Boolean delete(@PathVariable int id){
        return jobService.delete(id);
    }

    @PutMapping("/{id}/etat")
    public ResponseEntity<String> updateJobEtat(@PathVariable int id, @RequestParam String etat) {
        try {
            jobService.updateJobEtat(id, etat);
            return ResponseEntity.ok("État du job mise à jour avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }



}
