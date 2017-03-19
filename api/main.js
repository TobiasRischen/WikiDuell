const express = require('express');
const expressStatusMonitor = require('express-status-monitor');
const bodyParser = require('body-parser');
const expressValidator = require('express-validator');
const connection = require('./database');
const session = require('express-session');
const helper = require('./helper');
const validatorParams = helper.validatorParams;


const PORT = 3000;

const app = express();

/**
 * Middlewares
 */
app.set('views', './views');
app.set('view engine', 'hbs');

app.use(express.static('public'));
app.use(expressStatusMonitor());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(expressValidator());
app.use(session({
  resave: true,
  saveUninitialized: true,
  secret: 'SESSION_SECRET',
}));

/**
 * Routes
 */
const createUserController = require('./controller/create_user');
app.get('/create_user', (req, res, next) => validatorParams(req, res, next, createUserController.schema) , createUserController.func);
const createGameController = require('./controller/create_game');
app.get('/create_game', (req, res, next) => validatorParams(req, res, next, createGameController.schema) , createGameController.func);
const gameResultController = require('./controller/game_result')
app.get('/game_result', (req, res, next) => validatorParams(req, res, next, gameResultController.schema) , gameResultController.func);
app.get('/random_scenario', require('./controller/random_scenario'));
const joinGameController = require('./controller/join_game')
app.get('/join_game', (req, res, next) => validatorParams(req, res, next, joinGameController.schema) , joinGameController.func);
const endGameController = require('./controller/end_game')
app.get('/end_game', (req, res, next) => validatorParams(req, res, next, endGameController.schema) , endGameController.func);
const startGameController = require('./controller/start_game')
app.get('/start_game', (req, res, next) => validatorParams(req, res, next, startGameController.schema) , startGameController.func);
const gameStatusController = require('./controller/game_status')
app.get('/game_status', (req, res, next) => validatorParams(req, res, next, gameStatusController.schema) , gameStatusController.func);
const pingController = require('./controller/ping')
app.get('/ping', (req, res, next) => validatorParams(req, res, next, pingController.schema) , pingController.func);

const wikiProxyController = require('./controller/wiki_proxy');
app.get('/wiki/:wikiPath', wikiProxyController);

const webViewController = require('./controller/web_view');
app.get('/web_view', webViewController.index)
app.post('/web_view/login', webViewController.login)


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

