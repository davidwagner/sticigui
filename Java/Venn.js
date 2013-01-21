function Venn(container_id) {

  this.env = jQuery('#' + container_id);
  var app = jQuery('<div/>',{id:container_id + 'app',class:'app'});
  this.env.append(app);

  var container = jQuery('<div/>',{id:container_id + 'container',class:'container'});
  var buttons = jQuery('<div/>',{id:container_id + 'buttons',class:'buttons'});
  var scrollbars = jQuery('<div/>',{id:container_id + 'scrollbars',class:'scrollbars'});

  var i = 0;
  var names = ["A", "B", "A or B", "ABc", "S", "Ac", "Bc", "AB", "AcB", "{}"];
  while (i < 10) {
    var button = jQuery('<div/>',{class:'button'});
    buttons.append(button);
    var letter = String.fromCharCode(65 + i);
    var inp = jQuery('<input/>',{type:'radio',id:container_id + letter,name:'buttons'});
    var label = jQuery('<label/>',{for:letter});
    label.html(names[i]);
    button.append(inp);
    button.append(label);
    i++;
  }
  var j = 1;
  while (j <= 2) {

    var sb = jQuery('<div/>',{class:'scrollbar',id:container_id + 'psb' + j.toString()});

    var lbl = jQuery('<label/>',{for:container_id + 'sb'+j.toString()});
    lbl.html('P(' + String.fromCharCode(64 + j)  + ') (%)');
    var idFunc1 = "$('#" + container_id + "sb" + j.toString() + "').val(this.value)";
    var idFunc2 = "$('#" + container_id + "sb" + j.toString() + "t').val(this.value)";
    var input = jQuery('<input/>',{type:'text',id:container_id + 'sb'+j.toString()+'t',onkeyup:idFunc1,value:30,size:2});
    var input2 = jQuery('<input/>',{type:'range',id:container_id + 'sb'+j.toString(),onchange:idFunc2,min:1,max:100,step:1,value:30,style:'width: 92px'});

    sb.append(lbl);
    sb.append(input);
    sb.append(input2);

    scrollbars.append(sb);
    j++;
  }
  app.append(container);
  app.append(buttons);
  app.append(scrollbars);

  var scaleFactorX = 0.3;
  var scaleFactorY = 0.3;

  function draw() {

    var stage = new Kinetic.Stage({
      container: container_id + 'container',
        width: 340,
        height: 200
    });

    var messageLayer = new Kinetic.Layer();
    var layer = new Kinetic.Layer();
    var rectX = stage.getWidth() / 2 - 50;
    var rectY = stage.getHeight() / 2 - 25;

    // Groups for text label & each box.
    var group1 = new Kinetic.Group({
      draggable: true,
    });

    var group2 = new Kinetic.Group({
      draggable:true,
    });

    var box0 = new Kinetic.Rect({
      x: 10,
        y: 10,
        width: 320,
        height: 180,
        fill: '#eee',
        stroke: 'black',
        strokeWidth: 1,
        draggable: false
    });

    var textBox0 = new Kinetic.Text({
      x: 14,
        y: 14,
        text: 'S',
        fontSize: 14,
        fontFamily: 'sans-serif',
        fill: 'black',
    });

    var box1 = new Kinetic.Rect({
      x: rectX - 32,
        y: rectY - 16,
        width: 320 * scaleFactorX,
        height: 180 * scaleFactorY,
        fill: 'none',
        stroke: 'black',
        strokeWidth: 1,
    });

    var textBox1 = new Kinetic.Text({
      x: rectX - 28,
        y: rectY - 14,
        text: 'A',
        fontSize: 14,
        fontFamily: 'sans-serif',
        fill: 'black',
    });

    var box2 = new Kinetic.Rect({
      x: rectX + 32,
        y: rectY + 16,
        width: 320 * scaleFactorX,
        height: 180 * scaleFactorY,
        fill: 'none',
        stroke: 'black',
        strokeWidth: 1,
    });

    var textBox2 = new Kinetic.Text({
      x: rectX + 34,
        y: rectY + 18,
        height: 20,
        text: 'B',
        fontSize: 14,
        fontFamily: 'sans-serif',
        fill: 'black',
    });

    var fillArea = new Kinetic.Rect({
      x: rectX + 32,
        y: rectY + 16,
        width: (320*scaleFactorX) - 64,
        height: (180*scaleFactorY) - 32,
        fill: 'none',
        stroke: 'black',
        strokeWidth: 0.5,
    });

    // fillArea.on('mousedown', function() {
    //   console.log('hi');
    // });

    //console.log(stage.getIntersection(box1));


    // S
    $('#' + container_id + 'E').click(function () {
      box0.setFill('blue');
      box1.setFill('none');
      box2.setFill('none');
      fillArea.setFill('blue');
      fillArea.setStroke('black');
      layer.draw()
    });

    // A
    $('#' + container_id + 'A').click(function () {
      box0.setFill('#eee');
      box1.setFill('blue');
      box2.setFill('none');
      fillArea.setFill('blue');
      layer.draw()
    });

    // B
    $('#' + container_id + 'B').click(function () {
      box1.setFill('none');
      box0.setFill('#eee');
      fillArea.setFill('blue');
      box2.setFill('blue');
      layer.draw()
    });

    // Ac
    $('#' + container_id + 'F').click(function () {
      box2.setFill('none');
      box1.setFill('#eee');
      box0.setFill('blue');
      fillArea.setFill('#eee');
      layer.draw()
    });

    // Bc
    $('#' + container_id + 'G').click(function () {
      box1.setFill('none');
      box2.setFill('#eee');
      box0.setFill('blue');
      fillArea.setFill('#eee');
      layer.draw()
    });

    // A or B -- NOT QUITE RIGHT need to group
    $('#' + container_id + 'C').click(function () {
      box0.setFill('#eee');
      box1.setFill('blue');
      box2.setFill('blue');
      fillArea.setFill('blue');
      layer.draw()
    });

    // {}
    $('#' + container_id + 'J').click(function () {
      box0.setFill('#eee');
      box1.setFill('none');
      box2.setFill('none');
      fillArea.setFill('#eee');
      layer.draw()
    });

    // AB -- FIX
    $('#' + container_id + 'H').click(function () {
      box0.setFill('#eee');
      box1.setFill('none');
      box2.setFill('none');
      fillArea.setFill('blue')
      //fillArea.setPosition()
      layer.draw()
    });

    // ABc
    $('#' + container_id + 'D').click(function () {
      box0.setFill('#eee');
      box1.setFill('blue');
      box2.setFill('#eee');
      fillArea.setFill('#eee');
      // fillArea.setPosition()
      // fillArea.setFill('none');
      layer.draw()
    });

    // AcB
    $('#' + container_id + 'I').click(function () {
      box0.setFill('#eee');
      box2.setFill('blue');
      box1.setFill('#eee');
      fillArea.setFill('#eee');
      layer.draw()
    });

    //box1.simulate('click');
    //box2.simulate('click');

    $('#' + container_id + 'sb1').change(function () {
      box1.setWidth((320*this.value)/100);
      box1.setHeight((180*this.value)/100);
      fillArea.setWidth((320*this.value)/100-64);
      fillArea.setHeight((180*this.value)/100-32);
      layer.draw();
    });

    $('#' + container_id + 'sb2').change(function () {
      box2.setWidth((320*this.value)/100);
      box2.setHeight((180*this.value)/100);
      fillArea.setWidth((320*this.value)/100-64);
      fillArea.setHeight((180*this.value)/100-32);
      layer.draw();
    });

    $('#' + container_id + 'sb1t').change(function () {
      box1.setWidth((320*this.value)/100);
      box1.setHeight((180*this.value)/100);
      fillArea.setWidth((320*this.value)/100-64);
      fillArea.setHeight((180*this.value)/100-32);
      layer.draw();
    });

    $('#' + container_id + 'sb2t').change(function () {
      box2.setWidth((320*this.value)/100);
      box2.setHeight((180*this.value)/100);
      fillArea.setWidth((320*this.value)/100-64);
      fillArea.setHeight((180*this.value)/100-32);
      layer.draw();
    });

    group1.add(box1);
    group1.add(textBox1);

    group2.add(box2);
    group2.add(textBox2);

    layer.add(box0);
    layer.add(textBox0);
    layer.add(group1);
    layer.add(group2);
    //layer.add(fillArea);

    stage.add(layer);
    stage.add(messageLayer);
  }

  draw();
}
