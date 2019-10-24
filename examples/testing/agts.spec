spec agts

landingLane,takeOffLane,c3, c4, c5,c6,c7,c8:lock;

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

process parkingAirplane(llane:lock, tlane:lock, freeLane:lock, freeLane2:lock, freeLane3:lock, freeLane4:lock, freeLane5:lock, freeLane6:lock){
	enum st={ Touchdown, Parked, Tax16lc3};
	init: this.st=Touchdown && 
          own(llane) && av(tlane) && av(freeLane) ;


    action getL1(){
        frame: freeLane;
        pre: (this.st=Touchdown) && own(llane) && av(freeLane);
        post:  own(freeLane);
    }
    
    action getL2(){
        frame: freeLane2;
        pre: (this.st=Touchdown) && own(llane) && av(freeLane2);
        post:  own(freeLane2);
    }
    
    action getL3(){
        frame: freeLane3;
        pre: (this.st=Touchdown) && own(llane) && av(freeLane3);
        post:  own(freeLane3);
    }
    
     action getL4(){
        frame: freeLane4;
        pre: (this.st=Touchdown) && own(llane) && av(freeLane4);
        post:  own(freeLane4);
    }
    
      action getL5(){
        frame: freeLane4;
        pre: (this.st=Touchdown) && own(llane) && av(freeLane5);
        post:  own(freeLane5);
    }
    
     action getL6(){
        frame: freeLane4;
        pre: (this.st=Touchdown) && own(llane) && av(freeLane6);
        post:  own(freeLane6);
    }
    
	action exitRW3(){
		frame: st, llane;
		/*pre: (this.st=Touchdown) && own(llane) && own(freeLane) && !own(freeLane2) && !own(freeLane3);*/
        pre: own(llane) && own(freeLane) && !own(freeLane2) && !own(freeLane3) && !own(freeLane4) && !own(freeLane5) && !own(freeLane6);
		post: (this.st=Tax16lc3) && av(llane);
	}
    
    action exitRW4(){
		frame: st, llane;
        pre: own(llane) && own(freeLane2) && !own(freeLane) && !own(freeLane3) && !own(freeLane4) && !own(freeLane5) 
        && !own(freeLane6);
		post: (this.st=Tax16lc3) && av(llane);
	}
    
     action exitRW5(){
		frame: st, llane;
        pre: own(llane) && !own(freeLane2) && !own(freeLane) && own(freeLane3) && !own(freeLane4) && !own(freeLane5) 
        && !own(freeLane6);
		post: (this.st=Tax16lc3) && av(llane);
	}
    
     action exitRW6(){
		frame: st, llane;
        pre: own(llane) && !own(freeLane2) && !own(freeLane) && !own(freeLane3) && own(freeLane4) && !own(freeLane5) 
        && !own(freeLane6);
		post: (this.st=Tax16lc3) && av(llane);
	}
   
    action exitRW7(){
		frame: st, llane;
        pre: own(llane) && !own(freeLane2) && !own(freeLane) && !own(freeLane3) && !own(freeLane4) && own(freeLane5) 
        && !own(freeLane6);
		post: (this.st=Tax16lc3) && av(llane);
	}
    
     action exitRW8(){
		frame: st, llane;
        pre: own(llane) && !own(freeLane2) && !own(freeLane) && !own(freeLane3) && !own(freeLane4) && !own(freeLane5) 
        && own(freeLane6);
		post: (this.st=Tax16lc3) && av(llane);
	}
   
    
    action crossRW3(){
        frame:st, freeLane;
        pre: (this.st=Tax16lc3) && av(tlane) && own(freeLane);
        post: (this.st=Parked) && av(freeLane);
    }
    
    action crossRW4(){
        frame:st, freeLane2;
        pre: (this.st=Tax16lc3) && av(tlane) && own(freeLane2);
        post: (this.st=Parked) && av(freeLane2);
    }
    
    action crossRW5(){
        frame:st, freeLane3;
        pre: (this.st=Tax16lc3) && av(tlane) && own(freeLane3);
        post: (this.st=Parked) && av(freeLane3);
    }
    
    action crossRW6(){
        frame:st, freeLane4;
        pre: (this.st=Tax16lc3) && av(tlane) && own(freeLane4);
        post: (this.st=Parked) && av(freeLane4);
    }
    
     action crossRW7(){
        frame:st, freeLane5;
        pre: (this.st=Tax16lc3) && av(tlane) && own(freeLane5);
        post: (this.st=Parked) && av(freeLane5);
    }
    
      action crossRW8(){
        frame:st, freeLane6;
        pre: (this.st=Tax16lc3) && av(tlane) && own(freeLane6);
        post: (this.st=Parked) && av(freeLane6);
    }
    
	invariant: AG[!(own(freeLane) && own(freeLane2))];
}

main(){
	p1:arrivingPlane;
    p2:parkingAirplane;
	run p1(landingLane);
    run p2(landingLane, takeOffLane, c3,c4,c5,c6,c7,c8);
}
property: AF[(p1.st=Touchdown)];