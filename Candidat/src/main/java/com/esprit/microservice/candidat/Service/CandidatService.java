package com.esprit.microservice.candidat.Service;

import com.esprit.microservice.candidat.Entity.Candidat;
import com.esprit.microservice.candidat.Repository.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CandidatService {
    @Autowired
    private CandidatRepository candidateRepository;

    //ajout
    public Candidat addCandidat(Candidat candidate) { return candidateRepository.save(candidate);}
    //update
    public Candidat updateCandidat(int id, Candidat newCandidat) { if (candidateRepository.findById(id).isPresent()) {

        Candidat existingCandidat = candidateRepository.findById(id).get(); existingCandidat.setNom(newCandidat.getNom()); existingCandidat.setPrenom(newCandidat.getPrenom()); existingCandidat.setEmail(newCandidat.getEmail());

        return candidateRepository.save(existingCandidat);
    } else
        return null;
    }

    //delete
    public String deleteCandidat(int id) {
        if (candidateRepository.findById(id).isPresent()) { candidateRepository.deleteById(id);
            return "candidat supprimé";
        } else
            return "candidat non supprimé";
    }


}
