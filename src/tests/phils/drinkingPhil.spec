spec drinkingPhilosophers

b1, b2: boolean;

process phil(left:boolean, right:boolean) {
	
	tranquil, thirsty, drinking: boolean;
    init: this.tranquil && !this.thirsty && !this.drinking && av(left) && av(right); 

	action	startTranquil () {
				frame: drinking, tranquil, left, right;
				pre: this.drinking;
				post: this.tranquil && !this.drinking && !own(left) && !own(right);
			}

	action	becomeThirsty() {
				frame: tranquil, thirsty;
				pre: this.tranquil;
				post: this.thirsty && !this.tranquil;
			}

	action	getLeft () {
				frame: left;
				pre: av(left) && this.thirsty;
				post: own(left);
			}

	action	getRight () {
				frame: right;
				pre: av(right) && this.thirsty;
				post: own(right);
			}

	action	drinkLeft () {
				frame: drinking, thirsty;
				pre: this.thirsty && own(left);
				post: this.drinking && !this.thirsty;
			}
	action	drinkRight () {
				frame: drinking, thirsty;
				pre: this.thirsty && own(right);
				post: this.drinking && !this.thirsty;
			}
	action	drinkBoth () {
				frame: drinking, thirsty;
				pre: this.thirsty && own(left) && own(right);
				post: this.drinking && !this.thirsty;
			}
    invariant: AG[!(this.tranquil && this.thirsty)&&!(this.tranquil&&this.drinking)&&!(this.thirsty && this.drinking)] && EF[this.drinking];
}

main(){
    phil1:phil;
    phil2:phil;
    run phil1(b1,b2);
    run phil2(b2,b1);
} 

/* Temporal Spec */
/*AG(drinking -> !thirsty && !tranquil)*/
/*AG(thirsty -> !drinking && !tranquil)*/
/*AG(tranquil -> !thirsty && !drinking)*/
property: !EF[phil1.own(left) && phil2.own(left)] && !EF[phil1.own(right) && phil2.own(right)] && !AG[!phil1.drinking] && !AG[!phil2.drinking];
/*property: EF[phil1.drinking];*/
/*property: !EF[phil1.own(left) && phil2.own(left)] && !EF[phil1.own(right) && phil2.own(right)];*/
/*tranquil && !owns(leftfork) && !owns(rightfork) && !available(leftfork) && !available(rightfork)*/