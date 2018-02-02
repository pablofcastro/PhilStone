spec mutex
m:boolean;
process p{
	try, ncs, cs:boolean;
	init : this.ncs && !this.cs && !this.try && av(global.m);
	
	action enterTry(){
		frame: ncs, try;
		pre: (this.ncs ) || (this.try && !own(global.m)) ;
		post: this.try;
	}	
	
	action enterCS(){
		frame: try, m, cs;
		pre: this.try &&  own(global.m);
		post: this.cs && own(global.m); 
	}

	action enterNCS(){
		frame: cs, ncs, m;
		pre:  this.cs;
		post: this.ncs && (!own(global.m));
	}
	action getLock(){
		frame: m;
		pre: this.try && av(global.m);
		post: this.try && own(global.m);
	}	
	invariant: AG[!(this.ncs && this.try)&&!(this.try&&this.cs)&&!(this.ncs&&this.cs)];
}

main(){
 p1:p;
 p2:p;
 p3:p;
 run p1();
 run p2();
 run p3();
}

property: AG[(!p1.cs && !p2.cs) || (!p2.cs && !p3.cs) || (!p2.cs && !p3.cs)];
