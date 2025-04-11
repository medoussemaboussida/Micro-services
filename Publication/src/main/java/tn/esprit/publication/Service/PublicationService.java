package tn.esprit.publication.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import tn.esprit.publication.Entity.Publication;
import tn.esprit.publication.Repository.PublicationRepository;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;

    // Méthode pour générer un PDF avec toutes les publications
    public byte[] exportPublicationsToPdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Ajouter un titre au document
        document.add(new Paragraph("Liste des Publications").setBold().setFontSize(20));

        // Récupérer toutes les publications
        List<Publication> publications = getAllPublications();

        // Ajouter chaque publication au PDF
        for (Publication pub : publications) {
            document.add(new Paragraph("ID: " + pub.getId()));
            document.add(new Paragraph("Titre: " + pub.getTitle()));
            document.add(new Paragraph("Contenu: " + pub.getContent()));
            document.add(new Paragraph("Auteur: " + pub.getAuthor()));
            document.add(new Paragraph("Date de création: " + pub.getCreationDate()));
            document.add(new Paragraph("------------------------"));
        }

        // Fermer le document
        document.close();
        return baos.toByteArray();
    }

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