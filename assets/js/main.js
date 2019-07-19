var euphy = new Euphony();
euphy.initChannel();
var btnSwt = false;
function generateSound() {
    if(btnSwt) {
        euphy.stop();
        document.getElementById("euphy_btn").value = "broadcast";
    }
    else {
        var code = document.getElementById("euphy_text");
        euphy.setCode(code.value);
        euphy.play();
        document.getElementById("euphy_btn").value = "stop";
    }


}
