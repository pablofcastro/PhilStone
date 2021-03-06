spec peterson
try1, try2: prim_boolean;
turn: boolean;
process proc1{
    cs:boolean;
    owns:try1;
	init: !this.cs  && av(global.turn) && !global.turn && !global.try1 &&  !global.try2;

	
    action lockturn(){
		frame: turn;
		pre: !this.cs && av(global.turn) && !global.try1;
		post: own(global.turn);
	}
	action setTryTurn(){
		frame: turn, try1;
		pre: (!this.cs) && own(global.turn);
		post: global.turn && global.try1 && av(global.turn);
	}
	action enterCS(){
		frame: cs;
		pre: (global.try1 && !this.cs && !global.turn) || (!global.try2 && !this.cs && global.try1);
		post: this.cs;
	}
    action leaveCS(){
        frame: cs,  try1;
        pre: this.cs;
        post: !this.cs &&  !global.try1;    
    }
   
	invariant: EF[this.cs] && AG[!this.cs || EF[!this.cs]];
}


process proc2{
    cs:boolean;
    owns:try2;
	init: !this.cs &&  av(global.turn) && !global.turn && !global.try2 && !global.try1;

    action lockturn(){
		frame: turn;
		pre: !this.cs && av(global.turn) && !global.try2;
		post: own(global.turn);
	}
	action setTryTurn(){
		frame: turn, try2;
		pre: (!this.cs) && own(global.turn)  && !global.try2;
		post: !global.turn && global.try2 && av(global.turn);
	}
	action enterCS(){
		frame: cs;
		pre: (global.try2 && !this.cs && global.turn) || (!global.try1 && !this.cs && global.try2);
		post: this.cs;
	}
    action leaveCS(){
        frame: cs,  try2;
        pre: this.cs;
        post: !this.cs &&  !global.try2;    
    }
   
	invariant: EF[this.cs] && AG[!this.cs || EF[!this.cs]];
}


main(){
	p1:proc1;
    p2:proc2;
	run p1();
    run p2();
}

property: AG[!p1.cs || !p2.cs] && AG[!global.try1 || AF[p1.cs]] && AG[!global.try2 || AF[p2.cs]];
