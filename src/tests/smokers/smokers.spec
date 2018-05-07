spec smokers

t,p,m:boolean;

process smoker (ingredient1: boolean, ingredient2: boolean){
	dummy:boolean;
	init: !own(global.t) && !own(global.p) && !own(global.m);

	action acquire(){ 
		frame: p, m;
		pre: av(ingredient1) && av(ingredient2);
		post: own(ingredient1) && own(ingredient2);
	}
	action smoke(){
		frame: p, m;
		pre: own(ingredient1) && own(ingredient2);
		post: !own(ingredient1) && !own(ingredient2);
	}
	invariant: this.dummy || !this.dummy;
}

/*
process tobaccoSmoker{
	dummy:boolean;
	init: !own(global.t) && !own(global.p) && !own(global.m);

	action acquire(){ 
		frame: p, m;
		pre: av(global.p) && av(global.m);
		post: own(global.p) && own(global.m);
	}
	action smoke(){
		frame: p, m;
		pre: own(global.p) && own(global.m);
		post: !own(global.p) && !own(global.m);
	}
	invariant: this.dummy || !this.dummy;
}

process paperSmoker{
	dummy:boolean;
	init: !own(global.t) && !own(global.p) && !own(global.m);

	action acquire(){
		frame: t, m;
		pre: av(global.t) && av(global.m);
		post: own(global.t) && own(global.m);
	}
	action smoke(){
		frame: t, m;
		pre: own(global.t) && own(global.m);
		post: !own(global.t) && !own(global.m);
	}
	invariant: this.dummy || !this.dummy;
}

process matchesSmoker{
	dummy:boolean;
	init: !own(global.t) && !own(global.p) && !own(global.m);

	action acquire(){
		frame: t, p;
		pre: av(global.t) && av(global.p);
		post: own(global.t) && own(global.p);
	}
	action smoke(){
		frame: t, p;
		pre: own(global.t) && own(global.p);
		post: !own(global.t) && !own(global.p);
	}
	invariant: this.dummy || !this.dummy;
}
*/
process agent{
	dummy:boolean;
	init: own(global.t) && own(global.p) && own(global.m);

	action putTobaccoPaper(){
		frame: t, p;
		pre: own(global.t) && own(global.p);
		post: !own(global.t) && !own(global.p);
	}
	action putTobaccoMatches(){
		frame: t, m;
		pre: own(global.t) && own(global.m);
		post: !own(global.t) && !own(global.m);
	}
	action putPaperMatches(){
		frame: p, m;
		pre: own(global.p) && own(global.m);
		post: !own(global.p) && !own(global.m);
	}
	action replenishTobaccoPaper(){
		frame: t, p;
		pre: av(global.t) && av(global.p);
		post: own(global.t) && own(global.p);
	}
	action replenishTobaccoMatches(){
		frame: t, m;
		pre: av(global.t) && av(global.m);
		post: own(global.t) && own(global.m);
	}
	action replenishPaperMatches(){
		frame: p, m;
		pre: av(global.p) && av(global.m);
		post: own(global.p) && own(global.m);
	}

	/*agent shouldn't replenish ingredients he just put on the table*/
	invariant:	AG[own(global.t) || AX[!own(global.t)]] && 
				AG[own(global.p) || AX[!own(global.p)]] && 
				AG[own(global.m) || AX[!own(global.m)]];

}

main(){
 ts:smoker;
 ps:smoker;
 ms:smoker;
 a:agent;
 run ts(m,p);
 run ps(t,m);
 run ms(t,p);
 run a();
}

property: True; /*no deadlock AG[AF[av(t) && av(p) && av(m)]]*/
