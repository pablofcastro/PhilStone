spec agts

landingLane,takeOffLane,c3,c4:lock;

process airplane{
	enum st={Arflow, Touchdown, Takeoff, Parked, Depflow,Tax16lc3,  Tax16lb2, Tax16lc4};
	init: this.st=Parked && 
          av(global.landingLane) && av(global.takeOffLane) && !own(global.c3) && !own(global.c4);

	action tryLand(){
		frame: st, landingLane;
		pre: (this.st=Arflow) && av(global.landingLane);
		post: (this.st=Touchdown) && own(global.landingLane);
	}

	action exitRW3(){
		frame: st, c3, landingLane;
		pre: (this.st=Touchdown) && av(global.c3);
		post: (this.st=Tax16lc3) && own(global.c3);
	}
    
    action crossRW3(){
        frame:st;
        pre: (this.st=Tax16lc3) && av(global.takeOffLane);
        post: (this.st=Tax16lb2);
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
    
	invariant: AG[this.st = this.st];

}

main(){
	p1:airplane;
    p2:airplane;
	run p1();
    run p2();
}

property: AG[(p1.st=Arflow) || !(p1.st=Arflow)];