const request = require('request');
const cheerio = require('cheerio');


module.exports = (req, res) => {
  let wikiPath = req.params.wikiPath;
  request(`https://de.wikipedia.org/wiki/${wikiPath}`, function (error, response, body) {
    $ = cheerio.load(body);
    res.send($('.mw-body').html());
  });
};