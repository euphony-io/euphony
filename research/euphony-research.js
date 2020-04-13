var euphy = new Euphony();

function setFrequency(e) {
    $("#frequency-viewer .view-panel").text(e.value + " hz");
}

function setVolume(e) {
    $("#volume-viewer .view-panel").text(e.value + "/ 100");
}

$(document).ready(function() {
    console.log("I'm ready");
    setFrequency({ value : 400 });
    setVolume({ value : 50 });
});
