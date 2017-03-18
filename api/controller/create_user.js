const _ = require('lodash');
const async = require('async');
const uuidV4 = require('uuid/v4');

const connection = require('../database');
const helper = require('../helper');
/*
post /create_user_id
=> {}
<= { userid }
*/
module.exports.schema = {
  'name': {
    notEmpty: true,
  }
};
module.exports.func = (req, res) => {
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
  ], (err, result) => helper.responeFunc(res, err, result));
};