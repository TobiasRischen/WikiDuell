const _ = require('lodash');
const async = require('async');

const connection = require('../database');
const helper = require('../helper');

module.exports.schema = {
  'userId': {
    notEmpty: true,
    isUUID: true
  },
  'gameId': {
    notEmpty: true
  }
}

/*
post /game_status
=> { userId, gameId }
<= gameid, start,end, started, ended, players[names, time, clicks, last_ping]*/
module.exports.func = (req, res) => {
  let userId = req.body.userId;
  let gameId = req.body.gameId;
  async.waterfall([
    // get user by userid 
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
      });
    },
    // get game by gameid
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
      });
    },
    // check if userid is in game
    function (user, game, callback) {
      connection.query('SELECT * FROM user_in_game WHERE user_id = ? and game_id = ?',
        [user.id, game.id], function (err, results) {
          if (err) {
            console.error(err);
            return callback({ status: 500, data: { code: 500, text: 'database error' } });
          }
          if (!results.length) {
            return callback({ status: 404, data: { code: 404, text: 'user not in game' } });
          }
          return callback(null, user, game);
        });
    },
    // get all players to game
    function (user, game, callback) {
      connection.query(`
        SELECT 
          user.name, user_in_game.clicks, user_in_game.time, user_in_game.last_ping  
        FROM 
          user_in_game JOIN user ON user.id = user_in_game.user_id 
        WHERE 
          user_in_game.game_id = ?
        `, [game.id], function (err, results) {
          if (err) {
            console.error(err);
            return callback({ status: 500, data: { code: 500, text: 'database error' } });
          }
          if (!results.length) {
            return callback({ status: 404, data: { code: 404, text: 'game not found' } });
          }
          return callback(null, { gameId: game.id, start: game.start, end: game.end, started: game.started, ended: game.ended, players: results });
        });
    },
  ], (err, result) => helper.responeFunc(res, err, result));
};