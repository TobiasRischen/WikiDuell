const _ = require('lodash');
const async = require('async');

const connection = require('../database');

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
  let shortId = req.body.shortId;
  // get game by gameId
  // check if userid is in game
  // get all players to game
};