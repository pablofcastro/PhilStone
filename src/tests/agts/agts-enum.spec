spec agts

landingLane,takeOffLane,c3,c4,c5,c6,c7,c8,b2, b7, b9, b10, b11:lock;

process airplane{
    enum st = {Arflow, Touchdown, Takeoff, Parked, Depflow, Tax16lc3,  Tax16lc4, Tax16lc5, Tax16lc6, Tax16lc7, Tax16lc8, Tax16lb2, Tax16lb7, Tax16lb8,Tax16lb9, Tax16lb10, Tax16lb11};
	init: (this.st = Parked) &&
          !own(global.landingLane) && !own(global.takeOffLane) && !own(global.c3) && !own(global.c4) && !own(global.c5) && !own(global.c6) && !own(global.c7) && !own(global.c8)
          && !own(global.b2) && !own(global.b7) && !own(global.b9) && !own(global.b10) && !own(global.b11);

	action tryLand(){
		frame: st, landingLane;
		pre: this.st = Arflow && av(global.landingLane);
		post: this.st = Touchdown && own(global.landingLane);
	}

	action exitRW3(){
		frame: st, c3, landingLane;
		pre: (this.st = Touchdown) && av(global.c3);
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
    
    action exitRW6(){
		frame: st, c6, landingLane;
		pre: (this.st=Touchdown) && av(global.c6);
		post: (this.st=Tax16lc6) && own(global.c6);
	}
    
    action exitRW7(){
		frame: st, c7, landingLane;
		pre: (this.st=Touchdown) && av(global.c7);
		post: (this.st=Tax16lc7) && own(global.c7);
	}
    
     action exitRW8(){
		frame: st, c8, landingLane;
		pre: (this.st=Touchdown) && av(global.c8);
		post: (this.st=Tax16lc8) && own(global.c8);
	}

    action crossRW3(){
        frame:st, b2;
        pre: (this.st=Tax16lc3) && av(global.takeOffLane) && av(global.b2);
        post: (this.st=Tax16lb2) && own(global.b2);
    }
    
    action crossRW4(){
        frame:st,  b7;
        pre: (this.st=Tax16lc4) && av(global.takeOffLane) && av(global.b7);
        post: (this.st=Tax16lb7) && own(global.b7);
    }
    
    action crossRW5(){
        frame:st, b9;
        pre: (this.st=Tax16lc5) && av(global.takeOffLane) && av(global.b9);
        post: (this.st=Tax16lb9) && own(global.b9);
    }
    
    action crossRW6(){
        frame:st, b10;
        pre: (this.st=Tax16lc6) && av(global.takeOffLane) && av(global.b10);
        post: (this.st=Tax16lb10) && own(global.b10);
    }
    
    action crossRW7(){
        frame:st, b10;
        pre: (this.st=Tax16lc7) && av(global.takeOffLane) && av(global.b10);
        post: (this.st=Tax16lb10) && own(global.b10);
    }
    
    action crossRW8(){
        frame:st, b11;
        pre: (this.st=Tax16lc8) && av(global.takeOffLane) && av(global.b11);
        post: (this.st=Tax16lb11) && own(global.b11);
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
    
	invariant: AG[(this.st=Depflow) || !(this.st=Depflow)];

}

main(){
	p1:airplane;
    p2:airplane;
	run p1();
    run p2();
}

property: AG[(p1.st=Arflow) || !(p1.st=Arflow)];