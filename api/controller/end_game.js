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
post /end_game
=> { userId, gameId }
<= */
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
          if (!_.head(results).is_creator) {
            return callback({ status: 409, data: { code: 409, text: 'is not creator' } });
          }
          return callback(null, user, game);
        });
    },
    // update game started true update game ended true
    function (user, game, callback) {
      connection.query('UPDATE game set started = true, ended = true WHERE id = ?',
        [game.id], function (err, results) {
          if (err) {
            console.error(err);
            return callback({ status: 500, data: { code: 500, text: 'database error' } });
          }

          return callback(null, {});
        });
    }], (err, result) => helper.responeFunc(res, err, result));
};