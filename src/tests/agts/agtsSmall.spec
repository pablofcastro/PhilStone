spec agts

landingLane,takeOffLane,c3,c4,c5,c6,c7,c8:lock;

process airplane{
	arflow, touchdown, takeoff, parked, depflow,tax16lc3,  tax16lc4, tax16lc5, tax16lc6,tax16lc7, tax16lc8, tax16lb2, tax16lb7, tax16lb8,tax16lb9, tax16lb10, tax16lb11: boolean;
	init: !this.parked && !this.touchdown && !this.depflow && this.arflow && !this.takeoff && !this.tax16lc3 && !this.tax16lc4 && !this.tax16lc5 && !this.tax16lc6 && !this.tax16lc7 && !this.tax16lc8 &&
          av(global.landingLane) && av(global.takeOffLane) && av(global.c3) && av(global.c4) && av(global.c5) && av(global.c6) && av(global.c7) && av(global.c8);

	action tryLand(){
		frame: arflow, touchdown, landingLane;
		pre: this.arflow && av(global.landingLane);
		post: this.touchdown && own(global.landingLane);
	}

	action exitRW3(){
		frame: touchdown, c3, landingLane;
		pre: this.touchdown && av(global.c3);
		post: this.tax16lc3 && own(global.c3);
	}
    
    action exitRW4(){
		frame: touchdown, c4, landingLane;
		pre: this.touchdown && av(global.c4);
		post: this.tax16lc4 && own(global.c4);
	}
    
    action exitRW5(){
		frame: touchdown, c5, landingLane;
		pre: this.touchdown && av(global.c5);
		post: this.tax16lc5 && own(global.c5);
	}
    
    action exitRW6(){
		frame: touchdown, c6, landingLane;
		pre: this.touchdown && av(global.c6);
		post: this.tax16lc6 && own(global.c6);
	}
    
    action exitRW7(){
		frame: touchdown, c7, landingLane;
		pre: this.touchdown && av(global.c7);
		post: this.tax16lc7 && own(global.c7);
	}
    
     action exitRW8(){
		frame: touchdown, c8, landingLane;
		pre: this.touchdown && av(global.c8);
		post: this.tax16lc8 && own(global.c8);
	}


    action reqTakeOff(){
        frame: parked, takeOffLane, takeoff;
        pre: this.parked && av(global.takeOffLane);
        post: this.takeoff && own(global.takeOffLane);
    }
    
    action leave(){
        frame: takeoff, takeOffLane, depflow;
        pre: this.takeoff && own(global.takeOffLane);
        post: this.depflow && av(global.takeOffLane);
    }
    
	invariant: AG[!(this.arflow && this.takeoff) && !(this.takeoff && this.parked) && !(this.parked && this.depflow) && !(this.arflow && this.parked) && !(this.arflow && this.depflow)];

}

main(){
	p1:airplane;
    p2:airplane;
	run p1();
    run p2();
}

property: AG[p1.arflow || !p1.arflow];