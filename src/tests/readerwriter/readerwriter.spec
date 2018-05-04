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
	run w1();
	run r1();
}

property: AG[(!r1.reading || !w1.cs)]; 