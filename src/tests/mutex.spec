spec mutex
global m:boolean;

process p{
	try, ncs, cs:boolean;
	init : ncs && !try && !cs;
	
	action enterTry(){
		frame: ncs, try;
		pre: ncs && !try;
		post: try && !ncs;
	}	
	action getMutex(){
		frame: m;
		pre: av(m);
		post: try && own(s);
	}
	action enterNCS(){
		frame: cs, m, ncs;
		pre:cs;
		post: ncs && !own(s) && !cs;
	}
	action enterCS(){
		frame: try,  cs;
		pre: try && own(s) && !cs;
		post: cs && own(s) && !try;
	}		
	invariant: True;
}


main(){
 p1:p;
 p2:p;	
}
