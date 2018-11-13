spec peterson
try2: boolean;
turn: boolean;
process proc1{
    cs,try1:boolean;
	init: !this.cs  && av(global.turn) && !global.turn && !this.try1 &&  !global.try2;

	
    action lockturn(){
		frame: turn;
		pre: !this.cs && av(global.turn) && !this.try1;
		post: own(global.turn);
	}
	action setTryTurn(){
		frame: turn, try1;
		pre: (!this.cs) && own(global.turn) && !this.try1;
		post: global.turn && this.try1 && av(global.turn);
	}
	action enterCS(){
		frame: cs;
		pre: (this.try1 && !this.cs && !global.turn) || (!global.try2 && !this.cs && this.try1);
		post: this.cs;
	}
    action leaveCS(){
        frame: cs,  try1;
        pre: this.cs;
        post: !this.cs &&  !this.try1;    
    }
   
	invariant: EF[this.cs] && AG[!this.cs || EF[!this.cs]];
}

main(){
	p1:proc1;
	run p1();
}

property: AG[!p1.cs || p1.cs] ;