var euphyShutter = new Euphony();

function shutterStart() {

}

$(document).ready(function() {
    $("#shutter-button").bind("touchstart mousedown", function(e) {
        e.preventDefault();
        euphyShutter.initBuffers();
        euphyShutter.setFrequency(19500);
        euphyShutter.setVolume(100);
        euphyShutter.play();
        return false;
    });

    $("#shutter-button").bind("touchend mouseup", function(e) {
        e.preventDefault();
        euphyShutter.pause();
        return false;
    });


});

    
