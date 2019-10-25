spec peterson
turn0, turn1, try1, try2, try3 : prim_boolean;
process proc1{
    cs:boolean;
    owns:try1;
	init: !this.cs  && !global.turn0 && !global.turn1 && !global.try3 && !global.try2 &&  !global.try1;

	
    action setTryTurn(){
		frame: turn0, turn1, try1;
		pre: !this.cs && !global.try1;
		post: !global.turn0 && !global.turn1 && global.try1;
	}

	action enterCS(){
		frame: cs;
		/*pre: (global.try1 && !this.cs && !global.turn) || (!global.try2 && !this.cs && global.try1);*/
        pre: global.try1 && !this.cs;
		post: this.cs;
	}
    action leaveCS(){
        frame: cs,  try1;
        pre: this.cs;
        post: !this.cs &&  !global.try1;    
    }
   
	/*invariant: EF[this.cs] && AG[!this.cs || EF[!this.cs]];*/
    invariant: EF[this.cs];
}


process proc2{
    cs:boolean;
    owns:try2;
	init: !this.cs && !global.turn0 && !global.turn1 && !global.try3 && !global.try2 && !global.try1;

     action setTryTurn(){
		frame: turn0, turn1, try2;
		pre: !this.cs && !global.try2;
		post: !global.turn0 && global.turn1 && global.try2;
	}

	action enterCS(){
		frame: cs;
		/*pre: (global.try2 && !this.cs && global.turn) || (!global.try1 && !this.cs && global.try2);*/
        pre: global.try2 && !this.cs;
		post: this.cs;
	}
    action leaveCS(){
        frame: cs,  try2;
        pre: this.cs;
        post: !this.cs &&  !global.try2;    
    }
   
	invariant: EF[this.cs]; /*&& AG[!this.cs || EF[!this.cs]];*/
}

process proc3{
    cs:boolean;
    owns:try3;
	init: !this.cs && !global.turn0 && !global.turn1 && !global.try3 && !global.try2 && !global.try1;

     action setTryTurn(){
		frame: turn0, turn1, try3;
		pre: !this.cs && !global.try3;
		post: global.turn0 && !global.turn1 && global.try3;
	}

	action enterCS(){
		frame: cs;
		/*pre: (global.try2 && !this.cs && global.turn) || (!global.try1 && !this.cs && global.try2);*/
        pre: global.try3 && !this.cs;
		post: this.cs;
	}
    action leaveCS(){
        frame: cs,  try3;
        pre: this.cs;
        post: !this.cs &&  !global.try3;    
    }
   
	invariant: EF[this.cs]; /*&& AG[!this.cs || EF[!this.cs]];*/
}

main(){
	p1:proc1;
    p2:proc2;
    p3:proc3;
	run p1();
    run p2();
    run p3();
}

property: AG[(!p1.cs || !p2.cs) && (!p1.cs || !p3.cs) && (!p2.cs || !p3.cs)] && AG[!global.try1 || AF[p1.cs]] && AG[!global.try2 || AF[p2.cs]] && AG[!global.try3 || AF[p3.cs]];
