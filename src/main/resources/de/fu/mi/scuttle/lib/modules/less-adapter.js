function doCompileLess(lessString) {
  var parser = new(less.Parser);
  var cssString = '';
  parser.parse(lessString, function(err, tree) {
    cssString = tree.toCSS();
  });
  return cssString;
}