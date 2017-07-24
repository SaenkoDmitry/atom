Fire = Entity.extend({
    id: null,
    /**
     * Entity position on map grid
     */
    position: {},

    /**
     * Bitmap dimensions
     */
    size: {
        w: 38,
        h: 38
    },

    /**
     * Bitmap animation
     */
    bmp: null,

    /**
     * The bomb that triggered this fire
     */

    init: function(id, position) {
        this.id = id;

        var spriteSheet = new createjs.SpriteSheet({
            images: [gGameEngine.fireImg],
            frames: { width: this.size.w, height: this.size.h, regX: 0, regY: 0 },
            animations: {
                idle: [0, 5, null, 0.4]
            }
        });
        this.bmp = new createjs.Sprite(spriteSheet);
        this.bmp.gotoAndPlay('idle');
        var that = this;
        this.bmp.addEventListener('animationend', function() {
            that.remove();
        });

        this.position = position;

        this.bmp.x = position.x + 2;
        this.bmp.y = position.y - 5;

        gGameEngine.stage.addChild(this.bmp);
    },

    update: function() {
    },

    remove: function() {
        gGameEngine.stage.removeChild(this.bmp);

        for (var i = 0; i < this.bomb.fires.length; i++) {
            var fire = this.bomb.fires[i];
            if (this == fire) {
                this.bomb.fires.splice(i, 1);
            }
        }

        for (var i = 0; i < gGameEngine.bombs.length; i++) {
            var bomb = gGameEngine.bombs[i];
            if (this.bomb == bomb) {
                gGameEngine.bombs.splice(i, 1);
            }
        }
    }
});