const express = require('express');
const expressStatusMonitor = require('express-status-monitor');
const bodyParser = require('body-parser');
const expressValidator = require('express-validator');
const mysql = require('mysql');
const uuidV4 = require('uuid/v4');
const async = require('async');
const _ = require('lodash');

const credentials = require('./credentials.json');
let connection = mysql.createConnection({
  host: credentials.host,
  user: credentials.user,
  password: credentials.password,
  database: credentials.database
});
const PORT = 3000;

const app = express();


app.use(express.static('public'));
app.use(expressStatusMonitor());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(expressValidator());

const responeFunc = function (res, err, result) {
  if (err) {
    res.status(err.status);
    return res.json(err.data);
  }
  res.status(200);
  return res.json(result);
}


/*
post /create_user_id
=> {}
<= { userid }
*/
app.post('/create_user', (req, res) => {
  let name = req.body.name;
  async.waterfall([
    // create user
    function (callback) {
      connection.query('INSERT INTO user SET ?', {
        uuid: uuidV4(),
        name: name
      }, function (err, result) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, result.insertId);
      });
    },
    //select new record
    function (userId, callback) {
      connection.query('SELECT * FROM user WHERE ?', {
        id: userId
      }, function (err, results) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, { userid: _.head(results).uuid });
      });
    },
  ], (err, result) => responeFunc(res, err, result));
});


/*
post /creategame
=> { userid }
<= { gameid, start, end }*/
app.post('/create_game', (req, res) => {
  //TODO: Check for user id
  let userId = req.body.userId;
  async.waterfall([
    // check if user_id (uuid) exists
    function (callback) {
      connection.query('SELECT * FROM user WHERE ?', {
        uuid: userId
      }, function (err, results) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        if (!results.length) {
          return callback({ status: 404, data: { code: 404, text: 'unkown user id' } });
        }
        return callback(null, _.head(results))
      })
    },
    // select a start and end
    function (user, callback) {
      connection.query('SELECT * FROM scenario ORDER BY RAND() LIMIT 1',
        function (err, results) {
          if (err) {
            console.error(err);
            return callback({ status: 500, data: { code: 500, text: 'database error' } });
          }
          return callback(null, user, _.head(results))
        });
    },
    // insert game with start end
    function (user, scenario, callback) {
      connection.query('INSERT INTO game SET ?', {
        uuid: uuidV4(),
        scenario_id: scenario.id
      }, function (err, result) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, user, scenario, result.insertId);
      });
    },
    // check if user_id (uuid) exists
    function (user, scenario, gameId, callback) {
      connection.query('SELECT * FROM game WHERE ?', {
        id: gameId
      }, function (err, results) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        if (!results.length) {
          return callback({ status: 404, data: { code: 404, text: 'unkown user id' } });
        }
        return callback(null, user, scenario, _.head(results))
      })
    },
    // insert entry in game user mapping
    function (user, scenario, game, callback) {
      connection.query('INSERT INTO user_in_game SET ?', {
        user_id: user.id,
        game_id: game.id,
      }, function (err, result) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, { gameId: game.uuid, start: scenario.start, end: scenario.end });
      });
    }
  ], (err, result) => responeFunc(res, err, result));
});
/*
post /game_result
=> {userid, gameid, time, clicks}
<= {}
*/
app.post('/game_result', (req, res) => {
  // TODO: validate
  // TODO: check if click and time is empty
  let userId = req.body.userId;
  let gameId = req.body.gameId;
  let time = req.body.time;
  let clicks = req.body.clicks;
  async.waterfall([
    // check if user_id (uuid) exists
    function (callback) {
      connection.query('SELECT * FROM user WHERE ?', {
        uuid: userId
      }, function (err, results) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        if (!results.length) {
          return callback({ status: 404, data: { code: 404, text: 'unkown user id' } });
        }
        return callback(null, _.head(results))
      })
    },
    // check if game_id (uuid) exists
    function (user, callback) {
      connection.query('SELECT * FROM game WHERE ?', {
        uuid: gameId
      }, function (err, results) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        if (!results.length) {
          return callback({ status: 404, data: { code: 404, text: 'unkown game id' } });
        }
        return callback(null, user, _.head(results))
      })
    },
    // update result
    function (user, game, callback) {
      connection.query({
        sql: 'UPDATE `user_in_game` SET time = ?, clicks = ? WHERE `user_id` = ? and `game_id` = ?',
        values: [time, clicks, user.id, game.id]
      }, function (err, results) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null);
      });
    },
  ], (err, result) => responeFunc(res, err, result));
});

/*
get /scores
<= { start, end, scores: [{userid, clicks, time
 */
app.get('/scores', (req, res) => {
  res.json({});
});

/*
get /random_scenario
 */
app.get('/random_scenario', (req, res) => {
  connection.query('SELECT * FROM scenario ORDER BY RAND() LIMIT 1',
    function (err, results) {
      if (err) {
        console.error(err);
        res.status(500);
        return res.json({ status: 500, data: { code: 500, text: 'database error' } });
      }
      res.status(200);
      let result = _.head(results);
      return res.json({ start: result.start, end: result.end, complexity: result.complexity });
    });
});

// TODO APIS:
// start game
// join game
// leave game


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

