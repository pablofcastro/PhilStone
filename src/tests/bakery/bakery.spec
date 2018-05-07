spec mutex
m,turn,wait:boolean;
process p{
	cs:boolean;
	init : !this.cs && av(global.m) && av(global.turn) && av(global.wait);
	
	action pickTurnDirectly(){
		frame: turn;
		pre: !this.cs && av(global.turn) && av(global.wait) ;
		post: own(global.turn);
	}

	action pickTurnAfterWaiting(){
		frame: turn,wait;
		pre: !this.cs && av(global.turn) && own(global.wait) ;
		post: own(global.turn) && !own(global.wait);
	}

	action waitTurn(){
		frame: wait;
		pre: !this.cs && !own(global.turn) && av(global.wait);
		post: own(global.wait);
	}

	action getLock(){
		frame: turn,m;
		pre: !this.cs && own(global.turn) && av(global.m);
		post: !own(global.turn) && own(global.m);
	}

	action enterCS(){
		frame: cs;
		pre: !this.cs && own(global.m);
		post: this.cs ; 
	}

	action enterNCS(){
		frame: cs,m;
		pre:  this.cs;
		post: !this.cs && !own(global.m);
	}
		
	invariant: this.cs || !this.cs;

}

main(){
 p1:p;
 p2:p;
 run p1();
 run p2();
}


property: AG[!p1.cs || !p2.cs] ;
