 function SVG2PNG(svg, callback) {
   var canvas = document.createElement('canvas');
   var ctx = canvas.getContext('2d');
   var data = svg.outerHTML;
   canvg(canvas, data);
   callback(canvas);
 }

 function generateLink(fileName, data) {
   var link = document.createElement('a');
   link.download = fileName;
   link.href = data;
   return link;
 }

 function generate(file, element) {
   SVG2PNG(element, function(canvas) {
     var base64 = canvas.toDataURL("image/png");
     generateLink(file, base64).click();
   });
 }
