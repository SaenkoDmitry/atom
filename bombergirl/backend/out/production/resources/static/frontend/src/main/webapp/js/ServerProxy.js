ServerProxy = Class.extend({

    host: "192.168.1.101:8085",

    socket: null,

    handler: {},

    init: function() {
        this.handler['REPLICA'] = gMessages.handleReplica;
        this.handler['POSSESS'] = gMessages.handlePossess;
        this.handler['END_MATCH'] = gMessages.handleEndMatch;

        var self = this;
        gInputEngine.subscribe('up', function() {
            self.socket.send(gMessages.move('up'))
        });
        gInputEngine.subscribe('down', function() {
            self.socket.send(gMessages.move('down'))
        });
        gInputEngine.subscribe('left', function() {
            self.socket.send(gMessages.move('left'))
        });
        gInputEngine.subscribe('right', function() {
            self.socket.send(gMessages.move('right'))
        });
        gInputEngine.subscribe('bomb', function() {
            self.socket.send(gMessages.plantBomb())
        });

        this.initSocket();
    },

    initSocket: function() {
        var self = this;
        this.socket = new WebSocket("ws://" + this.host + "/events");
        this.so

        this.socket.onopen = function() {
            //var token = Cookies.getResponseHeader("AUTHORIZATION");
            var token1 = this.socket.getResponseHeader("AUTHORIZATION");
            //var token2 = socket.getResponseHeader("AUTHORIZATION");

            //console.log("Connection established. Token = " + token);
            console.log("Connection established. Token = " + token1);
            //console.log("Connection established. Token = " + token2);
        };

        this.socket.onclose = function(event) {
            if (event.wasClean) {
                console.log('closed');
            } else {
                console.log('alert close');
            }
            console.log('Code: ' + event.code + ' cause: ' + event.reason);
        };

        this.socket.onmessage = function(event) {
            var msg = JSON.parse(event.data);
            if (self.handler[msg.topic] === undefined)
                return;

            self.handler[msg.topic](msg);
        };

        this.socket.onerror = function(error) {
            console.log("Error " + error.message);
        };
    }

});
