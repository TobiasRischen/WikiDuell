const express = require('express');
const expressStatusMonitor = require('express-status-monitor');
const bodyParser = require('body-parser');
const expressValidator = require('express-validator');
const connection = require('./database');
const validatorFunc = require('./helper').validatorFunc

const PORT = 3000;

const app = express();

/**
 * Middlewares
 */
app.use(express.static('public'));
app.use(expressStatusMonitor());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(expressValidator());

/**
 * Routes
 */
const createUserController = require('./controller/create_user');
app.post('/create_user', (req, res, next) => validatorFunc(req, res, next, createUserController.schema) , createUserController.func);
const createGameController = require('./controller/create_game');
app.post('/create_game', (req, res, next) => validatorFunc(req, res, next, createGameController.schema) , createGameController.func);
const gameResultController = require('./controller/game_result')
app.post('/game_result', (req, res, next) => validatorFunc(req, res, next, gameResultController.schema) , gameResultController.func);
app.get('/random_scenario', require('./controller/random_scenario'));


// TODO APIS:
// start game
// join game
// leave game
// scores


connection.connect(err => {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }
  app.listen(PORT, () => {
    console.log('Hack-App started!');
    console.log(`http://localhost:${PORT}`);
    console.log(`Status Monitor @ http://localhost:${PORT}/status`);
  });
});

