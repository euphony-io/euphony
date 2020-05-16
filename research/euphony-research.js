var euphy = new Euphony();

function setFrequency(e) {
    $("#frequency-viewer .view-panel").text(e.value + " hz");
    euphy.initBuffers();
    euphy.setFrequency(e.value);
}

function setVolume(e) {
    console.log("setVolume : " + e.value);
    $("#volume-viewer .view-panel").text(e.value + " / 100");
    $("#data-generator .view-panel").text(e.value + " / 100");
    euphy.setVolume(e.value);
}

function onGenerateDataBtn() {
    let data = $("#text-panel").val();
    console.log(data);

    switch(euphy.getState()) {
        case 0: // STOP 2 PLAY
            console.log("STOP 2 PLAY : " + data);
            $("#generate-btn").text("PAUSE");
            euphy.initBuffers();
            euphy.setCode(data);
            euphy.play(false);
            break;
        case 1: // PLAY 2 PAUSE
            console.log("PLAY 2 PAUSE : ");
            $("#generate-btn").text("PLAY");
            euphy.pause();
            break;
        case 2: // PAUSE 2 PLAY
            console.log("PAUSE 2 PLAY : " + data);
            $("#generate-btn").text("PAUSE");
            euphy.setCode(data);
            euphy.play(false);
            break;
        }
}

function onPlayBtn() {
    let freq = $("#frequencyRange").val();
    console.log(euphy.getState());
    switch(euphy.getState()) {
    case 0: // STOP 2 PLAY
        console.log("STOP 2 PLAY : " + freq);
        $("#play-btn").text("PAUSE");
        euphy.initBuffers();
        euphy.setFrequency(freq);
        euphy.play(false);
        break;
    case 1: // PLAY 2 PAUSE
        console.log("PLAY 2 PAUSE : ");
        $("#play-btn").text("PLAY");
        euphy.pause();
        break;
    case 2: // PAUSE 2 PLAY
        console.log("PAUSE 2 PLAY : " + freq);
        $("#play-btn").text("PAUSE");
        euphy.setFrequency(freq);
        euphy.play(false);
        break;
    }
}

function onStopBtn() {
    $("#play-btn").text("PLAY");
    euphy.stop();
}
$(document).ready(function() {
    setFrequency({ value : 400 });
    setVolume({ value : 50 });
});
