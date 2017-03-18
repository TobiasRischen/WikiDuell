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


/*
post /create_user_id
=> {}
<= { userid }
post /creategame
=> { userid }
<= { gameid, start, end }
post /game_result
=> {userid, gameid, time, clicks}
<= {}
get /scores
<= { start, end, scores: [{userid, clicks, time
 */

app.post('/create_user', (req, res) => {
  async.waterfall([
    function (callback) {
      connection.query('INSERT INTO user SET ?', {
        uuid: uuidV4()
      }, function (err, result) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, result.insertId);
      });
    },
    function (userId, callback) {
      connection.query('SELECT * from user where ?', {
        id: userId
      }, function (err, results) {
        if (err) {
          console.error(err);
          return callback({ status: 500, data: { code: 500, text: 'database error' } });
        }
        return callback(null, { userid: _.head(results).uuid });
      });
    },
  ],
    function (err, result) {
      if (err) {
        res.status(err.status);
        return res.json(err.data);
      }
      res.status(200);
      return res.json(result);
    });
});

app.post('/creategame', (req, res) => {
  res.json({});
});

app.post('/game_result', (req, res) => {
  res.json({});
});

app.get('/scores', (req, res) => {
  res.json({});
});

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

