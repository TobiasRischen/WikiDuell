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
const joinGameController = require('./controller/join_game')
app.post('/join_game', (req, res, next) => validatorFunc(req, res, next, joinGameController.schema) , joinGameController.func);
const endGameController = require('./controller/end_game')
app.post('/end_game', (req, res, next) => validatorFunc(req, res, next, endGameController.schema) , endGameController.func);
const startGameController = require('./controller/start_game')
app.post('/start_game', (req, res, next) => validatorFunc(req, res, next, startGameController.schema) , startGameController.func);
const gameStatusController = require('./controller/game_status')
app.post('/game_status', (req, res, next) => validatorFunc(req, res, next, gameStatusController.schema) , gameStatusController.func);
const pingController = require('./controller/ping')
app.post('/ping', (req, res, next) => validatorFunc(req, res, next, pingController.schema) , pingController.func);
// TODO APIS:
// start game
// join game
// leave game
// scores
/**
 * Multiplayer
Player 1 create multiplayer game => post /create_game => gameid,start,end, gameshortcode
Player 2 join multiplayer with short code => /join_game {short code} => game_id
Player 1 sees player 2 in lobby => post /game_status {game_id, user_id} => gameid, start,end, started, ended, players[names, time, clicks, last_ping]
Player 1 starts game => post /start_game {game_id, user_id}
Player 2 starts game => (because started is now true) post /game_status {game_id, user_id} => gameid, start,end, started, ended, players[names, time, clicks, last_ping]
Player 1 ends game => post /game_result {game_id, user_id, clicks, time}
Player 1 sees that player 2 is not finished => post /game_status {game_id, user_id} => gameid, start,end, started, ended, players[names, time, clicks, last_ping]
Player 2 ends game => post /game_result {game_id, user_id, clicks, time}
all players are finished => ended
all players every x seconds => post /ping {game_id, user_id}
/force_end_game
 */


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

