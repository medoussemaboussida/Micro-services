// routes/reclamationRoutes.js
const express = require('express');
const router = express.Router();
const reclamationController = require('../controller/reclamation');

router.post('/add', reclamationController.addReclamation);        // Ajouter
router.delete('/deleteReclamatiion/:id', reclamationController.deleteReclamation); // Supprimer
router.get('/getall', reclamationController.getAllReclamations);     // Afficher toutes
router.put('/update/:id', reclamationController.updateReclamation);   // Modifier

module.exports = router;