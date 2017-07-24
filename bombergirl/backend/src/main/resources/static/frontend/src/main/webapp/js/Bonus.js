Bonus = Entity.extend({
    types: ['speed', 'bomb', 'fire'],

    id: null,
    type: '',
    position: {},
    bmp: null,

    size: {
        w: 32,
        h: 32
    },

    init: function(id, position, typePosition) {
        this.id = id;
        this.type = this.types[typePosition];

        this.position = position;

        this.bmp = new createjs.Bitmap(gGameEngine.bonusesImg);
        this.bmp.x = position.x;
        this.bmp.y = position.y;
        this.bmp.sourceRect = new createjs.Rectangle(typePosition * 32, 0, 32, 32);
        gGameEngine.stage.addChild(this.bmp);
    },

    update: function() {
    },

    // destroy: function() {
    //     gGameEngine.stage.removeChild(this.bmp);
    //     Utils.removeFromArray(gGameEngine.bonuses, this);
    // }

    remove: function() {
        gGameEngine.stage.removeChild(this.bmp);

        for (var i = 0; i < gGameEngine.bonuses.length; i++) {
            var bonus = gGameEngine.bonuses[i];
            if (this == bonus) {
                gGameEngine.bonuses.splice(i, 1);
            }
        }
    }
});