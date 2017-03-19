const mysql = require('mysql');
const async = require('async');
const _ = require('lodash');
const credentials = require('../credentials.json');
const fs = require('fs');

let connection = mysql.createConnection({
  host: credentials.host,
  user: credentials.user,
  password: credentials.password,
  database: credentials.database
});

const filePath = './scenarios/content2.txt'


connection.connect(err => {
  if (err) {
    console.error('error connecting: ' + err.stack);
    return;
  }

  fs.readFile(filePath, 'utf8', function (err, data) {
    if (err) throw err;
    let insertData = [];
    _.each(data.split('\r\n'), row => {
      if(row.split(' ').length < 2){
        return;
      }
      insertData.push(row.split(' '));
    });

    connection.query('INSERT INTO scenario(start, end, complexity) VALUES ?', [insertData],
      function (err, result) {
        if (err) {
          console.error(err);
        }
        console.log('success: new entries:', result.affectedRows);
        connection.destroy();
      });
  });
});
