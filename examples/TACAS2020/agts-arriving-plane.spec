spec agts

landingLane,takeOffLane,c3, c4:lock;

process arrivingPlane(lane:lock){
    enum st={Arflow, Touchdown};
    init: this.st=Arflow && av(global.lane);
    
    action tryLand(){
		frame: st, lane;
		pre: (this.st=Arflow) && av(global.lane);
		post: (this.st=Touchdown) && own(global.lane);
	}
    
    invariant: AG[!own(global.lane) || own(global.lane)];
}

process parkingAirplane(llane:lock, tlane:lock, freeLane:lock){
	enum st={ Touchdown, Parked, Tax16lc3};
	init: this.st=Touchdown && 
          own(llane) && av(tlane) && av(freeLane) ;


    action getL1(){
        frame: freeLane;
        pre: (this.st=Touchdown) && own(llane) && av(freeLane);
        post:  own(freeLane);
    }
    
	action exitRW3(){
		frame: st, llane;
		/*pre: (this.st=Touchdown) && own(llane) && own(freeLane);*/
        pre: own(llane) && own(freeLane);
		post: (this.st=Tax16lc3) && av(llane);
	}
    
    action crossRW3(){
        frame:st, freeLane;
        pre: (this.st=Tax16lc3) && av(tlane) && own(freeLane);
        post: (this.st=Parked) && av(freeLane);
    }
    
	invariant: AG[!own(freeLane) || own(freeLane)];
}

main(){
	p1:arrivingPlane;
    p2:parkingAirplane;
	run p1(landingLane);
    run p2(landingLane, takeOffLane, c3);
}
property: AF[(p1.st=Touchdown)];