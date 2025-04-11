export interface Reclamation {
    _id?: string; // L’ID est optionnel lors de la création, mais présent lors de la récupération
    subject: string;
    description: string;
    status?: string; // Optionnel, car il peut être défini par défaut dans le backend
    createdAt?: string; // Optionnel, géré par le backend
    updatedAt?: string; // Optionnel, géré par le backend
  }