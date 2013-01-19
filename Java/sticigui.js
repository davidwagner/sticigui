// Javascript rewrite of
// http://statistics.berkeley.edu/~stark/Java/Html/HistHiLite.htm
//
// Author: James Eady <jeady@berkeley.edu>
//
// container_id: the CSS ID of the container to create the histogram (and
//               controls) in.
// params: A javascript object with various parameters to customize the chart.
//  // Array of URLs (as strings) of json-encoded datasets.
//  - data: null
//
//  // Show normal by default. Normal can still be toggled by the button.
//  - showNormal: false
//
//  // Whether or not to display the 'Show Normal' button.
//  - showNormalButton: true
//
//  // Default number of bins to display.
//  - bins: 10
//
//  // Whether the user can set the number of bins.
//  - changeNumBins: true
//
//  // Whether or not to display the 'Show Univariate Stats' button.
//  - showUnivariateStats: true
//
//  // Whether or not to display the 'List Data' button.
//  - listData: true
//
//  // The following two options can be use to manually configure the histogram
//  // rather than having it loaded from a JSON data file. Note that if either
//  // counts or ends are set, data cannot be set and showNormal,
//  // showNormalButton, showUnivariateStats, changeNumBins, and listData will
//  // all be automatically set to false.
//
//  // Array of bin counts to use instead of parsing data.
//  - counts: null
//
//  // Array of bin ends. If counts is set, ends must also be set.
//  - ends: null

function Stici_HistHiLite(container_id, params) {
  var self = this;

  if (!params instanceof Object) {
    console.error('Stici_HistHiLite params should be an object');
    return;
  }

  // Configuration option defaults.
  this.options = {
    data: null,
    showNormalButton: true,
    showNormal: false,
    bins: 10,
    changeNumBins: true,
    showUnivariateStats: true,
    listData: true,
    counts: null,
    ends: null
  };

  // Override options with anything specified by the user.
  jQuery.extend(this.options, params);

  // jQuery object containing the entire chart.
  this.container = jQuery('#' + container_id);

  // Labels for the data.
  this.dataFields = null;

  // The data itself.
  this.dataValues = null;

  // The URL we got the JSON-encoded data from.
  this.dataSource = null;

  // Currently rendered data.
  this.data = null;

  // Histogram information calculated via stat_utils.js. Cached here.
  this.binEnds = null;
  this.binCounts = null;

  // Various handles to important jQuery objects.
  this.urlInput = null;
  this.dataSelect = null;
  this.variableSelect = null;
  this.chartDiv = null;
  this.overlayDiv = null;  // Used for the area selection feature.
  this.normalOverlayDiv = null;
  this.areaFromInput = null;
  this.areaFromSlider = null;
  this.areaToInput = null;
  this.areaToSlider = null;
  this.binsInput = null;
  this.areaInfoDiv = null;
  this.showNormalButton = null;
  this.showingNormal = false;

  if (!(self.options.data instanceof Array)) {
    this.dataSource = null;
    if (self.options.counts === null || self.options.ends === null) {
      console.error('Unknown data source.');
      return;
    }
    self.data = [self.options.ends.min(), self.options.ends.max()];
    self.options.showNormal = false;
    self.options.showNormalButton = false;
    self.options.showUnivariateStats = false;
    self.options.listData = false;
    self.options.changeNumBins = false;
  } else {
    this.dataSource = this.options.data[0];
  }

  // Reloads chart data from this.data_source
  this.reloadData = function() {
    self.dataFields = [];
    self.dataValues = [];

    if (self.options.data !== null) {
      self.dataSource = self.dataSelect.val();
      jQuery.getJSON(self.dataSource, function(data) {
        self.dataFields = data[0];
        self.dataValues = data.slice(1);
        self.variableSelect.children().remove();
        jQuery.each(self.dataFields, function(i, field) {
          if (field.indexOf('//') === 0)
            return;

          self.variableSelect.append(
            jQuery('<option/>').attr('value', i).text(field)
          );
        });
        self.variableSelect.val(2);  // TODO(jmeady): un-hardcode this.

        self.reloadChart();
      });
    } else {
      self.reloadChart();
    }
  };

  this.reloadChart = function() {
    var i;
    self.chartDiv.children().remove();
    var normalChartDiv = jQuery('<div/>').addClass('chart_box');
    self.overlayDiv = normalChartDiv.clone().addClass('overlay');
    self.normalOverlayDiv = jQuery('<div/>').addClass('chart_box');
    self.chartDiv.append(normalChartDiv);
    self.chartDiv.append(self.overlayDiv);
    self.chartDiv.append(self.normalOverlayDiv);

    // Background calculations about the data.
    var nBins = parseInt(self.binsInput.val(), 10);
    if (self.dataSource !== null) {
      var data = jQuery.map(self.dataValues, function(values) {
        return parseFloat(values[self.variableSelect.val()]);
      });
      self.data = data;
      self.binEnds = histMakeBins(nBins, data);
      self.binCounts = histMakeCounts(self.binEnds, data);
    } else {
      self.binEnds = self.options.ends;
      self.binCounts = self.options.counts;
      var total = self.binCounts.reduce(function(a, b) { return a + b; });
      for (i = 0; i < self.binCounts.length; i++) {
        self.binCounts[i] /= total * (self.binEnds[i + 1] - self.binEnds[i]);
      }
    }
    var width = self.overlayDiv.width();
    var height = self.overlayDiv.height();
    var dataMu = mean(self.data);
    var dataSd = sd(self.data);
    var graphWidth = self.data.max() - self.data.min();

    // Calculate the scaling by taking the max of the histogram bar heights
    // and the y-coordinate of the points on the normal curve.
    var normalCurveY = function(d) {
      var x =
        self.binEnds[0] +
        d * (self.binEnds[nBins] - self.binEnds[0]) / (width - 1);
      var y = normPdf(dataMu, dataSd, x);
      return y;
    };
    var yScale = null;
    // TODO(jmeady): Include height in yScale.
    for (i = 0; i < width; i++) {
      if (yScale === null || normalCurveY(i) > yScale)
        yScale = normalCurveY(i);
    }
    yScale = Math.max(self.binCounts.max(), yScale);
    yScale /= (height - 1);

    // Draw the histogram as an SVG. We draw a second copy to use as the
    // area overlay and use absolute positioning to put them on top of each
    // other.
    function appendSvg(div) {
      return d3.select(div.get(0)).append('svg')
        .selectAll('div')
        .data(self.binCounts)
        .enter()
        .append('rect')
        .attr('y', function(d) { return height - d / yScale; })
        .attr('height', function(d) { return d / yScale; })
        .attr('x', function(d, i) { return (width * (self.binEnds[i] - self.binEnds.min()) / graphWidth); })
        .attr('width', function(d, i) { return width * (self.binEnds[i + 1] - self.binEnds[i]) / graphWidth; });
    }
    appendSvg(normalChartDiv);
    appendSvg(self.overlayDiv);
    self.overlayDiv.css('clip', 'rect(0px, 0px, ' + height + 'px, 0px)');

    // Draw the axis
    var axisSvg = d3.select(normalChartDiv.get(0))
                .append('svg')
                .attr('class', 'axis');
    var axisScale = d3.scale.linear().domain([self.data.min(), self.data.max()]).range([0, width]);
    var axis = d3.svg.axis().scale(axisScale).orient('bottom');
    axisSvg.append('g').call(axis);

    // Draw the normal curve and then hide it. We can show it by toggling
    // its css.
    var normalCurve =
      d3.svg.line()
        .x(function(d) {return d;})
        .y(function(d) {
          return height - (normalCurveY(d) / yScale);
        });
    d3.select(self.normalOverlayDiv.get(0)).append('svg')
      .append('path')
      .data([d3.range(0, width)])
      .attr('d', normalCurve)
      .style('fill', 'none')
      .style('stroke', '#000000');

    self.areaFromSlider.slider('option', 'min', self.data.min());
    self.areaFromSlider.slider('option', 'value', self.data.min());
    self.areaFromSlider.slider('option', 'max', self.data.max());
    self.areaFromInput.val(self.data.min());
    self.areaToSlider.slider('option', 'min', self.data.min());
    self.areaToSlider.slider('option', 'value', self.data.min());
    self.areaToSlider.slider('option', 'max', self.data.max());
    self.areaToInput.val(self.data.min());

    self.showingNormal = self.options.showNormal;
    if (self.options.showNormal) {
      self.showNormalButton.text('Hide Normal Curve');
    } else {
      self.showNormalButton.text('Show Normal Curve');
      self.normalOverlayDiv.hide();
    }
  };

  // Initializes the chart controls. Adds the sliders, input fields, etc.
  function initControls() {
    var o = jQuery('<div/>').addClass('stici');
    self.container.append(o);

    // Top controls
    if (self.dataSource !== null) {
      self.urlInput = jQuery('<input type="text" />');
      self.dataSelect = jQuery('<select/>').change(self.reloadData);
      self.variableSelect = jQuery('<select/>').change(self.reloadChart);

      var top = jQuery('<div/>').addClass('top_controls');
      top.append('Data: ').append(self.dataSelect);
      jQuery.each(self.options.data, function(i, dataUrl) {
        self.dataSelect.append(jQuery('<option/>')
                       .attr('value', dataUrl)
                       .text(dataUrl));
      });
      top.append('Variable: ').append(self.variableSelect);
      o.append(top);
    }

    // Chart
    self.chartDiv = jQuery('<div/>')
                      .addClass('stici_chart')
                      .addClass('chart_box');
    o.append(self.chartDiv);

    // Area info overlay
    self.areaInfoDiv = jQuery('<div/>')
                         .addClass('area_info');
    o.append(self.areaInfoDiv);

    // Bottom controls
    var bottom = jQuery('<div/>').addClass('bottom_controls');
    o.append(bottom);
    var row1 = jQuery('<span/>');
    var row2 = jQuery('<span/>');
    bottom.append(row1).append(row2);
    self.binsInput = jQuery('<input type="text" />')
                       .val(self.options.bins)
                       .change(self.reloadChart);
    if (!self.options.changeNumBins)
      self.binsInput.attr('readonly', '');

    // Helper function that is called whenever any of the area overlay sliders
    // or inputs are changed.
    function refreshSelectedAreaOverlay() {
      var lower = parseFloat(self.areaFromSlider.slider('value'));
      var upper = parseFloat(self.areaToSlider.slider('value'));
      var scale = self.chartDiv.width() / (self.data.max() - self.data.min());
      var left = (lower - self.data.min()) * scale;
      var right = (upper - self.data.min()) * scale;
      self.overlayDiv.css('clip',
                          'rect(0px,' +
                                right + 'px,' +
                                self.chartDiv.height() + 'px,' +
                                left + 'px)');
      var p = histHiLitArea(lower, upper, self.binEnds, self.binCounts) * 100;
      var text = 'Selected area: ' + p.fix(2) + '%';
      if (self.showingNormal) {
        var m = mean(self.data);
        var s = sd(self.data);
        p = Math.max(0, (normCdf((upper - m) / s) - normCdf((lower - m) / s)) * 100);
        text += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
        text += 'Normal approx: ' + p.fix(2) + '%';
      }

      self.areaInfoDiv.html(text);
    }

    // Area from input/slider.
    self.areaFromInput = jQuery('<input type="text" />').change(function() {
      self.areaFromSlider.slider('value', self.areaFromInput.val());
    });
    var updateAreaFromInput = function() {
      self.areaFromInput.val(self.areaFromSlider.slider('value'));
      refreshSelectedAreaOverlay();
    };
    self.areaFromSlider = jQuery('<span/>').addClass('slider').slider({
      change: updateAreaFromInput,
      slide: updateAreaFromInput,
      step: 0.001
    });
    row1.append('Area from: ').append(self.areaFromInput).append(self.areaFromSlider);

    // Area to input/slider.
    self.areaToInput = jQuery('<input type="text" />').change(function() {
      self.areaToSlider.slider('value', self.areaToInput.val());
    });
    var updateAreaToInput = function() {
      self.areaToInput.val(self.areaToSlider.slider('value'));
      refreshSelectedAreaOverlay();
    };
    self.areaToSlider = jQuery('<span/>').addClass('slider').slider({
      change: updateAreaToInput,
      slide: updateAreaToInput,
      step: 0.001
    });
    row1.append(' to: ').append(self.areaToInput).append(self.areaToSlider);

    row1.append('Bins: ').append(self.binsInput);

    // Extra info buttons
    // TODO(jmeady): Move this into a more general file
    function createPopBox() {
      var parent = jQuery('<div/>').addClass('popbox');
      var open = jQuery('<button/>').addClass('open').text('Click Here!');
      var collapse = jQuery('<div/>').addClass('collapse');
      collapse.html(
        '<div class="box">' +
        '  <div class="arrow"></div>' +
        '  <div class="arrow-border"></div>' +
        '  <div class="tinyscrollbar">' +
        '    <div class="scrollbar">' +
        '      <div class="track">' +
        '        <div class="thumb">' +
        '          <div class="end"></div>' +
        '        </div>' +
        '      </div>' +
        '    </div>' +
        '    <div class="viewport">' +
        '      <p class="overview"></p>' +
        '    </div>' +
        '  </div>' +
        '  <a href="#" class="close">close</a>' +
        '</div>');
      parent.append(open).append(collapse);

      var content = collapse.find('p');
      content.text('Content Here :)');

      var inited_scrollbar = false;
      parent.data('onPopBox', function() {
        if (!inited_scrollbar) {
          inited_scrollbar = true;
          parent.tinyscrollbar();
        }
        parent.find('.viewport').parent().width(content.width() + 20);
      });


      return {
        parent: parent,
        button: open,
        content: content
      };
    }
    var lastListDataHeader = null;
    var listDataButton = createPopBox();
    listDataButton.button.text('List Data');
    listDataButton.button.click(function() {
      var rawHeader = '<tr>';
      for (i = 0; i < self.dataFields.length; i++) {
        var field = self.dataFields[i].toString();
        if (field.indexOf('//') === 0)
          rawHeader += '<th>' + field.substr(2) + '</th>';
        else
          rawHeader += '<th>' + field + '</th>';
      }
      rawHeader += '</tr>';
      if (lastListDataHeader !== null)
        lastListDataHeader.remove();
      var header = jQuery('<table>' + rawHeader + '</table>');
      header.css('position', 'absolute')
            .css('top', '0px')
            .css('background-color', '#fff');
      header.insertAfter(listDataButton.content);
      lastListDataHeader = header;

      var data = '<table>' + rawHeader;
      var i = 0;
      for (i = 0; i < self.dataValues.length; i++) {
        data += '<tr>';
        for (var j = 0; j < self.dataValues[i].length; j++) {
          var val = self.dataValues[i][j].toString();
          if (val.indexOf('//') === 0)
            data += '<td>' + val.substr(2) + '</td>';
          else
            data += '<td>' + val + '</td>';
        }
        data += '</tr>';
      }
      data += '</table>';
      listDataButton.content.html(data);
      jQuery(listDataButton.content.find('tr')[0])
        .css('visibility', 'hidden')
        .css('height', '0px');
    });
    if (self.options.listData) {
      row2.append(listDataButton.parent);
    }

    var statsButton = createPopBox();
    statsButton.button.text('Univariate Stats');
    statsButton.button.click(function() {
      var text = '';
      jQuery.each(self.dataFields, function(i) {
        text += '<b>' + self.dataFields[i] + '</b><br />';
        var data = jQuery.map(self.dataValues, function(values) {
          return parseFloat(values[i]);
        });
        text += 'Cases: ' + data.length + '<br />';
        text += 'Mean: ' + mean(data).fix(2) + '<br />';
        text += 'SD: ' + sd(data).fix(2) + '<br />';
        text += 'Min: ' + data.min().fix(2) + '<br />';
        text += 'LQ: ' + percentile(data, 25).fix(2) + '<br />';
        text += 'Median: ' + percentile(data, 50).fix(2) + '<br />';
        text += 'UQ: ' + percentile(data, 75).fix(2) + '<br />';
        text += 'Max: ' + data.max().fix(2) + '<br />';
        text += '<br />';
      });
      statsButton.content.html(text);
    });
    if (self.options.showUnivariateStats)
      row2.append(statsButton.parent);

    self.showNormalButton = jQuery('<button/>')
                         .addClass('open');
    if (self.options.showNormal)
      self.showNormalButton.text('Hide Normal Curve');
    else
      self.showNormalButton.text('Show Normal Curve');
    self.showNormalButton.click(function() {
      self.normalOverlayDiv.toggle();
      if (!self.showingNormal)
        self.showNormalButton
            .text(self.showNormalButton.text().replace('Show', 'Hide'));
      else
        self.showNormalButton
            .text(self.showNormalButton.text().replace('Hide', 'Show'));
      self.showingNormal = !self.showingNormal;
      refreshSelectedAreaOverlay();
    });
    if (self.options.showNormalButton)
      row2.append(self.showNormalButton);

    jQuery('.popbox').popbox();
  }

  initControls();
  this.reloadData();
}

// script irGrade:  interactive, real-time grading; html formatting; statistical functions,
//                  linear algebra
// copyright 1997-2012. P.B. Stark, statistics.berkeley.edu/~stark
// Version 2.2
// All rights reserved.

// !!!!Beginning of the code!!!!

Array.prototype.max = function() {
  return Math.max.apply(null, this);
};

Array.prototype.min = function() {
  return Math.min.apply(null, this);
};
Number.prototype.fix = function(n) {
  return parseFloat(this.toFixed(n));
};

// ============================================================================
// ========================= STATISTICAL SUBROUTINES ==========================

function mean(list) { // computes the mean of the list
    return(vSum(list)/list.length);
}

function vMult(a, list) { // multiply a vector times a scalar
    var list2 = new Array(list.length);
    for (var i=0; i < list.length; i++) {
        list2[i] = a*list[i];
    }
    return(list2);
}

function vScalarSum(list, scalar) { // adds the scalar to every component of the list
    var vs = new Array(list.length);
    for (var i =0; i < list.length; i++) {
        vs[i] = list[i] + scalar;
    }
    return(vs);
}

function vVectorSum(list1, list2) { // vector addition
    if (list1.length != list2.length) {
        alert('Error #1 in irGrade.vVectorSum: vector lengths are not equal');
        return(Math.NaN);
    } else {
        var vs = new Array(length(list1));
        for (var i =0; i < list1.length; i++) {
            vs[i] = list1[i] + list2[i];
        }
        return(vs);
    }
}

function vPointwiseMult(list1, list2) { // componentwise multiplication of two vectors
    var list3 = Math.NaN;
    if (list1.length != list2.length) {
        alert('Error #1 in irGrade.vPointwiseMult: vector lengths do not match!');
    } else {
        list3 = new Array(list1.length);
        for (var i=0; i < list1.length; i++) {
            list3[i] = list1[i]*list2[i];
        }
    }
    return(list3);
}

function vFloor(list) { // takes floor of all components
    var list2 = new Array(list.length);
    for (var i = 0; i < list.length; i++) {
        list2[i] = Math.floor(list[i]);
    }
    return(list2);
}

function vCeil(list) { // takes ceil of all components
    var list2 = new Array(list.length);
    for (var i = 0; i < list.length; i++) {
        list2[i] = Math.ceil(list[i]);
    }
    return(list2);
}

function vRoundToInts(list) { // round all components to the nearest int
    var list2 = new Array(list.length);
    var tmp;
    for (var i = 0; i < list.length; i++) {
        list2[i] = Math.floor(list[i]);
        if (list[i] - list2[i] >= 0.5) {
            list2[i]++;
        }
    }
    return(list2);
}

function vSum(list) { // computes the sum of the elements of list
    var tot = 0.0;
    for (var i = 0; i < list.length; i++) {
        tot += list[i];
    }
    return(tot);
}

function vProd(list) { // computes the product of the elements of list
    var p = 1.0;
    for (var i = 0; i < list.length; i++) {
        p *= list[i];
    }
    return(p);
}

function vCum(list) { // vector of cumulative sum
    var list2 = list;
    for (var i = 1; i < list.length; i++ ) {
        list2[i] += list2[i-1];
    }
    return(list2);
}

function vDiff(list) { // vector of differences; 1st element unchanged
    var list2 = new Array(list.length);
    for (var i = list.length-1; i > 0; i-- ) {
        list2[i] = list[i] - list[i-1];
    }
    list2[0] = list[0];
    return(list2);
}


function vInterval(list) { // vector of differences between successive elements; 0 subtracted from first element
    var list2 = [];
    list2[0] = list[0];
    for (var i = 1; i < list.length; i++) {
        list2[i] = list[i] - list[i-1];
    }
    return(list2);
}

function vZero(n) { // returns a vector of zeros of length n
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = 0.0;
    }
    return(list);
}

function vOne(n) { // returns a vector of ones of length n
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = 1.0;
    }
    return(list);
}

function twoNorm(list) { // two norm of a vector
    var tn = 0.0;
    for (var i=0; i < list.length; i++) {
        tn += list[i]*list[i];
    }
    return(Math.sqrt(tn));
}

function convolve(a,b) { // convolve two lists
    var c = new Array(a.length + b.length - 1);
    var left; var right;
    for (var i=0; i < a.length + b.length - 1; i++) {
        c[i] = 0;
        right = Math.min(i+1, a.length);
        left = Math.max(0, i - b.length + 1);
        for (var j=left; j < right; j++) {
            c[i] += a[j]*b[b.length - i - 1 + j];
        }
    }
    return(c);
}

function nFoldConvolve(a,n) {
    var b = a;
    for (var i=0; i < n; i++ ) {
        b = convolve(b,a);
    }
    return(b);
}

function numberLessThan(a,b) { // numerical ordering for javascript sort function
    var diff = parseFloat(a)-parseFloat(b);
    if (diff < 0) {
        return(-1);
    } else if (diff === 0) {
        return(0);
    } else {
        return(1);
    }
}

function numberGreaterThan(a,b) { // numerical ordering for javascript sort function
    var diff = parseFloat(a)-parseFloat(b);
    if (diff < 0) {
        return(1);
    } else if (diff === 0) {
        return(0);
    } else {
        return(-1);
    }
}

function sd(list) { // computes the SD of the list
    ave = mean(list);
    ssq = 0;
    for (var i = 0; i < list.length; i++) {
        ssq += (list[i] - ave)*(list[i] - ave);
    }
    ssq = Math.sqrt(ssq/list.length);
    return(ssq);
}

function sampleSd(list) { // computes the sample SD of the list
    ave = mean(list);
    ssq = 0;
    for (var i = 0; i < list.length; i++) {
        ssq += (list[i] - ave)*(list[i] - ave);
    }
    ssq = Math.sqrt(ssq/(list.length - 1.0));
    return(ssq);
}

function corr(list1, list2) {
// computes the correlation coefficient of list1 and list2
    if (list1.length != list2.length) {
        alert('Error #1 in irGrade.corr(): lists have different lengths!');
        return(Math.NaN);
    } else {
        var ave1 = mean(list1);
        var ave2 = mean(list2);
        var sd1 = sd(list1);
        var sd2 = sd(list2);
        var cc = 0.0;
        for (var i=0; i < list1.length; i++) {
            cc += (list1[i] - ave1)*(list2[i] - ave2);
        }
        cc /= sd1*sd2*list1.length;
        return(cc);
    }
}

function percentile(list,p) { // finds the pth percentile of list
    var n = list.length;
    var sList = new Array(n);
    for (var i=0; i < n; i++) sList[i] = list[i].valueOf();
    sList.sort(numberLessThan);
    var ppt = Math.max(Math.ceil(p*n/100),1);
    return(sList[ppt-1]);
}

function histMakeCounts(binEnd, data) {  // makes vector of histogram heights
        var nBins = binEnd.length - 1;
        var counts = new Array(nBins);
        var i = 0;
        for (i=0; i < nBins; i++) {
            counts[i] = 0;
        }
        for (i=0; i < data.length; i++) {
           for (var k=0; k < nBins - 1; k++) {
              if (data[i] >= binEnd[k] && data[i] < binEnd[k+1] ) {
                  counts[k] += 1;
              }
           }
           if (data[i] >= binEnd[nBins - 1] ) {
              counts[nBins - 1] += 1;
           }
        }
        for (i=0; i < nBins; i++) {
           counts[i] /= data.length*(binEnd[i+1]-binEnd[i]);
        }
        return(counts);
}

function histMakeBins(nBins, data) { // makes equispaced histogram bins that span the range of data
        binEnd = new Array(nBins+1);
        dMnMx = vMinMax(data);
        for (var i=0; i < nBins+1; i++) {
           binEnd[i] = dMnMx[0] + i*(dMnMx[1] - dMnMx[0])/nBins;
        }
        return(binEnd);
}

function histEstimatedPercentile(pct, binEnd, counts) {  // estimates the pth percentile from a histogram
        var p = pct/100.0;
        var pctile;
        if (p > 1.0) {
            pctile = Math.NaN;
        } else if (p == 1.0) {
            pctile = binEnd[nBins];
        } else {
            var area = 0.0;
            var j = 0;
            while (area < p) {
               j++;
               area = histHiLitArea(binEnd[0], binEnd[j], binEnd, counts);
            }
            j--;
            area = p - histHiLitArea(binEnd[0], binEnd[j], binEnd, counts);
            var nextBinArea = histHiLitArea(binEnd[j], binEnd[j+1], binEnd, counts);
            pctile = binEnd[j] + (area/nextBinArea) * (binEnd[j+1] - binEnd[j]);
        }
        return(pctile);
}

function histHiLitArea(loEnd, hiEnd, binEnd, counts) { // area of counts from loEnd to hiEnd
          var nBins = binEnd.length - 1;
          var area = 0;
          if (loEnd < hiEnd) {
             for (var i=0; i < nBins; i++) {
                if( binEnd[i]  > hiEnd ||  binEnd[i+1] <= loEnd) {
                } else if (binEnd[i] >= loEnd && binEnd[i+1] <= hiEnd) {
                   area += counts[i]*(binEnd[i+1]-binEnd[i]);
                } else if (binEnd[i] >= loEnd && binEnd[i+1] > hiEnd) {
                   area += counts[i]*(hiEnd - binEnd[i]);
                } else if (binEnd[i] <= loEnd && binEnd[i+1] <= hiEnd) {
                   area += counts[i]*(binEnd[i+1]-loEnd);
                } else if (binEnd[i] < loEnd && binEnd[i+1] > hiEnd) {
                   area += counts[i]*(hiEnd - loEnd);
                }
            }
         }
      return(area);
}

function listOfRandSigns(n) { // random +-1 vector
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        var rn = rand.next();
        if (rn < 0.5) {
            list[i] = -1;
        } else {
            list[i] = 1;
        }
    }
    return(list);
}

function listOfRandUniforms(n, lo, hi) { // n random variables uniform on (lo, hi)
    if ( (typeof(lo) == 'undefined') || (lo === null) ) {
        lo = 0.0;
    }
    if ( (typeof(hi) == 'undefined') || (hi === null) ) {
            hi = 1.0;
    }
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = lo + (hi-lo)*rand.next();
    }
    return(list);
}

function listOfRandInts(n, lo, hi) { // n random integers between lo and hi
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = Math.floor((hi+1 - lo)*rand.next()) + lo;
    }
    return(list);
}

function listOfDistinctRandInts(n, lo, hi) { // n dintinct random integers between lo and hi
    var list = new Array(n);
    var trial;
    var i=0;
    var unique;
    while (i < n) {
        trial = Math.floor((hi+1 - lo)*rand.next()) + lo;
        unique = true;
        for (var j = 0; j < i; j++) {
            if (trial == list[j]) unique = false;
        }
        if (unique) {
            list[i] = trial;
            i++;
        }
    }
    return(list);
}

function randomSample(list, ssize, replace) {
  // sample from list, size ssize w/ or w/o replacement.
  // default is without replacement
    var sample = [];
    var indices = [];
    if (replace !== null && replace ) {
        indices = listOfRandInts(ssize,0,list.length-1);
    } else {
        indices = listOfDistinctRandInts(ssize,0,list.length - 1);
    }
    for (var i=0; i < ssize; i++) {
        sample[i] = list[indices[i]];
    }
    return(sample) ;
}

function randomPartition(list, n) {
  // randomly partition list into n nonempty groups
    var bars = listOfDistinctRandInts(n-1, 1, list.length-1).sort(numberLessThan);
    bars[bars.length] = list.length;
    var parts = new Array(n);
    var i = 0;
    for (i = 0; i < parts.length; i++) {
        parts[i] = [];
    }
    for (i=0; i < bars[0]; i++) {
        parts[0][i] = list[i];
    }
    for (var j=1; j < n; j++) {
        for (i=0; i < bars[j]-bars[j-1]; i++) {
           parts[j][i] = list[bars[j-1]+i];
        }
    }
    return(parts);
}

function constrainedRandomPartition(list, n, mx) {
  // randomly partition list into n nonempty groups no bigger than mx
    if (n*mx < list.length) {
        alert('Error in irGrade.constrainedRandomPartition: mx too small!');
        return(false);
    } else {
        var bars = listOfDistinctRandInts(n-1, 1, list.length-1).sort(numberLessThan);
        bars[bars.length] = list.length;
        vm = vMinMax(vDiff(bars))[1];
        while (vm > mx) {
            bars = listOfDistinctRandInts(n-1, 1, list.length-1).sort(numberLessThan);
            bars[bars.length] = list.length;
            vm = vMinMax(vDiff(bars))[1];
        }
        var parts = new Array(n);
        var i = 0;
        for (i = 0; i < parts.length; i++) {
            parts[i] = [];
        }
        for (i=0; i < bars[0]; i++) {
            parts[0][i] = list[i];
        }
        for (var j=1; j < n; j++) {
            for (i=0; i < bars[j]-bars[j-1]; i++) {
               parts[j][i] = list[bars[j-1]+i];
            }
        }
        return(parts);
    }
}

function multinomialSample(pVec, n) { // multinomial sample of size n with probabilities pVec
    pVec = vMult(1.0/vSum(pVec), pVec); // renormalize in case
    var pCum = vCum(pVec);
    var counts = vZero(pVec.length);
    var rv;
    var inx;
    for (var i=0; i < n; i++) {
        rv = rand.next();
        inx = 0;
        while ( (rv > pCum[inx]) && (inx < n) ) {
            inx++;
        }
        counts[inx]++;
    }
    return(counts);
}

function normPoints(n, mu, s, dig) {   // n normals with expected value mu, sd s, rounded to dig
    var round = true;
    if ( (typeof(dig) == 'undefined') || (dig === null) ) {
        round = false;
    }
    var xVal = new Array(n);
    var i = 0;
    if (round) {
        for (i=0; i < n; i++) {
            xVal[i] = roundToDig(mu + s*rNorm(),dig);
        }
    } else {
        for (i=0; i < n; i++) {
            xVal[i] = mu + s*rNorm();
        }
    }
    return(xVal);
}

function cNormPoints(n, r) {
 // generate pseudorandom normal bivariate w/ specified realized correlation coefficient
    var xVal = new Array(n);
    var yVal = new Array(n);
    var i = 0;
    for (i=0; i<n ; i++ ) {
        xVal[i]= rNorm();
        yVal[i] = rNorm();
    }
    var rAtt = corr(xVal, yVal);
    var s = sgn(rAtt)*sgn(r);
    var xBarAtt = mean(xVal);
    var yBarAtt = mean(yVal);
    var xSdAtt = sd(xVal);
    var ySdAtt = sd(yVal);
    var pred = new Array(n);
    var resid = new Array(n);
    for (i=0; i < n; i++) {
        xVal[i] = (xVal[i] - xBarAtt)/xSdAtt;
        pred[i] = s*rAtt*xVal[i]*ySdAtt+ yBarAtt;
        resid[i] = s*yVal[i] - pred[i];
    }
    var resNrm = rms(resid);
    for (i = 0; i < n; i++) {
        yVal[i] = Math.sqrt(1.0-r*r)*resid[i]/resNrm + r*xVal[i];
    }
    var ymnmx = vMinMax(yVal);
    var xmnmx = vMinMax(xVal);
    var xscl = 9.5/(xmnmx[1] - xmnmx[0]);
    var yscl = 9.5/(ymnmx[1] - ymnmx[0]);
    for (i=0; i < n; i++) {
        xVal[i] = (xVal[i] - xmnmx[0]) * xscl  + 1.0;
        yVal[i] = (yVal[i] - ymnmx[0]) * yscl + 1.0;
    }
    var lists = new Array(xVal,yVal);
    return(lists);
}// ends cNormPoints

function listOfRandReals(n,lo,hi) { // n-vector of uniforms on [lo, hi]
    var list = new Array(n);
    for (var i=0; i < n; i++) {
        list[i] = (hi - lo)*rand.next() + lo;
    }
    return(list);
}

function rNorm() {  // standard normal pseudorandom variable
    var y = normInv(rand.next());
    return(y);
} // ends rNorm()

function normCdf(y) { // normal distribution cumulative distribution function
   return(0.5*erfc(-y*0.7071067811865475));
}

function erfc(x) { // error function
     var xbreak = 0.46875;     // for normal cdf
// coefficients for |x| <= 0.46875
    var a = [3.16112374387056560e00, 1.13864154151050156e02,
             3.77485237685302021e02, 3.20937758913846947e03,
             1.85777706184603153e-1];
    var b = [2.36012909523441209e01, 2.44024637934444173e02,
            1.28261652607737228e03, 2.84423683343917062e03];
// coefficients for 0.46875 <= |x| <= 4.0
    var c = [5.64188496988670089e-1, 8.88314979438837594e00,
             6.61191906371416295e01, 2.98635138197400131e02,
             8.81952221241769090e02, 1.71204761263407058e03,
             2.05107837782607147e03, 1.23033935479799725e03,
             2.15311535474403846e-8];
    var d = [1.57449261107098347e01, 1.17693950891312499e02,
             5.37181101862009858e02, 1.62138957456669019e03,
             3.29079923573345963e03, 4.36261909014324716e03,
             3.43936767414372164e03, 1.23033935480374942e03];
// coefficients for |x| > 4.0
    var p = [3.05326634961232344e-1, 3.60344899949804439e-1,
             1.25781726111229246e-1, 1.60837851487422766e-2,
             6.58749161529837803e-4, 1.63153871373020978e-2];
    var q = [2.56852019228982242e00, 1.87295284992346047e00,
             5.27905102951428412e-1, 6.05183413124413191e-2,
             2.33520497626869185e-3];
    var y, z, xnum, xden, result, del;

/*
Translation of a FORTRAN program by W. J. Cody,
Argonne National Laboratory, NETLIB/SPECFUN, March 19, 1990.
The main computation evaluates near-minimax approximations
from "Rational Chebyshev approximations for the error function"
by W. J. Cody, Math. Comp., 1969, PP. 631-638.
*/

//  evaluate  erf  for  |x| <= 0.46875

    var i = 0;
    if(Math.abs(x) <= xbreak) {
        y = Math.abs(x);
        z = y * y;
        xnum = a[4]*z;
        xden = z;
        for (i = 0; i< 3; i++) {
            xnum = (xnum + a[i]) * z;
            xden = (xden + b[i]) * z;
        }
        result = 1.0 - x* (xnum + a[3])/ (xden + b[3]);
    } else if (Math.abs(x) <= 4.0) {
        y = Math.abs(x);
        xnum = c[8]*y;
        xden = y;
        for (i = 0; i < 7; i++) {
            xnum = (xnum + c[i])* y;
            xden = (xden + d[i])* y;
        }
        result = (xnum + c[7])/(xden + d[7]);
        if (y > 0.0) {
            z = Math.floor(y*16)/16.0;
        } else {
            z = Math.ceil(y*16)/16.0;
        }
        del = (y-z)*(y+z);
        result = Math.exp(-z*z) * Math.exp(-del)* result;
    } else {
        y = Math.abs(x);
        z = 1.0 / (y*y);
        xnum = p[5]*z;
        xden = z;
        for (i = 0; i < 4; i++) {
            xnum = (xnum + p[i])* z;
            xden = (xden + q[i])* z;
        }
        result = z * (xnum + p[4]) / (xden + q[4]);
        result = (1.0/Math.sqrt(Math.PI) -  result)/y;
        if (y > 0.0) {
            z = Math.floor(y*16)/16.0;
        } else {
            z = Math.ceil(y*16)/16.0;
        }
        del = (y-z)*(y+z);
        result = Math.exp(-z*z) * Math.exp(-del) * result;
    }
    if (x < -xbreak) {
        result = 2.0 - result;
    }
    return(result);
}

function normInv(p) {
    if ( p === 0.0 ) {
        return(Math.NEGATIVE_INFINITY);
    } else if ( p >= 1.0 ) {
        return(Math.POSITIVE_INFINITY);
    } else {
        return(Math.sqrt(2.0) * erfInv(2*p - 1));
    }
}

function erfInv(y) {
    var a = [ 0.886226899, -1.645349621, 0.914624893, -0.140543331];
    var b = [-2.118377725, 1.442710462, -0.329097515, 0.012229801];
    var c = [-1.970840454, -1.624906493, 3.429567803, 1.641345311];
    var d = [ 3.543889200, 1.637067800];
    var y0 = 0.7;
    var x = 0;
    var z = 0;
    if (Math.abs(y) <= y0) {
        z = y*y;
        x = y * (((a[3]*z+a[2])*z+a[1])*z+a[0])/
         ((((b[3]*z+b[2])*z+b[1])*z+b[0])*z+1.0);
    } else if (y > y0 && y < 1.0) {
        z = Math.sqrt(-Math.log((1-y)/2));
        x = (((c[3]*z+c[2])*z+c[1])*z+c[0]) / ((d[1]*z+d[0])*z+1);
    } else if (y < -y0 && y > -1) {
        z = Math.sqrt(-Math.log((1+y)/2));
        x = -(((c[3]*z+c[2])*z+c[1])*z+c[0])/ ((d[1]*z+d[0])*z+1);
    }
    x = x - (1.0 - erfc(x) - y) / (2/Math.sqrt(Math.PI) * Math.exp(-x*x));
    x = x - (1.0 - erfc(x) - y) / (2/Math.sqrt(Math.PI) * Math.exp(-x*x));

    return(x);
} // ends erfInv

function betaCdf( x,  a,  b) {
   if (a <= 0 || b <= 0) {
      return(Math.NaN);
   } else if (x >= 1) {
      return(1.0);
   } else if ( x > 0.0) {
      return(Math.min(incBeta(x ,a ,b),1.0));
   } else {
      return(0.0);
   }
}

function betaPdf( x,  a,  b) {
    if (a <= 0 || b <= 0 || x < 0 || x > 1) {
        return(Math.NaN);
    } else if ((x === 0 && a < 1) || (x == 2 && b < 1)) {
        return(Math.POSITIVE_INFINITY);
    } else if (!(a <= 0 || b <= 0 || x <= 0 || x >= 1)) {
        return(Math.exp((a - 1)*Math.log(x) + (b-1)*Math.log(1 - x) - lnBeta(a,b)));
    } else {
        return(0.0);
    }
}

function lnBeta( x, y) {
    return(lnGamma(x) + lnGamma(y) - lnGamma(x+y));
}

function betaInv( p,  a,  b) {
    if (p < 0 || p > 1 || a <= 0 || b <= 0) {
        return(Math.NaN);
    } else if ( p === 0 ) {
        return(Math.NEGATIVE_INFINITY);
    } else if ( p == 1) {
        return(Math.POSITIVE_INFINITY);
    } else {
        var maxIt = 100;
        var it = 0;
        var tol = Math.sqrt(eps);
        var work = 1.0;
        var next;
        var x;
        if (a === 0.0 ) {
            x = Math.sqrt(eps);
        } else if ( b === 0.0) {
            x = 1 - Math.sqrt(eps);
        } else {
            x = a/(a+b);
        }
        while (Math.abs(work) > tol*Math.abs(x) && Math.abs(work) > tol && it < maxIt) {
           it++;
           work = (betaCdf(x,a,b) - p)/betaPdf(x,a,b);
           next =  x - work;
           while (next < 0 || next > 1) {
               work = work/2;
               next = x - work;
           }
           x = next;
         }
         return(x);
     }
}

function lnGamma(x) {
/*  natural ln(gamma(x)) without computing gamma(x)
    P.B. Stark

      JavaScript subroutine is based on a MATLAB program by C. Moler,
      in turn based on a FORTRAN program by W. J. Cody,
      Argonne National Laboratory, NETLIB/SPECFUN, June 16, 1988.

      References:

      1) W. J. Cody and K. E. Hillstrom, 'Chebyshev Approximations for
         the Natural Logarithm of the Gamma Function,' Math. Comp. 21,
         1967, pp. 198-203.

      2) K. E. Hillstrom, ANL/AMD Program ANLC366S, DGAMMA/DLGAMA, May,
         1969.

      3) Hart, Et. Al., Computer Approximations, Wiley and sons, New
         York, 1968.
*/

     var d1 = -5.772156649015328605195174e-1;
     var p1 = [4.945235359296727046734888e0, 2.018112620856775083915565e2,
           2.290838373831346393026739e3, 1.131967205903380828685045e4,
           2.855724635671635335736389e4, 3.848496228443793359990269e4,
           2.637748787624195437963534e4, 7.225813979700288197698961e3];
     var q1 = [6.748212550303777196073036e1, 1.113332393857199323513008e3,
           7.738757056935398733233834e3, 2.763987074403340708898585e4,
           5.499310206226157329794414e4, 6.161122180066002127833352e4,
           3.635127591501940507276287e4, 8.785536302431013170870835e3];
     var d2 = 4.227843350984671393993777e-1;
     var p2 = [4.974607845568932035012064e0, 5.424138599891070494101986e2,
           1.550693864978364947665077e4, 1.847932904445632425417223e5,
           1.088204769468828767498470e6, 3.338152967987029735917223e6,
           5.106661678927352456275255e6, 3.074109054850539556250927e6];
     var q2 = [1.830328399370592604055942e2, 7.765049321445005871323047e3,
           1.331903827966074194402448e5, 1.136705821321969608938755e6,
           5.267964117437946917577538e6, 1.346701454311101692290052e7,
           1.782736530353274213975932e7, 9.533095591844353613395747e6];
     var d4 = 1.791759469228055000094023e0;
     var p4 = [1.474502166059939948905062e4, 2.426813369486704502836312e6,
           1.214755574045093227939592e8, 2.663432449630976949898078e9,
           2.940378956634553899906876e10, 1.702665737765398868392998e11,
           4.926125793377430887588120e11, 5.606251856223951465078242e11];
     var q4 = [2.690530175870899333379843e3, 6.393885654300092398984238e5,
           4.135599930241388052042842e7, 1.120872109616147941376570e9,
           1.488613728678813811542398e10, 1.016803586272438228077304e11,
           3.417476345507377132798597e11, 4.463158187419713286462081e11];
     var c = [-1.910444077728e-03, 8.4171387781295e-04,
          -5.952379913043012e-04, 7.93650793500350248e-04,
          -2.777777777777681622553e-03, 8.333333333333333331554247e-02,
           5.7083835261e-03];

     var lng = Math.NaN;
     var mach = 1.e-12;
     var den = 1.0;
     var num = 0;
     var xm1, xm2, xm4;
     var i = 0;

   if (x < 0) {
       return(lng);
   } else if (x <= mach) {
       return(-Math.log(x));
   } else if (x <= 0.5) {
      for (i = 0; i < 8; i++) {
            num = num * x + p1[i];
            den = den * x + q1[i];
      }
      lng = -Math.log(x) + (x * (d1 + x * (num/den)));
   } else if (x <= 0.6796875) {
      xm1 = x - 1.0;
      for (i = 0; i < 8; i++) {
         num = num * xm1 + p2[i];
         den = den * xm1 + q2[i];
      }
      lng = -Math.log(x) + xm1 * (d2 + xm1*(num/den));
   } else if (x <= 1.5) {
      xm1 = x - 1.0;
      for (i = 0; i < 8; i++) {
         num = num*xm1 + p1[i];
         den = den*xm1 + q1[i];
      }
      lng = xm1 * (d1 + xm1*(num/den));
   } else if (x <= 4.0) {
      xm2 = x - 2.0;
      for (i = 0; i<8; i++) {
         num = num*xm2 + p2[i];
         den = den*xm2 + q2[i];
      }
      lng = xm2 * (d2 + xm2 * (num/den));
   } else if (x <= 12) {
      xm4 = x - 4.0;
      den = -1.0;
      for (i = 0; i < 8; i++)  {
         num = num * xm4 + p4[i];
         den = den * xm4 + q4[i];
      }
      lng = d4 + xm4 * (num/den);
   } else {
      var r = c[6];
      var xsq = x * x;
      for (i = 0; i < 6; i++) {
         r = r / xsq + c[i];
      }
      r = r / x;
      var lnx = Math.log(x);
      var spi = 0.9189385332046727417803297;
      lng = r + spi - 0.5*lnx + x*(lnx-1);
    }
    return(lng);
} // ends lnGamma


function normPdf( mu,  sigma, x) {
     return(Math.exp(-(x-mu)*(x-mu)/(2*sigma*sigma))/
            (Math.sqrt(2*Math.PI)*sigma));
} // ends normPdf


function tCdf(df, x) { // cdf of Student's t distribution with df degrees of freedom
    var ans;
    if (df < 1) {
        ans = Math.NaN;
    } else if (x === 0.0) {
        ans = 0.5;
    } else if (df == 1) {
        ans = 0.5 + Math.atan(x) / Math.PI;
    } else if (x > 0) {
        ans = 1 - (incBeta(df/(df+x*x), df/2.0, 0.5))/2;
    } else if (x < 0) {
        ans = incBeta(df/(df+x*x), df/2.0, 0.5)/2;
    }
    return(ans);
}

function tInv(p, df ) { // inverse Student-t distribution with
                                              // df degrees of freedom
    var z;
    if (df < 0 || p < 0) {
        return(Math.NaN);
    } else if (p === 0.0) {
        return(Math.NEGATIVE_INFINITY);
    } else if (p == 1) {
        return(Math.POSITIVE_INFINITY);
    } else if (df == 1) {
        return(Math.tan(Math.PI*(p-0.5)));
    } else if ( p >= 0.5) {
        z = betaInv(2.0*(1-p),df/2.0,0.5);
        return(Math.sqrt(df/z - df));
    } else {
        z = betaInv(2.0*p,df/2.0,0.5);
        return(-Math.sqrt(df/z - df));
    }
}

function incBeta(x, a, b) { // incomplete beta function
       // I_x(z,w) = 1/beta(z,w) * integral from 0 to x of t^(z-1) * (1-t)^(w-1) dt
       // Ref: Abramowitz & Stegun, Handbook of Mathemtical Functions, sec. 26.5.
    var res;
    if (x < 0 || x > 1) {
        res = Math.NaN;
    } else {
        res = 0;
        var bt = Math.exp(lnGamma(a+b) - lnGamma(a) - lnGamma(b) +
                    a*Math.log(x) + b*Math.log(1-x));
        if (x < (a+1)/(a+b+2)) {
            res = bt * betaGuts(x, a, b) / a;
        } else {
            res = 1 - bt*betaGuts(1-x, b, a) / b;
        }
    }
    return(res);
}

function betaGuts( x, a, b) { // guts of the incomplete beta function
    var ap1 = a + 1;
    var am1 = a - 1;
    var apb = a + b;
    var am = 1;
    var bm = am;
    var y = am;
    var bz = 1 - apb*x/ap1;
    var d = 0;
    var app = d;
    var ap = d;
    var bpp = d;
    var bp = d;
    var yold = d;
    var m = 1;
    var t;
    while (y-yold > 4*eps*Math.abs(y)) {
       t = 2 * m;
       d = m * (b - m) * x / ((am1 + t) * (a + t));
       ap = y + d * am;
       bp = bz + d * bm;
       d = -(a + m) * (apb + m) * x / ((a + t) * (ap1 + t));
       app = ap + d * y;
       bpp = bp + d * bz;
       yold = y;
       am = ap / bpp;
       bm = bp / bpp;
       y = app / bpp;
       if (m == 1) bz = 1;
       m++;
    }
    return(y);
}

function chi2Cdf( x, df ) {
    return(gammaCdf(x,df/2,2));
}

function chi2Inv( p, df ) { // kluge for chi-square quantile function.
    var guess = Math.NaN;
    if (p === 0.0) {
        guess = 0.0;
    } else if ( p == 1.0 ) {
        guess = Math.POSITIVE_INFINITY;
    } else if ( p < 0.0 ) {
        guess = Math.NaN;
    } else {
        var tolAbs = 1.0e-8;
        var tolRel = 1.0e-3;
        guess = Math.max(0.0, df + Math.sqrt(2*df)*normInv(p)); // guess from normal approx
        var currP = chi2Cdf( guess, df);
        var loP = currP;
        var hiP = currP;
        var guessLo = guess;
        var guessHi = guess;
        while (loP > p) { // step down
            guessLo = 0.8*guessLo;
            loP = chi2Cdf( guessLo, df);
        }
        while (hiP < p) { // step up
            guessHi = 1.2*guessHi;
            hiP = chi2Cdf( guessHi, df);
        }
        guess = (guessLo + guessHi)/2.0;
        currP = chi2Cdf( guess, df);
        while ( (Math.abs(currP - p) > tolAbs) || (Math.abs(currP - p)/p > tolRel) ) { // bisect
            if ( currP < p ) {
                guessLo = guess;
            } else {
                guessHi = guess;
            }
            guess = (guessLo + guessHi)/2.0;
            currP = chi2Cdf(guess, df);
        }
    }
    return(guess);
}

function gammaCdf( x,  a,  b) { // gamma distribution CDF.
    var p = Math.NaN;
    if (a <= 0 || b <= 0) {
    } else if (x <= 0) {
        p = 0.0;
    } else {
        p = Math.min(incGamma(x/b, a), 1.0);
    }
    return(p);
}

function incGamma( x,  a) {
    var inc = 0;
    var gam = lnGamma(a+rmin);
    if (x === 0.0) {
        inc = 0;
    } else if (a === 0) {
        inc = 1;
    } else if (x < a+1) {
        var ap = a;
        var sum = 1.0/ap;
        var del = sum;
        while (Math.abs(del) >= 10*eps*Math.abs(sum)) {
            del *= x/(++ap);
            sum += del;
        }
        inc = sum * Math.exp(-x + a*Math.log(x) - gam);
    } else if (x >= a+1) {
       var a0 = 1;
       var a1 = x;
       var b0 = 0;
       var b1 = 1;
       var fac = 1;
       var n = 1;
       var g = 1;
       var gold = 0;
       var ana;
       var anf;
       while (Math.abs(g-gold) >= 10*eps*Math.abs(g)) {
            gold = g;
            ana = n - a;
            a0 = (a1 + a0 *ana) * fac;
            b0 = (b1 + b0 *ana) * fac;
            anf = n*fac;
            a1 = x * a0 + anf * a1;
            b1 = x * b0 + anf * b1;
            fac = 1.0 / a1;
            g = b1 * fac;
            n++;
       }
       inc = 1 - Math.exp(-x + a*Math.log(x) - gam) * g;
    }
    return(inc);
}

function poissonPmf( lambda, k) {
    var p = 0.0;
    if (k >= 0) {
        p = Math.exp(-lambda)*Math.pow(lambda,k)/factorial(k);
    }
    return(p);
}

function poissonCdf( lambda, k) {
    var p = 0;
    var b = 0;
    var m = 0;
    while (m <= k) {
        b += Math.pow(lambda, m++)/factorial(k);
    }
    p += Math.exp(-lambda)*b;
    return(p);
}

function poissonTail(lambda, k) {
    return(1.0-poissonCdf(lambda, k-1));
}


function factorial(n) { // computes n!
    var fac=1;
    for (var i=n; i > 1; i--) {fac *= i;}
    return(fac);
}

function binomialCoef(n,k) { // computes n choose k
    if (n < k || n < 0) {
        return(0.0);
    } else if ( k === 0 || n === 0 || n == k) {
        return(1.0);
    } else {
        var minnk = Math.min(k, n-k);
        var coef = 1;
        for (var j = 0; j < minnk; j++) {
            coef *= (n-j)/(minnk-j);
        }
        return(coef);
    }
}

function binomialPmf(n, p, k) {  // binomial pmf at k.
    var pmf = binomialCoef(n,k)*Math.pow(p,k)*Math.pow((1-p),(n-k));
    return(pmf);
}

function binomialCdf(n, p, k) {  // binomial CDF:  Pr(X <= k), X~B(n,p)
    if (k < 0) {
        return(0.0);
    } else if (k >= n) {
        return(1.0);
    } else {
        var cdf = 0.0;
        for (var i = 0; i <= k; i++) {
            cdf += binomialPmf(n, p, i);
        }
        return(cdf);
    }
}

function binomialTail(n,p,k) { // binomial tail probability Pr(X >= k), X~B(n,p)
    if (k < 0) {
        return(1.0);
    } else if (k >= n) {
        return(0.0);
    } else {
        var tailP = 0.0;
        for (var i = k; i <= n; i++) {
            tailP += binomialPmf(n, p, i);
        }
        return(tailP);
    }
}

function binomialInv(n, p, pt) { // binomial percentile function
    var t = 0;
    if (pt < 0 || pt > 1) {
        t = NaN;
    } else if (pt === 0.0) {
        t = 0;
    } else if (pt == 1.0) {
        t = n;
    } else {
        t = 0;
        var pc = 0.0;
        while ( pc < pt ) {
            pc += binomialPmf(n, p, t++);
        }
        t -= 1;
    }
    return(t);
}

function multinomialCoef(list, n) { // multinomial coefficient.
// WARNING:  not very stable algorithm; avoid for large n.
    var val = 0;
    var lmn = vMinMax(list);
    if (typeof(n) == 'undefined' || n === null) {
        n = vSum(list);
    }
    if (lmn[0] < 0.0) {
        alert('Error #1 in irGrade.multinomialCoef: a number of outcomes is negative!');
    } else if (n == vSum(list)) {
        val = factorial(n);
        for (var i=0; i < list.length; i++) {
            val /= factorial(list[i]);
        }
    }
    return(val);
}

function multinomialPmf(olist, plist, n) { // multinomial pmf; not stable algorithm
    var val = 0.0;
    var pmn = vMinMax(plist);
    var omn = vMinMax(olist);
    if (typeof(n) == 'undefined' || n === null) {
        n = vSum(olist);
    }
    if (olist.length != plist.length) {
        alert('Error #1 in irGrade.multinomialPmf: length of outcome and probability vectors ' +
               'do not match!');
    } else if (pmn[0] < 0.0) {
        alert('Error #2 in irGrade.multinomialPmf: a probability is negative!');
    } else if (omn[0] < 0.0) {
        alert('Error #3 in irGrade.multinomialPmf: a number of outcomes is negative!');
    } else if (n == vSum(olist)) {
        var pl = vMult(1.0/vSum(plist), plist);  // just in case
        val = factorial(n);
        for (var i=0; i< olist.length; i++) {
            val *= Math.pow(pl[i], olist[i])/factorial(olist[i]);
        }
    }
    return(val);
}


function geoPmf( p,  k) {
  // chance it takes k trials to the first success in iid Bernoulli(p) trials
  // EX = 1/p; SD(X) = sqrt(1-p)/p
    if (k < 1 || p === 0.0) {
        return(0.0);
    } else {
        return(Math.pow((1-p),k-1)*p);
    }
}

function geoCdf( p, k) {
  // chance it takes k or fewer trials to the first success in iid Bernoulli(p) trials
    if (k < 1 || p === 0.0) {
        return(0.0);
    } else {
        return(1-Math.pow( 1-p, k));
    }
}

function geoTail( p,  k) {
  // chance of k or more trials to the first success in iid Bernoulli(p) trials
    return(1 - geoCdf(p, k-1));
}

function geoInv(p, pt) { // geometric percentile function
    var t = 0;
    if (pt < 0 || pt > 1) {
        t = Math.NaN;
    } else if (pt === 0.0) {
        t = 0;
    } else if (pt == 1.0) {
        t = Math.POSITIVE_INFINITY;
    } else {
        t = 0;
        var pc = 0.0;
        while ( pc < pt ) {
            pc += geoPmf(p, t++);
        }
    }
    return(t);
}

function hyperGeoPmf( N,  M,  n,  m) {
  // chance of drawing m of M objects in a sample of size n from
  // N objects in all.  p = (M C m)*(N-M C n-m)/(N C n)
  // EX = n*M/N; SD(X)= sqrt((N-n)/(N-1))*sqrt(np(1-p));
    var p;
    if ( n < m || N < M || M < m  || m < 0 || N < 0) {
        return(0.0);
    } else {
        p = binomialCoef(M,m)*binomialCoef(N-M,n-m)/binomialCoef(N,n);
        return(p);
    }
}

function hyperGeoCdf( N,  M,  n,  m) {
  // chance of drawing m or fewer of M objects in a sample of size n from
  // N objects in all
    var p=0.0;
    var mMax = Math.min(m,M);
    mMax = Math.min(mMax,n);
    for (var i = 0; i <= mMax; i++) {
        p += hyperGeoPmf(N, M, n, i);
    }
    return(p);
}

function hyperGeoTail( N,  M,  n,  m) {
  // chance of drawing m or more of M objects in a sample of size n from
  // N objects in all
    var p=0.0;
    for (var i = m; i <= Math.min(M,n); i++) {
        p += hyperGeoPmf(N, M, n, i);
    }
    return(p);
}

function negBinomialPmf( p,  s,  t) {
  // chance that the sth success in iid Bernoulli trials is on the tth trial
  // EX = s/p; SD(X) = sqrt(s(1-p))/p
    if (s > t || s < 0) {
        return(0.0);
    }
    var prob = p*binomialPmf(t-1,p,s-1);
    return(prob);
}

function negBinomialCdf( p,  s,  t) {
  // chance the sth success in iid Bernoulli trials is on or before the tth trial
    var prob = 0.0;
    for (var i = s; i <= t; i++) {
        prob += negBinomialPmf(p, s, i);
    }
    return(prob);
}

function pDieRolls(rolls,spots) { // chance that the sum of 'rolls' rolls of a die = 'spots'
    if (rolls > 4) {
        alert('Error #1 in irGrade.pDiceRolls: too many rolls ' + rolls + '. ');
        return(Math.NaN);
    } else {  // BRUTE FORCE!
        var found = 0;
        if (spots < rolls || spots > 6*rolls) {return(0.0);}
        var possible = Math.pow(6,rolls);
        var i = 0;
        var j = 0;
        var k = 0;
        if (rolls == 1) {
            return(1/possible);
        } else if (rolls == 2) {
            for (i=1; i <=6; i++ ) {
                for (j=1; j <= 6; j++ ) {
                    if (i+j == spots ) {found++;}
                }
            }
        } else if (rolls == 3 ) {
            for (i=1; i <=6; i++ ) {
                for (j=1; j<=6; j++ ) {
                    for (k=1; k<=6; k++ ) {
                        if (i+j+k == spots ) {found++;}
                    }
                }
            }
        } else if (rolls == 4 ) {
            for (i=1; i <=6; i++ ) {
                for (j=1; j<=6; j++ ) {
                    for (k=1; k<=6; k++ ) {
                        for (var m=1; m <=6; m++ ) {
                            if (i+j+k+m == spots ) {found++;}
                        }
                    }
                }
            }
        }
        return(found/possible);
    }
    return(false);
}

function permutations(n,k) { // number of permutations of k of n things
    if (n < k || n < 0) {
        return(0);
    } else if ( k===0 || n === 0) {
        return(1);
    } else {
        var coef=1;
        for (var j=0; j < k; j++) coef *= (n-j);
        return(coef);
    }
}


function sgn(x) {  // signum function
    if (x >= 0) {
        return(1);
    } else if (x < 0) {
        return (-1);
    }
}

function linspace(lo,hi,n) { // n linearly spaced points between lo and hi
    var spaced = new Array(n);
    var dx =(hi-lo)/(n-1);
    for (var i=0; i < n; i++) {
        spaced[i] = lo + i*dx;
    }
    return(spaced);
}

function rms(list) { // rms
    var r = 0;
    for (var i=0; i < list.length; i++) r += list[i]*list[i];
    r /= list.length;
    return(Math.sqrt(r));
}

function vMinMax(list){ // returns min and max of list
    var mn = list[0];
    var mx = list[0];
    for (var i=1; i < list.length; i++) {
        if (mn > list[i]) mn = list[i];
        if (mx < list[i]) mx = list[i];
    }
    var vmnmx =  new Array(mn,mx);
    return(vmnmx);
}

function vMinMaxIndices(list){ // returns min, max, index of min, index of max
    var mn = list[0];
    var indMn = 0;
    var mx = list[0];
    var indMx = 0;
    for (var i=1; i < list.length; i++) {
        if (mn > list[i]) {
            mn = list[i];
            indMn = i;
        }
        if (mx < list[i]) {
            mx = list[i];
            indMx = i;
        }
    }
    var vmnmx =  new Array(mn,mx,indMn,indMx);
    return(vmnmx);
}

function vMinMaxAbs(list) {
// returns min and max of absolute values of a list's elements
    var mn = Math.abs(list[0]);
    var mx = Math.abs(list[0]);
    var val;
    for (var i=1; i < list.length; i++) {
        val = Math.abs(list[i]);
            if (mn > val) mn = val;
            if (mx < val) mx = val;
    }
    var vmnmx =  new Array(mn,mx);
    return(vmnmx);
}

function randBoolean(p){ // random boolean value, prob p that it is true
    if (typeof(p) == 'undefined' || p === null) {
        p = 0.5;
    }
    if (rand.next() <= p) {
        return(false);
    } else {
        return(true);
    }
}

function sortUnique(list,order) { // sort a list, remove duplicate entries
    var temp = list;
    if (typeof(order) != 'undefined' && order !== null) {
        temp.sort(order);
    } else {
        temp.sort();
    }
    var temp2 = [];
    temp2[0] = temp[0];
    var ix = 0;
    for (var i=1; i < temp.length; i++) {
        if (temp[i] != temp2[ix] ) {
            temp2[++ix] = temp[i];
        }
    }
    return(temp2);
}

function uniqueCount(list) { // unique elements and their counts
    var temp = {};
    temp[list[0]] = list[0];
    var j = 0;
    for (j=1; j < list.length; j++) {
        if (typeof(temp[list[j]]) == 'undefined' || temp[list[j]] === null) {
             temp[list[j]] = 1;
        } else {
             temp[list[j]]++;
        }
    }
    uc = new Array(2);
    uc[0] = [];
    uc[1] = [];
    var k = 0;
    for (j in temp) {
        uc[0][k] = j;
        uc[1][k++] = temp[j];
    }
    return(uc);
}

function unique(list) {
    return(uniqueCount(list)[0]);
}

function randPermutation(list,index) { // returns a random permutation of list
    var randIndex = listOfDistinctRandInts(list.length,0,list.length-1);
    var thePermutation = new Array(list.length);
    var i = 0;
    for (i=0; i < list.length; i++) {
        thePermutation[i] = list[randIndex[i]];
    }
    var p = 0;
    if (typeof(index) != 'undefined' && index == 'forward') { // original indices
        p = new Array(2);
        p[0] = thePermutation;
        p[1] = randIndex;
        thePermutation = p;
    } else if (typeof(index) != 'undefined' && index == 'inverse') { // inverse permutation
        p = new Array(2);
        p[0] = thePermutation;
        p[1] = new Array(list.length);
        for (i=0; i < list.length; i++) {
            p[1][randIndex[i]] = i;
        }
        thePermutation = p;
    }
    return(thePermutation);
}


function cyclicPermutation(n, k) { // cyclic permutation by k of of the integers 0 to n-1
    if (typeof(k) == 'undefined' || k === null) {
        k = 1;
    }
    var perm = new Array(n);
    for (var i = 0; i < n; i++) {
            perm[i] = (i+k)%n;
    }
    return(perm);
}

function distinctPermutation(n, k) { // returns a permutation of the integers 0 to n-1
                                     // in which no index maps to itself
    if (typeof(k) == 'undefined' || k === null) {
            k = Math.min(3, n-1);
    }
    return(cyclicPermutation(n,k));
}

function distinctRandPermutation(n) { // returns a random permutation of the integers 0 to n-1
                                        // in which no index maps to itself
    function isInPlace(x) {  // is any index in its original place?
        v = false;
        for (var i=0; i < x.length; i++) {
           if (x[i] == i) {
              v = true;
           }
        }
        return(v);
    }
    var x = new Array(n);
    for (var i = 0; i < n; i++) {
        x[i] = i;
    }
    x = randPermutation(x);
    while (isInPlace(x)) {
       x = randPermutation(x);
    }
    return(x);
}




function fakeBivariateData(nPoints, funArray, heteroFac, snr, loEnd, hiEnd) {
   // returns a 2-d array of synthetic data generated from a polynomial,
   // according to the contents of funArray.
   // if funArray[0] == 'polynomial', uses the other elements of funArray as
   // the coefficients of a polynomial.
   // funArray[1] + funArray[2]*X + funArray[3]*X^2 + ...
   // 1/3 of the points have noise level heteroFac times larger than the rest.
   // Normalizes the errors  to signal/noise ratio snr  in 2-norm
    var data = new Array(2);
    data[0] = new Array(nPoints);
    data[1] = new Array(nPoints);
    if (snr === 0) {
            snr = 2;
    }
    var x;
    var fVal;
    var xPow;
    var i;
    if (funArray[0] == 'polynomial') {
        if (typeof(loEnd) == 'undefined' || loEnd === null) {   // lower limit of X variable
            loEnd = -10;
        }
        if (typeof(hiEnd) == 'undefined' || hiEnd === null) {   // upper limit of X variable
            hiEnd = 10;
        }
        var dX = (hiEnd - loEnd)/(nPoints - 1);
        for (i=0; i < nPoints; i++) {
            x = loEnd + i*dX;
            data[0][i] = x;
            fVal = 0.0;
            xPow = 1.0;
            for (var j=1;  j < funArray.length; j++) {
                fVal +=  xPow*funArray[j];
                xPow *= x;
            }
            data[1][i] = fVal;
        }
    } else {
        alert('Error #1 in irGrade.fakeBivariateData()!\n' +
            'Unsupported function type: ' + funArray[0].toString());
        return(null);
    }
// now add noise.
    var sigNorm = twoNorm(data[1]);
    var noise = new Array(nPoints);
    for (i=0; i < nPoints; i++) {
        noise[i] = rNorm();
    }
// pick a random set to perturb for heteroscedastic noise
    var segLen = Math.floor(nPoints/3);
    var startPt = Math.floor(2*nPoints/3*rand.next());
    for (i=startPt; i < startPt+segLen; i++) {
        noise[i] = noise[i]*heteroFac;
    }
    var noiseNorm = twoNorm(noise);
    for (i=0; i < nPoints; i++) {
        data[1][i] += noise[i]*sigNorm/noiseNorm/snr;
    }
    return(data);
}

function nextRand() {  // generates next random number in a sequence
    var up   = this.seed / this.Q;
    var lo   = this.seed % this.Q;
    var trial = this.A * lo - this.R * up;
    if (trial > 0) {
        this.seed = trial;
    } else {
        this.seed = trial + this.M;
    }
    return (this.seed * this.oneOverM);
}

function rng(s) {
       if ( typeof(s)=='undefined' || s === null ){
           var d = new Date();
           this.seed = 2345678901 + (d.getSeconds() * 0xFFFFFF) + (d.getMinutes() * 0xFFFF);
       } else {
           this.seed = s;
       }
       this.A = 48271;
       this.M = 2147483647;
       this.Q = this.M / this.A;
       this.R = this.M % this.A;
       this.oneOverM = 1.0 / this.M;
       this.next = nextRand;
       this.getSeed = getRandSeed;
       return(this);
}

function getRandSeed() { // get seed of random number generator
    return(this.seed);
}

function crypt(s,t) {
    var slen = s.length;
    var tlen = t.length;
    var rad = 16;
    var r = 0;
    var i;
    var j = -1;
    var result = '';
    if (s.substr(0,2) == '0x') {
        for (i=2; i < slen; i+=2) {
            if (++j >= tlen) {j = 0;}
            r = parseInt(s.substr(i,2),rad) ^ t.charCodeAt(j);
            result += String.fromCharCode(r);
        }
    } else {
        result +='0x';
        for ( i=0; i < slen; i++) {
           if (++j >= tlen) {j = 0;}
           r = s.charCodeAt(i) ^ t.charCodeAt(j);
           result += (r < rad ? '0' : '') + r.toString(rad);
        }
    }
    return(result);
}


