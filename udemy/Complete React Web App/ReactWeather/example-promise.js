

function addPromise(a,b) {
  return new Promise(function(resolve,reject){
    if(typeof a == 'number' && typeof b === 'number') {
      resolve({code: 'OK', result: a+b});
    } else {
      reject({code:'Error', result: {a:a,b:b}});
    }
  });
}

addPromise(1,5).then(
  function(result){
    console.log(result);
  }, function(err){
    console.log(err);
  });
