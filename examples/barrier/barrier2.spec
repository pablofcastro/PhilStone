spec barrier

sa1, ea1, sb1, eb1, sa2, ea2, sb2, eb2: prim_boolean;

process proc1{
    owns: sa1, ea1, sb1, eb1;
    init: global.sa1 && !global.ea1 && !global.sb1 && !global.eb1 && global.sa2 && !global.ea2 && !global.sb2 && !global.eb2;
    
    action staySA1(){
        frame: sa1;
        pre: global.sa1;
        post: global.sa1;
    }
    
    action stayEA1(){
        frame: ea1;
        pre: global.ea1;
        post: global.ea1;
    }
    
    action staySB1(){
        frame: sb1;
        pre: global.sb1;
        post: global.sb1;
    }
    
    action stayEB1(){
        frame: eb1;
        pre: global.eb1;
        post: global.eb1;
    }
    
    action finishA(){
        frame: sa1, ea1;
        pre: global.sa1 && !global.ea1;
        post: !global.sa1 && global.ea1;
    }
    
    action startB(){
        frame: ea1, sb1;
        pre: global.ea1;
        post: !global.ea1 && global.sb1;
    }

    action finishB(){
        frame: sb1, eb1;
        pre: global.sb1;
        post: !global.sb1 && global.eb1;
    }
    
     action startA(){
        frame: eb1, sa1;
        pre: global.eb1;
        post: !global.eb1 && global.sa1;
    }
    invariant: AG[global.sa1 || !global.sa1];
}

process proc2{
    owns: sa2, ea2, sb2, eb2;
    init: global.sa2 && !global.ea2 && !global.sb2 && !global.eb2 && global.sa1 && !global.ea1 && !global.sb1 && !global.eb1;
    
     action staySA2(){
        frame: sa2;
        pre: global.sa2;
        post: global.sa2;
    }
    
    action stayEA2(){
        frame: ea2;
        pre: global.ea2;
        post: global.ea2;
    }
    
    action staySB2(){
        frame: sb2;
        pre: global.sb2;
        post: global.sb2;
    }
    
    action stayEB2(){
        frame: eb2;
        pre: global.eb2;
        post: global.eb2;
    }
    
    
    action finishA(){
        frame: sa2, ea2;
        pre: global.sa2 && !global.ea2;
        post: !global.sa2 && global.ea2;
    }
    
    action startB(){
        frame: ea2, sb2;
        pre: global.ea2;
        post: !global.ea2 && global.sb2;
    }

    action finishB(){
        frame: sb2, eb2;
        pre: global.sb2;
        post: !global.sb2 && global.eb2;
    }
    
     action startA(){
        frame: eb2, sa2;
        pre: global.eb2;
        post: !global.eb2 && global.sa2;
    }
    invariant: AG[global.sa2 || !global.sa2];
}

main(){
        p1:proc1;
        p2:proc2;
        run p1();
        run p2();
}
property: AG[!(global.sa1 && global.sb2)];
