var React = require('react');
var {Link, IndexLink} = require('react-router');

var Nav = React.createClass({
  render: function(){
    return (
      <div className="top-bar">
        <div className="top-bar-left">
          <ui className="menu">
            <li className="menu-text">React Timer App</li>
            <li>
              <IndexLink to="/" activeClassName="active-link">Timer</IndexLink>
            </li>
            <li>
              <Link to="/countdown" activeClassName="active-link">Countdown</Link>
            </li>
          </ui>
        </div>
        <div className="top-bar-right">
          <ui className="menu">
            <li className="menu-text">Created by <a href="https://github.com/kazbek-d/scala-courses/tree/master/udemy/Complete%20React%20Web%20App" target="_blank">Kazbek Dzarasov</a>
            </li>
          </ui>
        </div>
      </div>
    );
  }
});

module.exports = Nav;
