Messages = Class.extend({
    handler: {},

    init: function () {
        this.handler['Pawn'] = this.handlePawn;
        this.handler['Bomb'] = this.handleBomb;
        this.handler['Wood'] = this.handleTile;
        this.handler['Wall'] = this.handleTile;
        this.handler['Fire'] = this.handleFire;
        this.handler['BonusFire'] = this.handleBonus;
        this.handler['BonusBomb'] = this.handleBonus;
        this.handler['BonusSpeed'] = this.handleBonus;
    },

    move: function (direction) {
        var template = {
            topic: "MOVE",
            data: {}
        };

        template.data.direction = direction.toUpperCase();
        return JSON.stringify(template);
    },

    plantBomb: function () {
        var template = {
            topic: "PLANT_BOMB",
            data: {}
        };

        return JSON.stringify(template);
    },


    handleReplica: function (msg) {
        var gameObjects = JSON.parse(msg.data);

        for (var i = 0; i < gameObjects.length; i++) {
            var obj = gameObjects[i];
            if (gMessages.handler[obj.type] === undefined)
                continue;

            gMessages.handler[obj.type](obj);
        }
    },

    handlePossess: function (msg) {
        console.log('handlePossess');
        console.log('msg.data', msg.data);
        console.log('gGameEngine', gGameEngine.stage);
        gInputEngine.possessed = parseInt(msg.data);
    },

    handleEndMatch: function (msg) {
        var res = JSON.parse(msg.data).gameEnd;

        if (res === 0) {
            alert("You lose. Try again?");
            gGameEngine.restart();
        }
        else if (res === 1) {
            alert("You win! Are you wanna get it again?");
            gGameEngine.restart();
        }
    },

    handlePawn: function(obj) {
        var player = gGameEngine.players.find(function (el) {
            return el.id === obj.id;
        });
        var position = Utils.getEntityPosition(obj.position);

        if (player) {
            player.bmp.x = position.x;
            player.bmp.y = position.y;
        } else {
            console.log(new Date().getTime() + " handle new player " + obj.id);
            player = new Player(obj.id, position);
            gGameEngine.players.push(player);
        }
    },

    handleBomb: function(obj) {
        var bomb = gGameEngine.bombs.find(function (el) {
            return el.id === obj.id;
        });
        var position = Utils.getEntityPosition(obj.position);

        if (bomb) {
            bomb.bmp.x = position.x;
            bomb.bmp.y = position.y;
        } else {
            bomb = new Bomb(obj.id, position);
            gGameEngine.bombs.push(bomb);
        }
    },

    handleTile: function(obj) {
        var tile = gGameEngine.tiles.find(function (el) {
            return el.id === obj.id;
        });
        var position = Utils.getEntityPosition(obj.position);
        if (tile) {
            tile.bmp.x = position.x;
            tile.bmp.y = position.y;
        } else {
            tile = new Tile(obj.id, obj.type, position);
            gGameEngine.tiles.push(tile);
        }
    },

    handleFire: function (obj) {
        var fire = gGameEngine.fires.find(function (el) {
            return el.id === obj.id;
        });
        var position = Utils.getEntityPosition(obj.position);
        if (fire) {
            fire.bmp.x = position.x;
            fire.bmp.y = position.y;
        } else {
            fire = new Fire(obj.id, position);
            gGameEngine.fires.push(fire);
        }
    },

    handleBonus: function (obj) {
        var bonus = gGameEngine.bonuses.find(function (el) {
            return el.id === obj.id;
        });
        var position = Utils.getEntityPosition(obj.position);
        if (bonus) {
            bonus.bmp.x = position.x;
            bonus.bmp.y = position.y;
        } else {
            if (obj.type === "BonusSpeed")
                bonus = new Bonus(obj.id, position, 0);
            else if (obj.type === "BonusBomb")
                bonus = new Bonus(obj.id, position, 1);
            else if (obj.type === "BonusFire")
                bonus = new Bonus(obj.id, position, 2);
            gGameEngine.bonuses.push(bonus);
        }
    },

    handleBonusBomb: function (obj) {
        var bonus = gGameEngine.bonuses.find(function (el) {
            return el.id === obj.id;
        });
        var position = Utils.getEntityPosition(obj.position);
        if (bonus) {
            bonus.bmp.x = position.x;
            bonus.bmp.y = position.y;
        } else {
            bonus = new Bonus(position, 1);
            gGameEngine.bonuses.push(bonus);
        }
    },

    handleBonusFire: function (obj) {
        var bonus = gGameEngine.bonuses.find(function (el) {
            return el.id === obj.id;
        });
        var position = Utils.getEntityPosition(obj.position);
        if (bonus) {
            bonus.bmp.x = position.x;
            bonus.bmp.y = position.y;
        } else {
            bonus = new Bonus(position, 2);
            gGameEngine.bonuses.push(bonus);
        }
    }

});

gMessages = new Messages();