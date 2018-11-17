spec readerwriter

r,read1:lock; /*r is the common resource*/

process writer{
	ncs, cs: boolean;
	init: this.ncs && !this.cs && !own(global.r) && !own(global.read1);

	action lockR(){
		frame: r;
		pre: this.ncs && av(global.r);
		post: own(global.r);
	}

	action enterCS(){
		frame: ncs, cs;
		pre: this.ncs && own(global.r) && av(global.read1);
		post: !this.ncs && this.cs;
	}

	action unlockR(){
		frame: ncs,cs,r;
		pre: this.cs && own(global.r);
		post: !this.cs && this.ncs && !own(global.r);
	}
	invariant: this.cs || !this.cs;

}

process reader (mylock: lock){
	reading:boolean;
	init: !this.reading && !own(global.r) && !own(mylock);

	action startRead(){
		frame: reading, mylock;
		pre: !this.reading && av(global.r) && av(mylock);
		post: this.reading && own(mylock);
	}

	action finishRead(){
		frame: reading, mylock;
		pre: this.reading && av(global.r) && own(mylock);
		post: !this.reading && !own(mylock);
	}
	invariant: this.reading || !this.reading;
}

main(){
	w1:writer;
	r1:reader;
	w2:writer;
	w3:writer;
	run w1();
	run r1(read1);
	run w2();
	run w3();
}

property: AG[(!r1.reading || !w1.cs) && (!r1.reading || !w2.cs) && (!r1.reading || !w3.cs)] 
&&
AG[(!w1.cs || !w2.cs) && (!w3.cs || !w2.cs) && (!w1.cs || !w3.cs)]; 