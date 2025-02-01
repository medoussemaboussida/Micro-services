package tn.esprit.job.service;

import tn.esprit.job.entity.Job;

import java.util.List;

public interface IJobService {
    List<Job> readAll();
    Job readById(int id);
    Job create(Job job);
    Job update (Job job);
    Boolean delete(int id);
    void updateJobEtat(int id, String etat);


}
