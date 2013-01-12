var nDoors = 3;
var doorsOpen = 0;
var doorLocations = new Array(nDoors);
//
function shufflePrize(){
// close all doors, pick prize location
    doorsOpen = 0;
    prizeDoor = Math.floor(3*rand.next());
    for (i=0; i < nDoors; i++){
        document.images[doorLocations[i]].src = "../Graphics/question.gif";
    }
    return(true);
}

function revealDoor(door){
    if (doorsOpen == 2) shufflePrize();
    else if (doorsOpen == 0){ // no door is open. Pick another door to show.
        var newDoor;
        if (door == prizeDoor){ // picked the right one. Show one of the others at random
            if (door == 0){newDoor = Math.floor(1 + 2*rand.next());}
            else if (door == 1){newDoor = 2*Math.floor(.5+rand.next());}
            else if (door == 2){newDoor = Math.floor(2*rand.next());}
        }
        else{ // picked the wrong one.  Show the empty.
            var sumDoors = door + prizeDoor;
            newDoor = 3 - sumDoors;
        }
        doorsOpen++;
        document.images[doorLocations[newDoor]].src = "../Graphics/answer_empty.gif";
    }
    else if (doorsOpen == 1) { // a door is open
        for (i=0; i < nDoors; i++){
                if (prizeDoor == i) document.images[doorLocations[i]].src = "../Graphics/answer_good.gif";
                else document.images[doorLocations[i]].src = "../Graphics/answer_empty.gif";
        }
        doorsOpen++;
    }
    return(true);
}
