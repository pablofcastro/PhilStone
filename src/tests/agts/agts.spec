spec agts

landingLane,takeOffLane:boolean;

process airplane{
	ia,tl,l,tc,c,tcr,cr,tb,b,p,tt,t: boolean;
	init: this.ia && !this.p && !this.tl && !this.l && !this.tc && !this.c
	&& !this.tcr && !this.cr && !this.tb && !this.b && !this.tt && !this.t && !own(global.landingLane) && !own(global.takeOffLane);

	action tryLand(){
		frame: ia, tl;
		pre: this.ia;
		post: !this.ia && this.tl;
	}

	action land(){
		frame: tl,l, landingLane;
		pre: this.tl && av(global.landingLane);
		post: !this.tl && this.l && this.tl && own(global.landingLane);
	}

	action tryC(){
		frame: l,tc;
		pre: this.l;
		post: !this.l && this.tc;
	}

	action c(){
		frame: tc,c;
		pre: this.tc;
		post: !this.tc && this.c;
	}

	action tryCross(){
		frame: c,tcr,landingLane;
		pre: this.c;
		post: !this.c && this.tcr && own(global.landingLane) && own(global.takeOffLane);
	}

	action cross(){
		frame: tcr,cr,landingLane;
		pre: this.tcr;
		post: !this.tcr && this.cr && !own(global.landingLane);
	}

	action tryB(){
		frame: cr,tb;
		pre: this.cr;
		post: !this.cr && this.tb;
	}

	action b(){
		frame: tb,b;
		pre: this.tb;
		post: !this.tb && this.b;
	}

	action park(){
		frame: b,p,takeOffLane;
		pre: this.b;
		post: !this.b && this.p && !own(global.takeOffLane);
	}

	action tryTakeOff(){
		frame: p,tt,takeOffLane;
		pre: this.p;
		post: !this.p && this.tt && own(global.takeOffLane);
	}
	action takeOff(){
		frame: tt,t;
		pre: this.tt;
		post: !this.tt && this.t;
	}
	action inAir(){
		frame: t,ia,takeOffLane;
		pre: this.t;
		post: !this.t && this.ia && !own(global.takeOffLane);
	}
	invariant: this.ia || !this.ia;

}

main(){
	p1:airplane;
	p2:airplane;
	run p1();
	run p2();
}

property: /*mutual exclusion*/
		  /*AG[(!p1.l && !p1.tc) || (!p2.l && !p2.tc)] &&
		  AG[(!p1.c && !p1.tcr && !p1.cr && !p1.tb && !p1.t) || (!p2.c && !p2.tcr && !p2.cr && !p2.tb && !p2.t)] &&
		  AG[!p1.b || !p2.b]; */

		  /*equivalent property*/
		  !EF[(p1.l || p1.tc) && (p2.l || p2.tc)] &&
		  !EF[(p1.c || p1.tcr || p1.cr || p1.tb || p1.t) && (p2.c || p2.tcr || p2.cr || p2.tb || p2.t)] &&
		  !EF[!p1.b || !p2.b];

