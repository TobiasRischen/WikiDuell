const util = require('util');

module.exports.responeFunc = function (res, err, result) {
  if (err) {
    res.status(err.status);
    console.log('OUT:', err.data);
    return res.json(err.data);
  }
  res.status(200);
  console.log('OUT:', result);
  return res.json(result);
}

module.exports.validatorBody = function (req, res, next, schema) {
  req.checkBody(schema);
  req.getValidationResult().then(function (result) {
    if (!result.isEmpty()) {
      res.status(400);
      return res.json(result.mapped())
    }
    return next();
  });
}

module.exports.validatorParams = function (req, res, next, schema) {
  req.checkQuery(schema);
  req.getValidationResult().then(function (result) {
    if (!result.isEmpty()) {
      res.status(400);
      return res.json(result.mapped())
    }
    return next();
  });
}

module.exports.logBefore = function (req, res, next) {
  console.log('IN:', req.originalUrl);
  return next();
}