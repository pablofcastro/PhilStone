spec peterson

try0,try1:boolean;
turn:boolean;

process proc0 {
	cs:boolean;
	init: !this.cs && !own(global.try0);

	action locktry0(){
		frame: try0;
		pre: !this.cs && !own(global.try0);
		post: own(global.try0);
	}
	action unlocktry0(){
		frame: try0,cs;
		pre: this.cs && own(global.try0);
		post: !this.cs && !own(global.try0);
	}
	action turnOn(){
		frame: turn;
		pre: !this.cs && own(global.try0);
		post: global.turn;
	}
	action enterCS(){
		frame: cs;
		pre: (av(global.try1) && !this.cs) || (!global.turn && !this.cs);
		post: this.cs;
	}
	invariant: this.cs || !this.cs;
}

process proc1{
	cs:boolean;
	init: !this.cs && !own(global.try1);

	action locktry1(){
		frame: try1;
		pre: !this.cs && !own(global.try1);
		post: own(global.try1);
	}
	action unlocktry1(){
		frame: try1,cs;
		pre: this.cs && own(global.try1);
		post: !this.cs && !own(global.try1);
	}
	action turnOn(){
		frame: turn;
		pre: !this.cs && own(global.try1);
		post: !global.turn;
	}
	action enterCS(){
		frame: cs;
		pre: (av(global.try0) && !this.cs) || (global.turn && !this.cs);
		post: this.cs;
	}
	invariant: this.cs || !this.cs;
}

main(){
	p0:proc0;
	p1:proc1;
	run p0();
	run p1();
}

property: AG[!p0.cs || !p1.cs] && 
		  AG[av(global.try0) || AF[p0.cs]] &&
		  AG[av(global.try1) || AF[p1.cs]];