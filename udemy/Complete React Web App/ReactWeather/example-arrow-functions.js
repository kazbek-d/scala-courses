var names = ['Andrew', 'Chris', 'Jean'];

names.forEach( function(name) {
  console.log('forEach', name);
});

names.forEach((name)=>console.log('arrowFunc', name));

var returnMe = name => name + "!";
console.log(returnMe('Kazbek'));

var Person = {
  name: 'Kazbek',
  greet: function() {
    names.forEach( function(name) {
      console.log(this.name, 'says hi to ' + name);
    });
  }
}
Person.greet();

var person = {
  name: 'Kazbek',
  greet: function() {
    names.forEach( (name) => console.log(this.name, 'says hi to ' + name) );
  }
}
person.greet();

var add = (a,b) => a+b;
console.log(add(3,1));
console.log(add(9,0));
