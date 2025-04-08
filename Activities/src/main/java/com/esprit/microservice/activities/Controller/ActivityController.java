package com.esprit.microservice.activities.Controller;

import com.esprit.microservice.activities.Entity.Activity;
import com.esprit.microservice.activities.Service.ActivityService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/activities") //
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private JavaMailSender mailSender;

    @Value("${twilio.account_sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth_token}")
    private String twilioAuthToken;

    @Value("${twilio.phone_number}")
    private String twilioPhoneNumber;

    private static final String EXCLUDED_EMAIL = "ghassen.benmahmoud@esprit.tn";

    // Envoyer SMS pour une activité
    // Envoyer SMS pour une activité
    @PostMapping("/{id}/send-sms")
    public ResponseEntity<String> sendActivitySMS(
            @PathVariable Long id,
            @RequestParam(required = false) String toPhoneNumber) {
        System.out.println("Début de sendActivitySMS pour ID: " + id);
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            System.out.println("Activité non trouvée pour ID: " + id);
            return new ResponseEntity<>("Activité non trouvée", HttpStatus.NOT_FOUND);
        }

        if (toPhoneNumber == null || toPhoneNumber.trim().isEmpty()) {
            System.out.println("Numéro de téléphone non fourni");
            return new ResponseEntity<>("Le numéro de téléphone est requis", HttpStatus.BAD_REQUEST);
        }

        System.out.println("Numéro reçu: " + toPhoneNumber);
        // Normaliser le numéro : supprimer espaces et ajouter "+" si absent
        String normalizedNumber = toPhoneNumber.replaceAll("\\s+", "");
        if (!normalizedNumber.startsWith("+")) {
            normalizedNumber = "+" + normalizedNumber;
        }

        if (!normalizedNumber.matches("^\\+[1-9]\\d{1,14}$")) {
            System.out.println("Format invalide pour: " + normalizedNumber);
            return new ResponseEntity<>("Format de numéro invalide. Utilisez le format E.164 (ex: +21612345678)",
                    HttpStatus.BAD_REQUEST);
        }

        // Récupérer les credentials dynamiquement
        String accountSid = twilioAccountSid.isEmpty() ? System.getenv("TWILIO_ACCOUNT_SID") : twilioAccountSid;
        String authToken = twilioAuthToken.isEmpty() ? System.getenv("TWILIO_AUTH_TOKEN") : twilioAuthToken;
        String phoneNumber = twilioPhoneNumber.isEmpty() ? System.getenv("TWILIO_PHONE_NUMBER") : twilioPhoneNumber;

        if (accountSid == null || authToken == null || phoneNumber == null) {
            System.out.println("Credentials Twilio manquants");
            return new ResponseEntity<>("Credentials Twilio manquants", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println("Tentative d'envoi SMS à: " + normalizedNumber + " depuis: " + phoneNumber);
        try {
            Twilio.init(accountSid, authToken);
            System.out.println("Twilio initialisé");

            String messageBody = "Activité: " + activity.getTitle() +
                    "\nDescription: " + activity.getDescription() +
                    "\nCatégorie: " + activity.getCategory();
            System.out.println("Message: " + messageBody);

            Message message = Message.creator(
                    new PhoneNumber(normalizedNumber),
                    new PhoneNumber(phoneNumber),
                    messageBody
            ).create();

            System.out.println("SMS envoyé, SID: " + message.getSid());
            return new ResponseEntity<>("SMS envoyé avec succès: " + message.getSid(), HttpStatus.OK);
        } catch (com.twilio.exception.ApiException e) {
            System.out.println("Erreur Twilio: " + e.getMessage());
            return new ResponseEntity<>("Erreur Twilio: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println("Erreur générale: " + e.getMessage());
            return new ResponseEntity<>("Erreur lors de l'envoi du SMS: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Envoyer Email pour une activité
    @PostMapping("/{id}/send-email")
    public ResponseEntity<String> sendActivityEmail(
            @PathVariable Long id,
            @RequestParam(required = false) String toEmail) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return new ResponseEntity<>("Activité non trouvée", HttpStatus.NOT_FOUND);
        }

        // Vérifier si l'email est fourni
        if (toEmail == null || toEmail.trim().isEmpty()) {
            return new ResponseEntity<>("L'adresse email est requise", HttpStatus.BAD_REQUEST);
        }

        // Vérifier si l'email est exclu
        if (EXCLUDED_EMAIL.equalsIgnoreCase(toEmail)) {
            return new ResponseEntity<>("Cet email est exclu du service de mailing", HttpStatus.FORBIDDEN);
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Détails de l'activité: " + activity.getTitle());
            message.setText("Bonjour,\n\nVoici les détails de l'activité:\n" +
                    "Titre: " + activity.getTitle() + "\n" +
                    "Description: " + activity.getDescription() + "\n" +
                    "Catégorie: " + activity.getCategory() + "\n" +
                    "Date de création: " + activity.getCreatedAt() + "\n\n" +
                    "Cordialement,\nL'équipe Activities");

            mailSender.send(message);
            return new ResponseEntity<>("Email envoyé avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de l'envoi de l'email: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CREATE : Ajouter une nouvelle activité
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        Activity createdActivity = activityService.createActivity(activity);
        return new ResponseEntity<>(createdActivity, HttpStatus.OK); // ou HttpStatus.CREATED
    }

    // READ : Récupérer toutes les activités
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return new ResponseEntity<>(activities, HttpStatus.OK); // 200
    }

    // READ : Récupérer une activité par ID
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        try {
            Activity activity = activityService.getActivityById(id);
            return new ResponseEntity<>(activity, HttpStatus.OK); // 200
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404
        }
    }

    // READ : Récupérer une activité par titre
    @GetMapping("/title/{title}")
    public ResponseEntity<Activity> getActivityByTitle(@PathVariable String title) {
        try {
            Activity activity = activityService.getActivityByTitle(title);
            return new ResponseEntity<>(activity, HttpStatus.OK); // 200
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404
        }
    }

    // UPDATE : Mettre à jour une activité existante
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        try {
            Activity updatedActivity = activityService.updateActivity(id, activity);
            return new ResponseEntity<>(updatedActivity, HttpStatus.OK); // 200
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // 409 si titre déjà utilisé
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404
        }
    }

    // DELETE : Supprimer une activité
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return new ResponseEntity<>("Activité supprimée avec succès", HttpStatus.OK);
    }
    // Générer PDF avec toutes les activités
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportToPDF() throws Exception {
        List<Activity> activities = activityService.getAllActivities();

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Liste des Activités", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // espace

        // Tableau
        PdfPTable table = new PdfPTable(4); // 4 colonnes
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 3f, 2f});

        // Entêtes
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        table.addCell(new PdfPCell(new Phrase("ID", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Titre", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Description", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Catégorie", headerFont)));

        // Données
        for (Activity activity : activities) {
            table.addCell(String.valueOf(activity.getId()));
            table.addCell(activity.getTitle());
            table.addCell(activity.getDescription());
            table.addCell(activity.getCategory().toString());
        }

        document.add(table);
        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "activities.pdf");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }

    // Générer fichier Excel avec toutes les activités
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel() throws Exception {
        List<Activity> activities = activityService.getAllActivities();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Activities");

        // Ligne d'entête
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Titre");
        header.createCell(2).setCellValue("Description");
        header.createCell(3).setCellValue("Catégorie");
        header.createCell(4).setCellValue("Date de création");

        // Données
        int rowNum = 1;
        for (Activity activity : activities) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(activity.getId());
            row.createCell(1).setCellValue(activity.getTitle());
            row.createCell(2).setCellValue(activity.getDescription());
            row.createCell(3).setCellValue(activity.getCategory().toString());
            row.createCell(4).setCellValue(activity.getCreatedAt().toString());
        }

        // Ajuster la taille des colonnes
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "activities.xlsx");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }

    // Générer QR Code pour une activité spécifique
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long id) throws Exception {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Contenu du QR Code (par exemple, une URL ou les détails de l'activité)
        String qrContent = "Activity: " + activity.getTitle() +
                "\nDescription: " + activity.getDescription() +
                "\nCategory: " + activity.getCategory();

        int width = 300;
        int height = 300;

        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("attachment", "qrcode_activity_" + id + ".png");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }
    // Récupérer toutes les activités avec option de tri
    @GetMapping("/sorted")
    public ResponseEntity<List<Activity>> getAllActivitiesSorted(
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        List<Activity> activities = activityService.getAllActivities();

        // Tri selon le paramètre
        Comparator<Activity> comparator;
        switch (sortBy.toLowerCase()) {
            case "id":
                comparator = Comparator.comparing(Activity::getId);
                break;
            case "description":
                comparator = Comparator.comparing(Activity::getDescription);
                break;
            case "category":
                comparator = Comparator.comparing(Activity::getCategory);
                break;
            case "createdat":
                comparator = Comparator.comparing(Activity::getCreatedAt);
                break;
            case "title":
            default:
                comparator = Comparator.comparing(Activity::getTitle);
                break;
        }

        // Appliquer l'ordre (ascendant ou descendant)
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        List<Activity> sortedActivities = activities.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        return new ResponseEntity<>(sortedActivities, HttpStatus.OK);
    }

    // Imprimer une activité spécifique (génération PDF d'une seule activité)
    @GetMapping("/{id}/print")
    public ResponseEntity<byte[]> printActivity(@PathVariable Long id) throws Exception {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Détails de l'Activité", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // espace

        // Détails
        Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        document.add(new Paragraph("ID: " + activity.getId(), detailFont));
        document.add(new Paragraph("Titre: " + activity.getTitle(), detailFont));
        document.add(new Paragraph("Description: " + activity.getDescription(), detailFont));
        document.add(new Paragraph("Catégorie: " + activity.getCategory(), detailFont));
        document.add(new Paragraph("Date de création: " + activity.getCreatedAt(), detailFont));

        // Optionnel : ajouter le QR Code dans le PDF
        String qrContent = "Activity: " + activity.getTitle() +
                "\nDescription: " + activity.getDescription() +
                "\nCategory: " + activity.getCategory();

        int width = 150;
        int height = 150;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrOut);

        Image qrImage = Image.getInstance(qrOut.toByteArray());
        qrImage.setAlignment(Element.ALIGN_CENTER);
        document.add(new Paragraph(" ")); // espace
        document.add(qrImage);

        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "activity_" + id + ".pdf");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }
}