var euphy = new Euphony();
euphy.initChannel();
function generateSound() {
    console.log('good!');
    var code = document.getElementById("euphy_text");
    euphy.setCode(code.value);
    euphy.play();
}
