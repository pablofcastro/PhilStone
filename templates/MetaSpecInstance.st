// this file describes the alloy  specification template to find instances for processes
// Author: Pablo Castro

metaspec(name,boolProps, sharedBoolProps, localActions,envActions,actions,invariants, init)  ::=
<<
open util/relation

abstract sig Node{}
abstract sig Prop{}

<nodes:{n | one sig <n.name> extends Node{\}}; separator="\n">

<boolProps:{p | one sig Prop_<p> extends Prop{\}}; separator="\n">

<boolProps:{p | pred Prop_<p>[m:<name>Meta,n:Node]{Prop_<p> in m.val[n] \}}; separator="\n">

<sharedBoolProps:{ s | one sig Av_<s> extends Prop{\}}; separator="\n">

<sharedBoolProps:{ s | one sig Own_<s> extends Prop{\}}; separator="\n">

<sharedBoolProps:{ s | pred Av_<s>[m:<name>Meta, n:Node]{Av_<s> in m.val[n]\}}; separator="\n">

<sharedBoolProps:{ s | pred Own_<s>[m:<name>Meta, n:Node]{Own_<s> in m.val[n]\}}; separator="\n">

one sig Instance{
    nodes:set Node,
	val: nodes -> Prop,
	succs : nodes -> nodes,
	local: nodes -> nodes,
	env: nodes -> nodes,
	<localActions :{a | ACT<a.name>:nodes -> nodes,}; separator="\n">
	<envActions : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
}
{
    nodes = <nodes:{n | one sig <n.name> extends Node{\}}; separator="+">
    <if(localActions)> <localActions : {a | ACT<a.name> in <a.edges : {e | <e.origin> -> <e.target>}; separator="+">}; separator="\n"> <endif>
    <if(envActions)> <envActions : {a | ACT<a.name> = <a.edges : {e | <e.origin> -> <e.target>}; separator="+">}; separator="\n"> <endif>
    succs = <localActions : {a | ACT<a.name>}; separator="+"> + <envActions : {s | ACTchange_<s>}; separator="+">
 	val in <vals : {v | <v.first> -> <v.second>}; separator="+">
	local = (<localActions : {a | ACT<a.name>}; separator="+">
 	env = <envActions : {s | ACTchange_<s>}; separator="+">
    all n:nodes | some n.succs
    
}

pred compile[]{}
run compile for 6

>>
