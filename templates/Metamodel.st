// this file describes the alloy metamodel for processes
// Author: Pablo Castro
metamodel(name,boolProps,sharedBoolProps, locks, localActions,envActions,actions,invariants, init)  ::=
<<
open util/relation

abstract sig Node{}
abstract sig Prop{}

<boolProps:{p | one sig Prop_<p> extends Prop{\}}; separator="\n">

<boolProps:{p | pred Prop_<p>[m:<name>Meta,n:Node]{Prop_<p> in m.val[n] \}}; separator="\n">

<sharedBoolProps:{ s | one sig Av_<s> extends Prop{\}}; separator="\n">

<sharedBoolProps:{ s | one sig Own_<s> extends Prop{\}}; separator="\n">

<sharedBoolProps:{ s | pred Av_<s>[m:<name>Meta, n:Node]{Av_<s> in m.val[n]\}}; separator="\n">

<sharedBoolProps:{ s | pred Own_<s>[m:<name>Meta, n:Node]{Own_<s> in m.val[n]\}}; separator="\n">

one sig <name>Meta{
	nodes:set Node,
	val: nodes -> Prop,
	succs : nodes -> nodes,
	local: nodes -> nodes,
	env: nodes -> nodes,
	<actions :{a | ACT<a.name>:nodes -> nodes,}; separator="\n">
	<sharedBoolProps : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
}
{
	<if(actions)>succs = <actions : {a | ACT<a.name>}; separator="+"> + <sharedBoolProps : {s | ACTchange_<s>}; separator="+"> <endif>
	<if(localActions)>local = <localActions : {a | ACT<a.name>}; separator="+"><endif>
	<! <if(envActions)>env = <envActions : {a | ACT<a.name>}; separator="+"> <endif> !>
	<if(sharedBoolProps)>env = <sharedBoolProps : {s | ACTchange_<s>}; separator="+"><endif>
	no (local & env)
}
-- actions axioms
<if(actions)><actions : {a | fact Action_<a.name>_Ax1{ all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | <a.pre> implies <a.post> \} }; separator="\n"> <endif>
<if(actions)><actions : {a | fact Action_<a.name>_Ax2{ all s:<name>Meta.nodes | (not <a.pre>) implies (no <name>Meta.ACT<a.name>[s])\} }; separator="\n"> <endif>
<if(actions)><actions : {a | fact Action_<a.name>_Ax3{ all s:<name>Meta.nodes | <a.pre> implies (some <name>Meta.ACT<a.name>[s])  \} }; separator="\n"> <endif> 


<if(actions)><actions : {a | fact Action_<a.name>_Ax3{ all s:<name>Meta.nodes | <a.pre> implies (some <name>Meta.ACT<a.name>[s])  \} }; separator="\n"> <endif> 

-- resource axioms
<if(sharedBoolProps)><sharedBoolProps :  {sh | fact ResAx1 { all s:<name>Meta.nodes | Own_<sh>[<name>Meta, s] implies (not Av_<sh>[<name>Meta, s]) \} } ><endif>
<if(sharedBoolProps)><sharedBoolProps : {sh | fact ResAx2 { all s:<name>Meta.nodes | (not Own_<sh>[<name>Meta,s]) implies (some <name>Meta.ACTchange_<sh>[s]) \}  }><endif>
<if(sharedBoolProps)><sharedBoolProps : {sh | fact ResAx3 { all s:<name>Meta.nodes | all s':<name>Meta.ACTchange_<sh>[s] | Av_<sh>[<name>Meta,s] iff (not Av_<sh>[<name>Meta, s']) \}} >  <endif>
<if(sharedBoolProps)><sharedBoolProps : {sh | fact ResAx4 { all s:<name>Meta.nodes | all s':(<name>Meta.env[s] - <name>Meta.ACTchange_<sh>[s]) | Av_<sh>[<name>Meta,s] iff Av_<sh>[<name>Meta, s'] \}}> <endif>

-- frame axioms
<if(actions)><actions :  {a | fact FrameAxioms<a.name>{ 
<a.frameComplement: {fr | all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | Prop_<fr>[<name>Meta,s] iff Prop_<fr>[<name>Meta, s']}; separator="\n" >
<a.lockFrameComplement: {fr | all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | Av_<fr>[<name>Meta,s] iff Av_<fr>[<name>Meta, s']}; separator="\n" > 
<a.lockFrameComplement: {fr | all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | Own_<fr>[<name>Meta,s] iff Own_<fr>[<name>Meta, s']}; separator="\n" >
\}}; separator="\n"><endif>

-- frame axioms for locks (shared vars)
<if(locks)><locks :  {l | fact FrameAxioms<l.varName>{ 
all s:<name>Meta.nodes | all s':<name>Meta.ACT<l.actionName>[s] | Own_<l.varName>[<name>Meta,s] iff Own_<l.varName>[<name>Meta, s']
<boolProps: {v | all s:<name>Meta.nodes | all s':<name>Meta.ACT<l.actionName>[s] | Prop_<v>[<name>Meta,s] iff Prop_<v>[<name>Meta, s']}; separator="\n" >
<l.otherGlobalVars: {fr | all s:<name>Meta.nodes | all s':<name>Meta.ACT<l.actionName>[s] | Av_<fr>[<name>Meta,s] iff Av_<fr>[<name>Meta, s']}; separator="\n" > 
<l.otherGlobalVars: {fr | all s:<name>Meta.nodes | all s':<name>Meta.ACT<l.actionName>[s] | Own_<fr>[<name>Meta,s] iff Own_<fr>[<name>Meta, s']}; separator="\n" >
\}}; separator="\n"><endif>



-- Pred with inital condition and Invariants
pred Mod[s:<name>Meta.nodes]{ 
    all s':(*(<name>Meta.succs)[s]) | some <name>Meta.succs[s']
    <if(invariants)> <invariants : {inv | <inv>}; separator="\n">  <endif>
    <init>
}

run Mod for 6

>>
