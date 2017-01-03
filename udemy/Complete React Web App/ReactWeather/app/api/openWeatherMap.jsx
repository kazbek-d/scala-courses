var axios = require('axios');

const OPEN_WEATHER_MAP_URL = 'http://api.openweathermap.org/data/2.5/weather?appid=51fe02246fe5c621b20368b78a09715b&units=metric';

module.exports = {
  getTemp: function(location) {
    var encodedLocation = encodeURIComponent(location);
    var requestUrl = `${OPEN_WEATHER_MAP_URL}&q=${encodedLocation}`;
    return axios.get(requestUrl).then(function(res){
        //debugger;
        if(res.data.cod && res.data.message){
          throw new Error(err.data.message);
        } else {
          return res.data.main.temp;
        }
      }, function(err){
        throw new Error(err.data.message);
      });
  }
}
