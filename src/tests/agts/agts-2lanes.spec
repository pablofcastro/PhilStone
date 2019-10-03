spec agts

landingLane,takeOffLane,c3,c4:lock;

process airplane{
	enum st={ Arflow, Touchdown, Takeoff, Parked, Depflow, Tax16lc3, Tax16lc4 };
	init: this.st=Arflow && 
          av(global.landingLane) && av(global.takeOffLane) && (av(global.c3) || av(global.c4));

	action tryLand(){
		frame: st, landingLane;
		pre: (this.st=Arflow) && av(global.landingLane);
		post: (this.st=Touchdown) && own(global.landingLane);
	}
    
    action reqC3(){
        frame: c3;
        pre: av(global.c3);
        post: own(global.c3);
    }

	action exitRW3(){
		frame: st, c3, landingLane;
		pre: (this.st=Touchdown) && own(global.landingLane) && own(global.c3) && !own(global.c4);
		post: (this.st=Tax16lc3) && own(global.c3) && av(global.landingLane);
	}
    
    action crossRW3(){
        frame:st;
        pre: (this.st=Tax16lc3) && av(global.takeOffLane);
        post: (this.st=Parked);
    }
    
    action exitRW4(){
		frame: st, c4, landingLane;
		pre: (this.st=Touchdown) && own(global.landingLane) && own(global.c4) && !own(global.c3);
		post: (this.st=Tax16lc4) && own(global.c4) && av(global.landingLane);
	}
    
    action crossRW4(){
        frame:st;
        pre: (this.st=Tax16lc4) && av(global.takeOffLane);
        post: (this.st=Parked);
    }
    
    action reqTakeOff(){
        frame: st, takeOffLane;
        pre: (this.st=Parked) && av(global.takeOffLane);
        post: (this.st=Takeoff) && own(global.takeOffLane);
    }
    
    action leave(){
        frame: st, takeOffLane;
        pre: (this.st=Takeoff) && own(global.takeOffLane);
        post: (this.st=Depflow) && av(global.takeOffLane);
    }
    
	invariant: AG[!(own(global.c3) && own(global.c4))];
}

main(){
	p1:airplane;
    p2:airplane;
	run p1();
    run p2();
}

property: AG[(p1.st=Arflow) || !(p1.st=Arflow)];