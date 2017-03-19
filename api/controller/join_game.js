const _ = require('lodash');
const async = require('async');

const connection = require('../database');
const helper = require('../helper');

module.exports.schema = {
  'userId': {
    notEmpty: true,
    isUUID: true
  },
  'shortId': {
    notEmpty: true
  }
}

/*
post /join_game
=> { short_id, user_id }
<= { gameid, start, end, short_id }*/
module.exports.func = (req, res) => {
  let userId = req.query.userId;
  let shortId = req.query.shortId;
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
    // get game by shortId and not ended and not started
    function (user, callback) {
      connection.query('SELECT * FROM game WHERE short_id = ? and started = ? and ended = ?',
        [shortId, false, false],
        function (err, results) {
          if (err) {
            console.error(err);
            return callback({ status: 500, data: { code: 500, text: 'database error' } });
          }
          if (!results.length) {
            return callback({ status: 404, data: { code: 404, text: 'unkown short id' } });
          }
          return callback(null, user, _.head(results))
        });
    },
    // TODO: check if user already in game 
    // insert user_in_game with user and game
    function (user, game, callback) {
      connection.query('INSERT INTO user_in_game SET ?', {
        user_id: user.id,
        game_id: game.id,
        is_creator: false
      }, function (err, result) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, { gameId: game.uuid });
      });
    }
  ], (err, result) => helper.responeFunc(res, err, result));


};