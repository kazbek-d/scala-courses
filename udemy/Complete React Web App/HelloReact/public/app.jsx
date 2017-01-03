var React = require('react');
var ReactDOM = require('react-dom');
var Greeter = require('Greeter');


var firstName = "Kazbek";
ReactDOM.render(
  <Greeter name={firstName} message="Message from props"/>,
  document.getElementById('app')
);
