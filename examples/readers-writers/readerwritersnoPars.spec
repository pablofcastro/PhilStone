spec readerwriter

r,readerlock:lock; /*r is the common resource*/

process writer{
	ncs, cs: boolean;
	init: this.ncs && !this.cs && !own(global.r) && !own(global.readerlock);

	action lockR(){
		frame: r;
		pre: this.ncs && av(global.r);
		post: own(global.r);
	}

	action enterCS(){
		frame: ncs, cs;
		pre: this.ncs && own(global.r) && av(global.readerlock);
		post: !this.ncs && this.cs;
	}

	action unlockR(){
		frame: ncs,cs,r;
		pre: this.cs && own(global.r);
		post: !this.cs && this.ncs && !own(global.r);
	}
	invariant: AG[!own(readerlock)];

}

process reader{
	reading:boolean;
	init: !this.reading && !own(global.r) && !own(readerlock);

	action startRead(){
		frame: reading, readerlock;
		pre: !this.reading && av(r) && av(readerlock);
		post: this.reading && own(readerlock);
	}

	action finishRead(){
		frame: reading, readerlock;
		pre: this.reading && av(global.r) && own(readerlock);
		post: !this.reading && !own(readerlock);
	}
	invariant: this.reading || !this.reading;
}

main(){
	w1:writer;
	r1:reader;
	run w1();
	run r1();
}

property: AG[!(r1.reading && w1.cs)]; 