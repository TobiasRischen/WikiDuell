const _ = require('lodash');
const async = require('async');
const uuidV4 = require('uuid/v4');

const connection = require('../database');
const helper = require('../helper');

module.exports.schema = {
  'userId': {
    notEmpty: true,
    isUUID: true
  }
}

/*
post /creategame
=> { userid }
<= { gameid, start, end }*/
module.exports.func = (req, res) => {
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
    // TODO: short id should not exist in any running game
    // insert game with start end
    function (user, scenario, callback) {
      connection.query('INSERT INTO game SET ?', {
        uuid: uuidV4(),
        scenario_id: scenario.id,
        short_id: _.sample('ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789', 4).join('')
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
        is_creator: true
      }, function (err, result) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, { gameId: game.uuid, start: scenario.start, end: scenario.end, shortId:game.short_id });
      });
    }
  ], (err, result) => helper.responeFunc(res, err, result));
};