var euphyTone = new Euphony();
var euphyData = new Euphony();

function setFrequency(e) {
    $("#frequency-viewer .view-panel").text(e.value + " hz");
    euphyTone.initBuffers();
    euphyTone.setFrequency(e.value);
}

function setToneVolume(e) {
    console.log("setVolume : " + e.value);
    $("#volume-viewer .view-panel").text(e.value + " / 100");
    euphyTone.setVolume(e.value);
}

function setDataVolume(e) {
    console.log("setVolume : " + e.value);
    $("#data-generator .view-panel").text(e.value + " / 100");
    euphyData.setVolume(e.value);    
}

function onGenerateDataBtn() {
    let data = $("#text-panel").val();
    console.log(data);

    switch(euphyData.getState()) {
        case 0: // STOP 2 PLAY
            console.log("STOP 2 PLAY : " + data);
            $("#generate-btn").text("PAUSE");
            euphyData.initBuffers();
            euphyData.setCode(data);
            euphyData.play();
            break;
        case 1: // PLAY 2 PAUSE
            console.log("PLAY 2 PAUSE : ");
            $("#generate-btn").text("PLAY");
            euphyData.pause();
            break;
        case 2: // PAUSE 2 PLAY
            console.log("PAUSE 2 PLAY : " + data);
            $("#generate-btn").text("PAUSE");
        euphyData.initBuffers();
        euphyData.setCode(data);
            euphyData.play();
            break;
        }
}

function onPlayBtn() {
    let freq = $("#frequencyRange").val();
    console.log(euphyTone.getState());
    switch(euphyTone.getState()) {
    case 0: // STOP 2 PLAY
        console.log("STOP 2 PLAY : " + freq);
        $("#play-btn").text("PAUSE");
        euphyTone.initBuffers();
        euphyTone.setFrequency(freq);
        euphyTone.play();
        break;
    case 1: // PLAY 2 PAUSE
        console.log("PLAY 2 PAUSE : ");
        $("#play-btn").text("PLAY");
        euphyTone.pause();
        break;
    case 2: // PAUSE 2 PLAY
        console.log("PAUSE 2 PLAY : " + freq);
        $("#play-btn").text("PAUSE");
        euphyTone.setFrequency(freq);
        euphyTone.play();
        break;
    }
}

function onStopBtn() {
    $("#play-btn").text("PLAY");
    euphyTone.stop();
}
$(document).ready(function() {
    setFrequency({ value : 400 });
    setToneVolume({ value : 50 });
    setDataVolume({ value : 50 });
});
