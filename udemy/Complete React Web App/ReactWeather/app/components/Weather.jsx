var React = require('react');
var WeatherMessage = require('WeatherMessage');
var WeatherForm = require('WeatherForm');
var ErrorModal = require('ErrorModal');
var openWeatherMap = require('openWeatherMap');

var Weather = React.createClass({
  getInitialState: function(){
    return {
      isLoading: false
    }
  },
  handleSearch: function(location){
    //debugger;
    this.setState({
      isLoading: true,
      errorMessage: undefined
    });
    openWeatherMap.getTemp(location).then((temp) => {
      this.setState({
        isLoading: false,
        location: location,
        temp: temp
      });
    }, (e) => {
      //debugger;
      this.setState({
        isLoading: false,
        errorMessage: e.message
      });
    });
  },
  render: function(){
    var {isLoading, temp, location, errorMessage, errorMessage} = this.state;

    function renderMessage(){
      //debugger;
      if(isLoading) {
        return <h3 className="text-center">Fetching weather...</h3>;
      } else if (temp && location) {
        return <WeatherMessage location={location} temp={temp}/>;
      }
    }
    function renderError() {
      //debugger;
      if(typeof errorMessage === 'string') {
        return (
          <ErrorModal message = {errorMessage}/>
        );
      }
    }
    return (
      <div>
        <h1 className="text-center">Get weather</h1>
        <WeatherForm onSearch={this.handleSearch}/>
        {renderMessage()}
        {renderError()}
      </div>
    );
  }
});

module.exports = Weather;
