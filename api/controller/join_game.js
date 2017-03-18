const _ = require('lodash');
const async = require('async');

const connection = require('../database');

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
  let userId = req.body.userId;
  let shortId = req.body.shortId;
  // get game by shortId and not ended and not started
  // get user by userid 
  // insert user_in_game with user and game
};