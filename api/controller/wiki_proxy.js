const request = require('request');
const cheerio = require('cheerio');
const _ = require('lodash');


module.exports = (req, res) => {
  if (!_.has(req.session, 'end')) {
    return res.redirect('/web_view');
  }

  let end = _.last(req.session.end.split('/'));
  let wikiPath = req.params.wikiPath;
  if (end === wikiPath) {
    return res.render('success', { clicks: req.session.clicks });
  }
  req.session.clicks += 1;
  request(`https://de.wikipedia.org/wiki/${wikiPath}`, function (error, response, body) {
    $ = cheerio.load(body);
    // res.send();
    return res.render('wiki', { start: req.session.start, end: req.session.end, clicks: req.session.clicks, wiki: $('.mw-body').html() })

  });
};