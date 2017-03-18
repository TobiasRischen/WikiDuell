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
post /end_game
=> { userId, gameId }
<= */
module.exports.func = (req, res) => {
  let userId = req.body.userId;
  let gameId = req.body.gameId;
  // get game by gameId
  // get user by userid
  // check if userid is in game and is creator
  // update game started false
  // update game ended true
};