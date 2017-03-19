const _ = require('lodash');
const request = require('request');

module.exports.index = (req, res) => {
  if (_.has(req.session, 'userId')) {
    res.render('index', { name: req.session.name });
  } else {
    res.render('login');
  }
}

module.exports.login = (req, res) => {
  request(`http://localhost:3000/create_user?name=${req.body.name}`, function (err, response, body) {
    if (err) throw err;
    req.session.userId = JSON.parse(body).userid;
    req.session.name = req.body.name;
    res.redirect('/web_view');
  });
}

module.exports.start_single_player = (req, res) => {
  request(`http://localhost:3000/random_scenario`, function (err, response, body) {
    if (err) throw err;
    req.session.start = JSON.parse(body).start;
    req.session.end = JSON.parse(body).end;
    req.session.clicks = -1;
    res.redirect(`/wiki/${_.last(req.session.start.split('/'))}`);
  });
}