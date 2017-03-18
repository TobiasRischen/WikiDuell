const mysql = require('mysql');
const async = require('async');
const _ = require('lodash');
const credentials = require('../credentials.json');

let connection = mysql.createConnection({
  host: credentials.host,
  user: credentials.user,
  password: credentials.password,
  database: credentials.database
});

const QUERIES = [
  `DROP TABLE IF EXISTS user`,
  `CREATE TABLE
    user(
      id            INT AUTO_INCREMENT PRIMARY KEY,
      uuid          TEXT NOT NULL,
      name          TEXT NOT NULL,
      created       DATETIME NOT NULL DEFAULT now(),
      last_updated  DATETIME NOT NULL DEFAULT now());`,

  `DROP TABLE IF EXISTS game`,
  `CREATE TABLE
    game(
      id            INT AUTO_INCREMENT PRIMARY KEY,
      uuid          TEXT NOT NULL,
      short_id      TEXT NOT NULL,
      scenario_id   INT NOT NULL,
      started       BOOL DEFAULT 0,
      ended         BOOL DEFAULT 0,
      created       DATETIME NOT NULL DEFAULT now(),
      last_updated  DATETIME NOT NULL DEFAULT now());`,

  `DROP TABLE IF EXISTS user_in_game`,
  `CREATE TABLE
    user_in_game(
      user_id       INT NOT NULL,
      game_id       INT NOT NULL,
      time          INT,
      clicks        INT,
      is_creator    BOOL DEFAULT 0,
      last_ping     DATETIME NOT NULL DEFAULT now(),
      created       DATETIME NOT NULL DEFAULT now(),
      last_updated  DATETIME NOT NULL DEFAULT now());`,

  `DROP TABLE IF EXISTS scenario`,
  `CREATE TABLE
    scenario(
      id            INT AUTO_INCREMENT PRIMARY KEY,
      start         TEXT NOT NULL,
      end           TEXT NOT NULL,
      complexity    INT NOT NULL,
      created       DATETIME NOT NULL DEFAULT now(),
      last_updated  DATETIME NOT NULL DEFAULT now());`,
];

let queryFuncs = [];

_.each(QUERIES, item => {
  queryFuncs.push(function (callback) {
    connection.query(item, function (err, rows, fields) {
      if (err) {
        return callback(err, null);
      }
      return callback(null, `OK: ${item}`);
    })
  });
});

connection.connect();

async.series(queryFuncs, function (err, results) {
  connection.end();
  if (err) {
    return console.error(err);
  }
  console.log('all ok');
  _.each(results, console.log);
});
