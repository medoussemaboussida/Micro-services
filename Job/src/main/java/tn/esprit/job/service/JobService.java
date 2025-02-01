package tn.esprit.job.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.job.entity.Job;
import tn.esprit.job.repository.JobRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JobService implements IJobService {

    @Autowired
    JobRepository jobRepository;

    @Override
    public List<Job> readAll() {
        return jobRepository.findAll();
    }

    @Override
    public Job readById(int id) {
        return jobRepository.findById(id).orElse(new Job());
    }

    @Override
    public Job create(Job job) {

        return jobRepository.save(job);

    }

    @Override
    public Job update(Job job) {

        return jobRepository.save(job);
    }

    @Override
    public Boolean delete(int id) {
        if (jobRepository.existsById(id)){
            jobRepository.deleteById(id);
            return true;
        }else
            return false;

    }


    @Override
    public void updateJobEtat(int id, String etat) {
        Optional<Job> jobOptional = jobRepository.findById(id);

        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            boolean isAvailable = etat.equalsIgnoreCase("oui"); // "oui" = true, "non" = false
            job.setEtat(isAvailable); // Mettre à jour l'état
            jobRepository.save(job); // Enregistrer les modifications
        } else {
            throw new RuntimeException("Job non trouvé avec ID : " + id);
        }
    }

}
