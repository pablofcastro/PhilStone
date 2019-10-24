spec philosophers

f1, f2, f3, f4: lock;

process phil(left:lock, right:lock) {
	
	enum st = {Thinking, Hungry, Eating};
    init: (this.st=Thinking) && av(left) && av(right); 

	action	startThinking () {
				frame: st, left, right;
				pre: this.st = Eating;
				post: this.st=Thinking && av(left) && av(right);
			}

	action	becomeHunrgy () {
				frame: st;
				pre: this.st = Thinking;
				post: this.st=Hungry;
			}

	action	getLeft () {
				frame: left;
				pre: av(left) && this.st=Hungry;
				post: own(left);
			}

	action	getRight () {
				frame: right;
				pre: av(right) && this.st=Hungry;
				post: own(right);
			}

	action	eat () {
				frame: st;
				pre: this.st=Hungry && own(left) && own(right);
				post: this.st=Eating ;
			}
    invariant: EF[this.st=Eating];
}

main(){
    phil1:phil;
    phil2:phil;
    phil3:phil;
    phil4:phil;
    run phil1(f1,f2);
    run phil2(f2,f3); 
    run phil3(f3,f4);
    run phil4(f4,f1);
} 

/* Temporal Spec */

property: !EF[phil1.own(left) && phil2.own(left) && phil3.own(left) && phil4.own(left)] && !EF[phil1.own(right) && phil2.own(right) && phil3.own(right) && phil4.own(right)];
