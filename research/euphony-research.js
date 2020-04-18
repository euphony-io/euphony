var euphy = new Euphony();

function setFrequency(e) {
    $("#frequency-viewer .view-panel").text(e.value + " hz");
    euphy.initBuffers();
    euphy.setFrequency(e.value);
}

function setVolume(e) {
    $("#volume-viewer .view-panel").text(e.value + " / 100");
}

function onPlayBtn() {
    if(euphy.getState() == 0){
        euphy.play(false);
        $("#play-btn").text("STOP");
    }
    else {
        euphy.stop();
        $("#play-btn").text("PLAY");
    }
}

$(document).ready(function() {
    setFrequency({ value : 400 });
    setVolume({ value : 50 });
});
