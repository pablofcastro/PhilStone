spec philosophers

f1, f2: boolean;

process phil(left:boolean, right:boolean) {
	
	thinking, hungry, eating: boolean;
    init: this.thinking && !this.hungry && !this.eating && av(left) && av(right); 

	action	startThinking () {
				frame: eating, thinking, left, right;
				pre: this.eating;
				post: this.thinking && !this.eating && av(left) && av(right);
			}

	action	becomeHunrgy () {
				frame: thinking, hungry;
				pre: this.thinking;
				post: this.hungry && !this.thinking;
			}

	action	getLeft () {
				frame: left;
				pre: av(left) && this.hungry;
				post: own(left);
			}

	action	getRight () {
				frame: right;
				pre: av(right) && this.hungry;
				post: own(right);
			}

	action	eat () {
				frame: eating, hungry;
				pre: this.hungry && own(left) && own(right);
				post: this.eating && !this.hungry;
			}
    invariant: AG[!(this.thinking && this.hungry)&&!(this.thinking&&this.eating)&&!(this.hungry && this.eating)];
}

main(){
    phil1:phil;
    phil2:phil;
    run phil1(f1,f2);
    run phil2(f2,f1);
} 

/* Temporal Spec */
/*AG(eating -> !hungry && !thinking)*/
/*AG(hungry -> !eating && !thinking)*/
/*AG(thinking -> !hungry && !eating)*/
property: !EF[phil1.own(left) && phil2.own(left)] && !EF[phil1.own(right) && phil2.own(right)] && EF[phil1.eating];

/*thinking && !owns(leftfork) && !owns(rightfork) && !available(leftfork) && !available(rightfork)*/
