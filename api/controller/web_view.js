const _ = require('lodash');
const request = require('request');

module.exports.index = (req, res) => {
  // if no userid in session create one
  // console.log(req.session);
  // req.session.test = 1337;
  // res.render('index', { title: 'Hey', message: 'Hello there!' });
  if (_.has(req.session, 'userId')) {
    res.render('index', { name: req.session.name });
  } else {
    res.render('login');
  }


}

module.exports.login = (req, res) => {
  // console.log('moep',req.body, req.params, req.query);
  // res.redirect('/foo/bar');
  request(`http://localhost:3000/create_user?name=${req.body.name}`, function (err, response, body) {
    if (err) throw err;
    req.session.userId = JSON.parse(body).userid;
    req.session.name = req.body.name;
    res.redirect('/web_view');
  });
}