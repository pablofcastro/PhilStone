spec agts

landingLane,takeOffLane,c3,c4,c5,c6c7,c8:lock;

process airplane{
	enum st={Arflow, Touchdown, Takeoff, Parked, Depflow,Tax16lc3,  Tax16lc4, Tax16lc5, Tax16lc6c7, Tax16lc8, Tax16lb2, Tax16lb7, Tax16lb8,Tax16lb9, Tax16lb10, Tax16lb11};
	init: this.st=Arflow && 
          av(global.landingLane) && av(global.takeOffLane) && av(global.c3) && av(global.c4) && av(global.c5)  && av(global.c6c7) && av(global.c8);

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
    
    action exitRW4(){
		frame: st, c4, landingLane;
		pre: (this.st=Touchdown) && av(global.c4);
		post: (this.st=Tax16lc4) && own(global.c4);
	}
    
    action exitRW5(){
		frame: st, c5, landingLane;
		pre: (this.st=Touchdown) && av(global.c5);
		post: (this.st=Tax16lc5) && own(global.c5);
	}
    
   
    
    action exitRW7(){
		frame: st, c6c7, landingLane;
		pre: (this.st=Touchdown) && av(global.c6c7);
		post: (this.st=Tax16lc6c7) && own(global.c6c7);
	}
    
     action exitRW8(){
		frame: st, c8, landingLane;
		pre: (this.st=Touchdown) && av(global.c8);
		post: (this.st=Tax16lc8) && own(global.c8);
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
    
	invariant: AG[(this.st=Parked) || !(this.st=Parked)];

}

main(){
	p1:airplane;
    p2:airplane;
	run p1();
    run p2();
}

property: AG[(p1.st=Arflow) || !(p1.st=Arflow)];