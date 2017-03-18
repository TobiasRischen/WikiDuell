'use strict';
const mysql = require('mysql');
const credentials = require('./credentials.json');

let connection = mysql.createConnection({
  host: credentials.host,
  user: credentials.user,
  password: credentials.password,
  database: credentials.database
});

module.exports = connection;