/*
var formats = Document.fileFormats;
for (var i in formats) {
    print(formats[i]);
}
*/

// print(documents[0].write(new File("/Users/lehni/test.ai"), null, false));
// var doc = documents[0];
// print(doc.cropBox);
// var doc = new Document("test", 100, 100, Color.CMYK, Document.DIALOG_ON);
// var doc = new Document(new File("/Users/lehni/test.ai"), 2, 1);

/*
var obj = ArtSet.getSelected()[0];
layers[0].color = null; // obj.style.fill.color; // obj.style.fill.color.convert(Color.CONVERSION_RGB);
print(layers[0].color);
obj.style.fill.color = layers[0].color;
*/

var effect = new LiveEffect("Test One", "Test Eins Zwei", LiveEffect.INPUT_PATH, LiveEffect.TYPE_PRE_EFFECT, LiveEffect.FLAG_NONE, 1, 0);
effect.addMenuItem("Hello", new Date() + "");

effect.onEditParameters = function(map) {
    print(map);
    this.updateParameters(map);
}

function walkKids(obj, indent) {
    var str = "";
    for (var i = 0; i < indent; i++)
        str += " ";
    str += obj.name;
    str += " " + obj.selected;
    /*
    if (obj instanceof Path)
        str += obj.segments;
    */
    print(str);
    var children = obj.children;
    for (var i in children) {
        walkKids(children[i], indent + 2);
    }
}

effect.onExecute = function(map, art) {
    // var path = Path.createRectangle(art.bounds);
    var m = Matrix.getRotateInstance(Math.PI / 2);
    art.transform(m);
    walkKids(art, 0);
    return art;
}