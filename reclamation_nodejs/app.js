var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
const http = require('http');
const mongoose = require('mongoose');
const { Eureka } = require('eureka-js-client');
const eurekaConfig = require('./eurekaConfig');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var reclamationRouter = require('./routes/reclamation');

var app = express();
const cors = require('cors');
app.use(cors({
  origin: 'http://localhost:4200'
}));
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/reclamation', reclamationRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};
  res.status(err.status || 500);
  res.render('error');
});

// Fonction pour connecter à MongoDB avec retry
const connectWithRetry = async () => {
  console.log('Tentative de connexion à MongoDB...');
  let retries = 5;
  while (retries > 0) {
    try {
      await mongoose.connect(process.env.MONGO_URI, {
        useNewUrlParser: true,
        useUnifiedTopology: true,
        serverSelectionTimeoutMS: 5000, // Timeout de 5 secondes par tentative
      });
      console.log('✅ Database connectée');
      return; // Connexion réussie, sortir de la boucle
    } catch (err) {
      console.error('❌ Erreur de connexion à la database:', err.message);
      retries -= 1;
      if (retries === 0) {
        console.error('❌ Impossible de se connecter à MongoDB après plusieurs tentatives. Arrêt du serveur.');
        process.exit(1);
      }
      console.log(`Nouvelle tentative dans 5 secondes... (${retries} tentatives restantes)`);
      await new Promise(resolve => setTimeout(resolve, 5000)); // Attendre 5 secondes avant de réessayer
    }
  }
};

// Démarrer le serveur uniquement après une connexion réussie à MongoDB
const startServer = async () => {
  await connectWithRetry(); // Attendre que MongoDB soit connecté

  // Configurer le client Eureka
  const client = new Eureka(eurekaConfig);
  client.start((error) => {
    if (error) {
      console.error('❌ Erreur lors de l\'enregistrement auprès d\'Eureka:', error);
    } else {
      console.log('✅ Service enregistré auprès d\'Eureka');
    }
  });

  // ✅ Démarrage du serveur backend sur le bon port
  const PORT = 8500;
  const server = http.createServer(app);

  server.listen(PORT, () => {
    console.log(`🚀 Serveur backend démarré sur http://localhost:${PORT}`);
  });
};

// Lancer le serveur
startServer();

module.exports = app;