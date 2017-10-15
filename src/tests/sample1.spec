spec sample
global v:boolean;

process p{	
 init : True;
 action myaction(){
 frame v;
 pre: True;
 post: (!v || v && (v=v)) || False && (v=v);
 }
 invariant: A[True U False];
}

process q{
	w:int;
	init : True;
 	action a(){
		frame: w;
		pre:True;
		post:True;
	}
	invariant: True;

}

main(){
 p1:p;
 p2:q;
}

property: True;
