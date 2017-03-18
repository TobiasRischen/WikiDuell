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
    notEmpty: true,
    isUUID: true
  },
  'time': {
    isInt: true
  },
  'clicks': {
    isInt: true
  }
};
/*
post /game_result
=> {userid, gameid, time, clicks}
<= {}
*/
module.exports.func = (req, res) => {
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
    // TODO: check if this is not already set
    function (user, game, callback) {
      connection.query({
        sql: 'UPDATE `user_in_game` SET time = ?, clicks = ? WHERE `user_id` = ? and `game_id` = ?',
        values: [time, clicks, user.id, game.id]
      }, function (err, results) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, {});
      });
    },
  ], (err, result) => helper.responeFunc(res, err, result));
};