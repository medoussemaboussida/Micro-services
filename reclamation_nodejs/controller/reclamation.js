const Reclamation = require('../model/reclamation');


// Ajouter une nouvelle réclamation
module.exports.addReclamation = async (req, res) => {
    try {
        console.log(req.body);

        // Récupérer les données du corps de la requête
        const { subject, description } = req.body;

        // Créer une nouvelle réclamation
        const reclamation = new Reclamation({ subject, description });

        // Sauvegarder la réclamation dans la base de données
        const reclamationAdded = await reclamation.save();

        // Répondre avec la réclamation ajoutée
        res.status(201).json(reclamationAdded);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

// Supprimer une réclamation
module.exports.deleteReclamation = async (req, res) => {
    try {
        const { id } = req.params;

        // Vérifier si la réclamation existe
        const reclamation = await Reclamation.findByIdAndDelete(id);
        if (!reclamation) {
            return res.status(404).json({ message: "Réclamation non trouvée !" });
        }

        // Répondre avec un message de succès
        res.status(200).json({ message: "Réclamation supprimée avec succès" });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

// Afficher toutes les réclamations
module.exports.getAllReclamations = async (req, res) => {
    try {
        const reclamations = await Reclamation.find();
        res.status(200).json(reclamations);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

// Modifier une réclamation
module.exports.updateReclamation = async (req, res) => {
    try {
        const { id } = req.params;
        const { subject, description, status } = req.body;

        // Vérifier si la réclamation existe
        const reclamation = await Reclamation.findById(id);
        if (!reclamation) {
            return res.status(404).json({ message: "Réclamation non trouvée !" });
        }

        // Mettre à jour les champs fournis
        if (subject) reclamation.subject = subject;
        if (description) reclamation.description = description;
        if (status) reclamation.status = status;

        const updatedReclamation = await reclamation.save();

        // Répondre avec la réclamation mise à jour
        res.status(200).json(updatedReclamation);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};