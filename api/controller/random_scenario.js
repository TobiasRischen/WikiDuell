const connection = require('../database');
const _ = require('lodash');

/*
get /random_scenario
 */
module.exports = (req, res) => {
  connection.query('SELECT * FROM scenario ORDER BY RAND() LIMIT 1',
    function (err, results) {
      if (err) {
        console.error(err);
        res.status(500);
        return res.json({ status: 500, data: { code: 500, text: 'database error' } });
      }
      res.status(200);
      let result = _.head(results);
      return res.json({ start: result.start, end: result.end, complexity: result.complexity });
    });
};