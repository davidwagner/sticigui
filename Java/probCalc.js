/*
Calculator for probability distributions

copyright (c) 2013 by P.B. Stark
License: AGPL

Dependencies: irGrade,js, jQuery, jQuery-ui

*/

function probCalc(container_id, params) {
    var self = this;
    this.container = $('#' + container_id);

    if (!params instanceof Object) {
       console.error('probCalc parameters should be an object');
       return;
    }

// default options
    this.options = {
                    distributions: [ ["Binomial", ["n","p"]],
                                     ["Geometric", ["p"]],
                                     ["Negative Binomial", ["p","r"]],
                                     ["Normal", ["mean","SD"]],
                                     ["Student t", ["degrees of freedom"]],
                                     ["Chi-square", ["degrees of freedom"]],
                                     ["Exponential", ["mean"]],
                                     ["Poisson", ["mean"]]
                                   ],
                    digits: 20
    };

// Extend options
    $.extend(this.options, params);

    self.lo = 0.0;
    self.hi = 0.0;

    function init() {
        var me = $('<div />').addClass('probCalc');
        self.container.append(me);
        // display
        self.theDisplay = $('<input type="text" readonly />').attr('size',self.options['digits']);
        me.append(self.theDisplay);
        // distribution selection
        self.selectDist = $('<select />');
        $.each(self.options['distributions'], function(i, v) {
               $('<option/>', { value : v[0] }).text(v[0]).appendTo(self.selectDist);
        })
        // parameters
        me.append(self.selectDist);
    }
    init();

//  action functions

    function buttonClick(v, opType) {
    }

}
