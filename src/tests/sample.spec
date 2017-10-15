spec sample
global v:boolean;

process p{	
 init : True;
 action myaction(){
 frame: v;
 pre: True;
 post: (!v || v && (v=v)) || False && (v=v);
 }
 invariant: A[True U False];
}

main(){
 p1:p;
 p2:q;
}

