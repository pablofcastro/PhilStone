spec barrier

/* s1 is on when 1 is in the start section of when it is in a end section !*/
/* a1 on when is in the a section, of when it is the b section */
s1, a1, s2, a2: prim_boolean;

process proc1{
    owns: s1, a1;
    init: global.s1 && global.a1 && global.s2 && global.a2;
    
    action finishA(){
        frame: s1;
        pre: global.s1 && global.a1;
        post: !global.s1;
    }
    
    /*Adding a loop for waiting affects the perfomance! */
    action wait(){
        frame: s1;
        pre: !global.s1;
        post: !global.s1;
    }
    
    
    action startB(){
        frame: a1, s1;
        pre: global.a1 && !global.s1;
        /*pre: !global.s1 || global.s1;*/
        post: !global.a1 && global.s1;
    }

    action finishB(){
        frame: s1;
        pre: !global.a1 && global.s1; /* original pre */
        /*pre: !global.a1;*/
        post: !global.s1;
    }
    
     action startA(){
        frame: s1, a1;
        pre: !global.s1 && !global.a1;
        post: global.s1 && global.a1;
    }
    invariant: EF[!global.a1 && !global.s1];
}

process proc2{
    owns: s2, a2;
    init: global.s2 && global.a2 && global.s1 && global.a1;
    
    action finishA(){
        frame: s2;
        pre: global.s2 && global.a2;
        post: !global.s2;
    }

        /*Adding a loop for waiting affects the perfomance! */
    action wait(){
        frame: s2;
        pre: !global.s2;
        post: !global.s2;
    }
    
    action startB(){
        frame: a2, s2;
        pre: global.a2 && !global.s2;
        /*pre: global.a2 || !global.a2;*/
        post: !global.a2 && global.s2;
    }

    action finishB(){
        frame: s2;
        pre: !global.a2 && global.s2;
        post: !global.s2;
    }
    
     action startA(){
        frame: s2, a2;
        pre: !global.s2 && !global.a2;
        /*pre: !global.s2;*/
        post: global.s2 && global.a2;
    }
    invariant: EF[!global.a2 && !global.s2];
}


main(){
        p1:proc1;
        p2:proc2;
        run p1();
        run p2();
}
/*property: AG[!(global.sa1 && global.sb2)];*/
property: AG[!global.a1 || global.a2] && AG[!global.a2 || global.a1];