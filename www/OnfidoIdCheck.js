var exec = require('cordova/exec');

module.exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'OnfidoIdCheck', 'coolMethod', [arg0]);
};

module.exports.add = function (arg0, success, error) {
    exec(success, error, 'OnfidoIdCheck', 'add', [arg0]);
};

module.exports.multiply = function (arg0, success, error) {
    exec(success, error, 'OnfidoIdCheck', 'multiply', [arg0]);
};

module.exports.substract = function (arg0, success, error) {
    exec(success, error, 'OnfidoIdCheck', 'substract', [arg0]);
};

module.exports.divide = function (arg0, success, error) {
    exec(success, error, 'OnfidoIdCheck', 'divide', [arg0]);
};