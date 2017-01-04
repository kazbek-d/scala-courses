var React = require('react');
var {Link} = require('react-router');

var Examples = (props) => (
  <div>
    <h1 className="text-center">Examples</h1>
    <p>Here are a few examples to try out:</p>
    <ol>
      <li>
        <Link to="/?location=Moscow">Moscow</Link>
      </li>
      <li>
        <Link to="/?location=Ramenskoye">Ramenskoye</Link>
      </li>
    </ol>
  </div>
);

module.exports = Examples;
