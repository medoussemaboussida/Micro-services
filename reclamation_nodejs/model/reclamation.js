const mongoose = require("mongoose");

const reclamationSchema = new mongoose.Schema({
    subject: { 
        type: String, 
        required: true 
    },
    description: { 
        type: String, 
        required: true 
    },
    status: { 
        type: String, 
        enum: ["en cours", "résolu", "refusé"], 
        default: "en cours" 
    }
}, {
    timestamps: true 
});

// Middleware post-save
reclamationSchema.post("save", async function (doc, next) {
    console.log("Réclamation créée avec succès");
    next();
});

const Reclamation = mongoose.model("Reclamation", reclamationSchema);

module.exports = Reclamation;