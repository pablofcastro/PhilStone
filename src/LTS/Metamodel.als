abstract sig Node{}
one sig Node03 extends Node{}
one sig Node02 extends Node{}
one sig Node05 extends Node{}
one sig Node04 extends Node{}
one sig Node01 extends Node{}
one sig Node00 extends Node{}
abstract sig Prop{}
one sig Prop_CS extends Prop{}
one sig Own_S extends Prop{}
one sig Prop_TRYING extends Prop{}
one sig Prop_NCS extends Prop{}
one sig Av_S extends Prop{}
one sig Meta_NoName{
    nodes : set Node,
    succs : nodes -> nodes,
    val: nodes -> Prop,
    ACTgetNCS: nodes -> nodes,
    ACTgetCS: nodes -> nodes,
    ACTgetTRYING: nodes -> nodes,
    ACTgetS: nodes -> nodes,
    ACTchange_s: nodes -> nodes,
    local: nodes -> nodes,
    env: nodes -> nodes
}
{
    nodes = Node03+Node02+Node05+Node04+Node01+Node00
    ACTgetNCS = Node00->Node02
    ACTgetCS = Node01->Node00
    ACTgetTRYING = Node03->Node03 + Node02->Node03 + Node05->Node04 + Node04->Node04
    ACTgetS = Node04->Node01
    ACTchange_s = Node03->Node04 + Node02->Node05 + Node05->Node02 + Node04->Node03

    val = Node03->Prop_TRYING + Node02->Prop_NCS + Node05->Prop_NCS + Node05->Av_S + Node04->Prop_TRYING + Node04->Av_S + Node01->Prop_TRYING + Node01-Own_S + Node00->Prop_CS + Node00->Own_S
    succs = ACTgetNCS+ACTgetCS+ACTgetTRYING+ACTgetS+ACTchange_s
    env = ACTchange_s
    local = succs - env
}

one sig Process1{
 	nodes : set Node,
    succs : nodes -> nodes,
    val: nodes -> Prop,
    ACTgetNCS: nodes -> nodes,
    ACTgetCS: nodes -> nodes,
    ACTgetTRYING: nodes -> nodes,
    ACTgetS: nodes -> nodes,
    ACTchange_s: nodes -> nodes,
    local: nodes -> nodes,
    env: nodes -> nodes
}
{
	nodes = Meta_NoName.nodes
	succs in Meta_NoName.succs
 	val in Meta_NoName.val
    ACTgetNCS in Meta_NoName.ACTgetNCS
	ACTgetCS in Meta_NoName.ACTgetCS
	ACTgetTRYING in Meta_NoName.ACTgetTRYING
	ACTgetS in Meta_NoName.ACTgetS
	ACTchange_s in Meta_NoName.ACTchange_s
	local in Meta_NoName.local
 	env in Meta_NoName.env
	all n:nodes | some n.succs
}

one sig Process2{
 nodes : set Node,
    succs : nodes -> nodes,
    val: nodes -> Prop,
    ACTgetNCS: nodes -> nodes,
    ACTgetCS: nodes -> nodes,
    ACTgetTRYING: nodes -> nodes,
    ACTgetS: nodes -> nodes,
    ACTchange_s: nodes -> nodes,
    local: nodes -> nodes,
    env: nodes -> nodes

}
{
	nodes = Meta_NoName.nodes
	succs in Meta_NoName.succs
 	val in Meta_NoName.val
    ACTgetNCS in Meta_NoName.ACTgetNCS
	ACTgetCS in Meta_NoName.ACTgetCS
	ACTgetTRYING in Meta_NoName.ACTgetTRYING
	ACTgetS in Meta_NoName.ACTgetS
	ACTchange_s in Meta_NoName.ACTchange_s
	local in Meta_NoName.local
 	env in Meta_NoName.env
	all n:nodes | some n.succs
}

sig NodeSystem{
	nodeProcess1: Node,
	nodeProcess2: Node,
}

pred match[n:Process1.nodes,n':Process2.nodes]{
	(n->Av_S in Process1.val) iff (n'->Av_S in Process2.val)
}

one sig System{
	nodes: set NodeSystem,
	succs: nodes -> nodes
}
{
//	nodes.nodeProcess1 = Process1.nodes
//	nodes.nodeProcess2 = Process2.nodes
//	some nodes
	    all n,n':Process1.nodes | all m,m':Process2.nodes | ( match[n,m] and match[n',m'] and (n->n' in Process1.local) and (m->m' in Process2.env) ) implies 
																		  (some s,s':nodes | s.nodeProcess1=n and s.nodeProcess2=n' and s'.nodeProcess1=n' and s'.nodeProcess2=m'  and (s->s' in succs))
		all n,n':Process1.nodes | all m,m':Process2.nodes | ( match[n,m] and match[n',m'] and (n->n' in Process1.env) and (m->m' in Process2.local) ) implies 
																		  (some s,s':nodes | s.nodeProcess1=n and s.nodeProcess2=n' and s'.nodeProcess1=n' and s'.nodeProcess2=m'  and (s->s' in succs))	

//all n,n':nodes | n -> n' in succs iff ((n.nodeProcess1 -> n'.nodeProcess1 in Process1.local) and (n.nodeProcess2 -> n'.nodeProcess2 in Process2.env)) || 
//														((n.nodeProcess1 -> n'.nodeProcess1 in Process1.env) and (n.nodeProcess2 -> n'.nodeProcess2 in Process2.local))
//	all n:nodes | some n.succs

}



pred compile[]{}
run compile for 6
