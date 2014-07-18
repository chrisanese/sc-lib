(function(Scuttle) {
    
  var __modName__ = "{{modName}}";
  var __mod__ = {};
  
  function define(name, func) {
    __mod__[name] = func;
  }
  
  with (Scuttle) {
    (function() {
      "use strict";
      {{&source}}
    })();
  }

  Scuttle._modules[__modName__] = __mod__;
})(Scuttle);
