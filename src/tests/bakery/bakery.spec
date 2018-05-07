spec mutex
m,turn,wait:boolean;
process p{
	try, ncs, cs:boolean;
	init : this.ncs && !this.cs && !this.try && av(global.m) && av(global.turn) && av(global.wait);
	
	action enterTry(){
		frame: ncs,try,turn;
		pre: (this.ncs ) || (this.try && !own(global.turn)) ;
		post: this.try;
	}

	action pickTurnDirectly(){
		frame: try,turn,wait;
		pre: (this.try) && av(global.turn) && av(global.wait)  ;
		post: this.try && own(global.turn);
	}

	action pickTurnAfterWaiting(){
		frame: try,turn,wait;
		pre: (this.try) && av(global.turn) && own(global.wait) ;
		post: this.try && own(global.turn) && !own(global.wait);
	}

	action waitTurn(){
		frame: try,turn,wait;
		pre: (this.try)  && !own(global.turn) && av(global.wait);
		post: this.try && own(global.wait);
	}

	action getLock(){
		frame: try,turn,m;
		pre: this.try && own(global.turn) && av(global.m);
		post: this.try && !own(global.turn) && own(global.m);
	}

	action enterCS(){
		frame: try,cs,m;
		pre: this.try &&  own(global.m);
		post: this.cs && own(global.m); 
	}

	action enterNCS(){
		frame: cs,ncs,m;
		pre:  this.cs;
		post: this.ncs && (!own(global.m));
	}
		
	invariant: AG[!(this.ncs && this.try)&&!(this.try&&this.cs)&&!(this.ncs&&this.cs)];

}

main(){
 p1:p;
 run p1();
}

property: AG[p1.cs || !p1.cs];
