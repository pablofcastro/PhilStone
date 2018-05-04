spec readerwriter

r:boolean; /*common resource*/

process writer{
	ncs, cs: boolean;
	init: this.ncs && !own(global.r);

	action lockR(){
		frame: ncs, r;
		pre: this.ncs && av(global.r);
		post: this.ncs && own(global.r);
	}

	action enterCS(){
		frame: r, ncs, cs;
		pre: this.ncs && own(global.r);
		post: !this.ncs && this.cs;
	}

	action unlockR(){
		frame: ncs,cs,r;
		pre: this.cs && own(global.r);
		post: !this.cs && this.ncs && !own(global.r);
	}
	invariant: this.cs || !this.cs;

}

process reader{
	reading:boolean;
	init: !this.reading && !own(global.r);

	action startRead(){
		frame: reading, r;
		pre: !this.reading && av(global.r);
		post: this.reading;
	}

	action finishRead(){
		frame: reading, r;
		pre: this.reading && av(global.r);
		post: !this.reading;
	}
	invariant: this.reading || !this.reading;
}

main(){
	w1:writer;
	r1:reader;
	w2:writer;
	r2:reader;
	w3:writer;
	run w1();
	run r1();
	run w2();
	run r2();
	run w3();
}

property: AG[(!r1.reading || !w1.cs) && (!r1.reading || !w2.cs) && (!r1.reading || !w3.cs) && (!r2.reading || !w1.cs) && (!r2.reading || !w2.cs) && (!r2.reading || !w3.cs)] 
&&
AG[(!w1.cs || !w2.cs) && (!w3.cs || !w2.cs) && (!w1.cs || !w3.cs)]; 