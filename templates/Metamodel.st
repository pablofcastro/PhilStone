// This file describes the alloy metamodel for processes
// Author: Pablo Castro
Metamodel(name, boolProps, sharedBoolProps, sharedPrimBoolProps, sharedPrimIntVars, intVars, intSet, sharedIntVars, containsEnums, containsInts, sharedEnumVars, sharedPrimEnumVars, enumVars, enumTypes, locks, onlyLocks, localActions, envActions, actions, auxVars, auxAxioms, auxPreds, invariants, init, scope, allSharedVars)  ::=
<<
open util/relation

-- basic signatures

pred true {no none}
pred false {some none}

abstract sig Node{}
abstract sig Prop{}
abstract sig Enum{}
abstract sig NumVar{}
abstract sig EnumVar{}

-- for bool propositions
<boolProps:{p | one sig Prop_<p> extends Prop{\}}; separator="\n">

-- for int vars
<intVars:{p | one sig NumVar_<p> extends NumVar{\}}; separator="\n">

-- for enum vars
<enumVars:{p | one sig EnumVar_<p> extends EnumVar{\}}; separator="\n">

-- definition of the propositions in the model
<sharedBoolProps:{p | one sig Prop_<p> extends Prop{\}}; separator="\n">

-- definition of the propositions in the model corresponding to volatile Bools
<sharedPrimBoolProps:{p | one sig Prop_<p> extends Prop{\}}; separator="\n">

-- definition of the int in the model
<sharedIntVars:{p | one sig NumVar_<p> extends NumVar{\}}; separator="\n">

-- definition of the int in the model correpsonding to volatile ints
<sharedPrimIntVars:{p | one sig NumVar_<p> extends NumVar{\}}; separator="\n">

-- definition of the int in the model
<sharedEnumVars:{p | one sig EnumVar_<p> extends EnumVar{\}}; separator="\n">

-- definition of the int in the model correpsonding to volatile ints
<sharedPrimEnumVars:{p | one sig EnumVar_<p> extends EnumVar{\}}; separator="\n">

-- definition of the enums, one enum for each possible value
<enumTypes:{t  | one sig <t> extends Enum{\}}; separator="\n">

-- definition of the inc fun for every enum type
<enumTypes:{t  | --fun <t.name>Inc:Enum->Enum { <t.AlloyCode>\} }; separator="\n">

-- definition of the dec fun for every enum type
<enumTypes:{t  | -- fun <t.name>Dec:Enum->Enum { <t.AlloyCode>\} }; separator="\n">

<boolProps:{p | pred Prop_<p>[m:<name>Meta,n:Node]{Prop_<p> in m.val[n] \}}; separator="\n">

<intVars:{p | fun Val_<p>[m:<name>Meta,n:Node]:Int {m.ints[n][NumVar_<p>] \}}; separator="\n">

<enumVars:{p | fun Val_<p>[m:<name>Meta,n:Node]:Enum {m.enums[n][EnumVar_<p>] \}}; separator="\n">

<sharedBoolProps:{p | pred Prop_<p>[m:<name>Meta,n:Node]{Prop_<p> in m.val[n] \}}; separator="\n">

<sharedIntVars:{p | fun Val_<p>[m:<name>Meta,n:Node]{m.ints[n][NumVar_<p>] \}}; separator="\n">

<sharedEnumVars:{p | fun Val_<p>[m:<name>Meta,n:Node]{m.enum[n][EnumVar_<p>] \}}; separator="\n">

<sharedPrimBoolProps:{p | pred Prop_<p>[m:<name>Meta,n:Node]{Prop_<p> in m.val[n] \}}; separator="\n">

<sharedPrimIntVars:{p | fun Val_<p>[m:<name>Meta,n:Node]{m.ints[n][NumVar_<p>] \}}; separator="\n">

<sharedPrimEnumVars:{p | fun Val_<p>[m:<name>Meta,n:Node]{m.enums[n][EnumVar_<p>] \}}; separator="\n">

-- additional vars for shared non-rpimitive vars representing the locks associated to each of them
<sharedBoolProps:{ s | one sig Av_<s> extends Prop{\}}; separator="\n">

<sharedBoolProps:{ s | one sig Own_<s> extends Prop{\}}; separator="\n">

<sharedBoolProps:{ s | pred Av_<s>[m:<name>Meta, n:Node]{Av_<s> in m.val[n]\}}; separator="\n">

<sharedBoolProps:{ s | pred Own_<s>[m:<name>Meta, n:Node]{Own_<s> in m.val[n]\}}; separator="\n">

-- similarly for the shared non-primitive  ints
<sharedIntVars:{ s | one sig Av_<s> extends Prop{\}}; separator="\n">

<sharedIntVars:{ s | one sig Own_<s> extends Prop{\}}; separator="\n">

<sharedIntVars:{ s | pred Av_<s>[m:<name>Meta, n:Node]{Av_<s> in m.val[n]\}}; separator="\n">

<sharedIntVars:{ s | pred Own_<s>[m:<name>Meta, n:Node]{Own_<s> in m.val[n]\}}; separator="\n">

-- and for the shared non-primitive enums
<sharedEnumVars:{ s | one sig Av_<s> extends Prop{\}}; separator="\n">

<sharedEnumVars:{ s | one sig Own_<s> extends Prop{\}}; separator="\n">

<sharedEnumVars:{ s | pred Av_<s>[m:<name>Meta, n:Node]{Av_<s> in m.val[n]\}}; separator="\n">

<sharedEnumVars:{ s | pred Own_<s>[m:<name>Meta, n:Node]{Own_<s> in m.val[n]\}}; separator="\n">

-- and variables for those locks that are not linked to any variables
<onlyLocks:{ s | one sig Av_<s> extends Prop{\}}; separator="\n">

<onlyLocks:{ s | one sig Own_<s> extends Prop{\}}; separator="\n">

<onlyLocks:{ s | pred Av_<s>[m:<name>Meta, n:Node]{Av_<s> in m.val[n]\}}; separator="\n">

<onlyLocks:{ s | pred Own_<s>[m:<name>Meta, n:Node]{Own_<s> in m.val[n]\}}; separator="\n">

<auxVars:{ s | <s>}; separator="\n">

one sig <name>Meta{
	nodes:set Node,
	val: nodes -> Prop,
    <if(containsInts)>ints: (nodes -> NumVar) -> one Int,<endif>,
    <if(containsEnums)>enums : (nodes-> EnumVar) -> one Enum <endif>,
	succs : nodes -> nodes,
	local: nodes -> nodes,
	env: nodes -> nodes,
	<actions :{a | ACT<a.name>:nodes -> nodes,}; separator="\n">
	<sharedBoolProps : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
    <onlyLocks : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
    <sharedIntVars : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
    <sharedEnumVars : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
    <sharedPrimBoolProps : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
    <sharedPrimIntVars : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
    <sharedPrimEnumVars : {a | ACTchange_<a>:nodes -> nodes,}; separator="\n">
    
}
{
	<if(actions)>
        succs = <actions : {a | ACT<a.name>}; separator="+"> 
        <if(sharedBoolProps)> + <sharedBoolProps : {s | ACTchange_<s>}; separator="+"> <endif>
        <if(onlyLocks)> + <onlyLocks : {s | ACTchange_<s>}; separator="+"> <endif>
        <if(sharedIntVars)> + <sharedIntVars : {s | ACTchange_<s>}; separator="+"> <endif>
        <if(sharedEnumVars)> + <sharedEnumVars : {s | ACTchange_<s>}; separator="+"> <endif>
        <if(sharedPrimBoolProps)> + <sharedPrimBoolProps : {s | ACTchange_<s>}; separator="+"> <endif>
        <if(sharedPrimIntVars)> + <sharedPrimIntVars : {s | ACTchange_<s>}; separator="+"> <endif>
        <if(sharedPrimEnumVars)> + <sharedPrimEnumVars : {s | ACTchange_<s>}; separator="+"> <endif>
    <endif>
	<if(localActions)>
        local = <localActions : {a | ACT<a.name>}; separator="+">
    <endif>
	<! <if(envActions)> env = <envActions : {a | ACT<a.name>}; separator="+"> <endif> !>
	<if(allSharedVars)>
        env = <allSharedVars : {s | ACTchange_<s>}; separator="+">
    <endif>
	   no (local & env)
    <auxAxioms: {s | <s>}; separator="\n">
}

-- actions axioms
<if(actions)>
    <actions : {a | fact Action_<a.name>_Ax1{ all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | <a.pre> implies <a.post> \} }; separator="\n"> 
<endif>
<if(actions)>
    <actions : {a | fact Action_<a.name>_Ax2{ all s:<name>Meta.nodes | (not <a.pre>) implies (no <name>Meta.ACT<a.name>[s])\} }; separator="\n"> 
<endif>


<if(actions)>
    <actions : {a | fact Action_<a.name>_Ax3{ all s:<name>Meta.nodes | <a.pre> implies (some <name>Meta.ACT<a.name>[s])  \} }; separator="\n"> 
<endif>


-- resource axioms for booleans
<if(sharedBoolProps)>
    <sharedBoolProps :  {sh | fact ResAx1 { all s:<name>Meta.nodes | Own_<sh>[<name>Meta, s] implies (not Av_<sh>[<name>Meta, s]) \} } >
<endif>
<if(sharedBoolProps)>
    <sharedBoolProps : {sh | fact ResAx2 { all s:<name>Meta.nodes | (not Own_<sh>[<name>Meta,s]) iff (some <name>Meta.ACTchange_<sh>[s]) \}  }>
<endif>
<if(sharedBoolProps)>
    <sharedBoolProps : {sh | fact ResAx3 { all s:<name>Meta.nodes | all s':<name>Meta.ACTchange_<sh>[s] | Av_<sh>[<name>Meta,s] iff (not Av_<sh>[<name>Meta, s']) \}} >  
<endif>

-- similar axioms for those variables that are only locks, no data associated to them
<if(onlyLocks)>
    <onlyLocks :  {sh | fact ResAx1 { all s:<name>Meta.nodes | Own_<sh>[<name>Meta, s] implies (not Av_<sh>[<name>Meta, s]) \} } >
<endif>
<if(onlyLocks)>
    <onlyLocks : {sh | fact ResAx2 { all s:<name>Meta.nodes | (not Own_<sh>[<name>Meta,s]) iff (some <name>Meta.ACTchange_<sh>[s]) \}  }>
<endif>
<if(onlyLocks)>
    <onlyLocks : {sh | fact ResAx3 { all s:<name>Meta.nodes | all s':<name>Meta.ACTchange_<sh>[s] | Av_<sh>[<name>Meta,s] iff (not Av_<sh>[<name>Meta, s']) \}} >  
<endif>

-- these axioms state that local actions cannot change shared variables which locks are not owned by the process
<if(sharedBoolProps)>
    <sharedBoolProps : {sh | fact ResAx4 {  all s:<name>Meta.nodes | (not Own_<sh>[<name>Meta,s]) implies (all s':<name>Meta.local[s] | (Prop_<sh>[<name>Meta,s] iff Prop_<sh>[<name>Meta,s']) ) \} }>
<endif>

<if(sharedBoolProps)>
    <sharedBoolProps : {sh | fact ResAx4 { all s:<name>Meta.nodes | all s':(<name>Meta.env[s] - <name>Meta.ACTchange_<sh>[s]) | Av_<sh>[<name>Meta,s] iff Av_<sh>[<name>Meta, s'] \}}> 
<endif>

<if(onlyLocks)>
    <onlyLocks : {sh | fact ResAx4 { all s:<name>Meta.nodes | all s':(<name>Meta.env[s] - <name>Meta.ACTchange_<sh>[s]) | Av_<sh>[<name>Meta,s] iff Av_<sh>[<name>Meta, s'] \}}> 
<endif>


-- the following axioms state that environment actions are unrestricted
<if(sharedBoolProps)>
    <sharedBoolProps : {sh | fact ResAx5 { all s:<name>Meta.nodes | ((not Own_<sh>[<name>Meta,s]) and (not Av_<sh>[<name>Meta,s])) implies (some s':(*(<name>Meta.ACTchange_<sh>)[s]) | Prop_<sh>[<name>Meta,s']) \}} >  
    <sharedBoolProps : {sh | fact ResAx6 { all s:<name>Meta.nodes | ((not Own_<sh>[<name>Meta,s]) and (not Av_<sh>[<name>Meta,s])) implies (some s':(*(<name>Meta.ACTchange_<sh>)[s]) | not Prop_<sh>[<name>Meta,s']) \}} > 
<endif>

/*
<if(sharedBoolProps)>
    <sharedBoolProps : {sh | fact ResAx5 { all s:<name>Meta.nodes | ((not Own_<sh>[<name>Meta,s]) and (not Av_<sh>[<name>Meta,s])) implies (some s':<name>Meta.ACTchange_<sh>[s] | Prop_<sh>[<name>Meta,s']) \}} >  
    <sharedBoolProps : {sh | fact ResAx6 { all s:<name>Meta.nodes | ((not Own_<sh>[<name>Meta,s]) and (not Av_<sh>[<name>Meta,s])) implies (some s':<name>Meta.ACTchange_<sh>[s] | not Prop_<sh>[<name>Meta,s']) \}} > 
<endif>
*/

/*
-- new version of these axioms, using the * instead of next, it should be more efficient...
<if(sharedBoolProps)>
    <sharedBoolProps : {sh | fact ResAx5 { all s:<name>Meta.nodes | ((not Own_<sh>[<name>Meta,s]) and (not Av_<sh>[<name>Meta,s])) implies (some s':(*(<name>Meta.env)[s]) | Prop_<sh>[<name>Meta,s']) \}} >  
    <sharedBoolProps : {sh | fact ResAx6 { all s:<name>Meta.nodes | ((not Own_<sh>[<name>Meta,s]) and (not Av_<sh>[<name>Meta,s])) implies (some s':(*(<name>Meta.env)[s]) | not Prop_<sh>[<name>Meta,s']) \}} > 
<endif>
*/

/*
-- similarly for volatile booleans
<if(sharedPrimBoolProps)>
    <sharedPrimBoolProps : {sh | fact ResAx5 { all s:<name>Meta.nodes | (some s':(*(<name>Meta.env)[s]) | Prop_<sh>[<name>Meta,s']) \}} >  
    <sharedPrimBoolProps : {sh | fact ResAx6 { all s:<name>Meta.nodes | (some s':(*(<name>Meta.env)[s]) | not Prop_<sh>[<name>Meta,s']) \}} > 
<endif>
*/


/*
-- similarly for volatile booleans
<if(sharedPrimBoolProps)>
    <sharedPrimBoolProps : {sh | fact ResAx5 { all s:<name>Meta.nodes | (some s':(*(<name>Meta.ACTchange_<sh>)[s]) | Prop_<sh>[<name>Meta,s']) \}} >  
    <sharedPrimBoolProps : {sh | fact ResAx6 { all s:<name>Meta.nodes | (some s':(*(<name>Meta.ACTchange_<sh>)[s]) | not Prop_<sh>[<name>Meta,s']) \}} > 
<endif>

*/

<if(sharedPrimBoolProps)>
    <sharedPrimBoolProps : {sh | fact ResAx5 { all s:<name>Meta.nodes | (some s':<name>Meta.ACTchange_<sh>[s] | Prop_<sh>[<name>Meta,s']) \}} >  
    <sharedPrimBoolProps : {sh | fact ResAx6 { all s:<name>Meta.nodes | (some s':<name>Meta.ACTchange_<sh>[s] | not Prop_<sh>[<name>Meta,s']) \}} > 
<endif>

-- resource axioms for ints
<if(sharedIntVars)>
    <sharedIntVars :  {sh | fact ResIntAx1 { all s:<name>Meta.nodes | Own_<sh>[<name>Meta, s] implies (not Av_<sh>[<name>Meta, s]) \} } >
<endif>
<if(sharedIntVars)>
    <sharedIntVars : {sh | fact ResIntAx2 { all s:<name>Meta.nodes | (not Own_<sh>[<name>Meta,s]) implies (some <name>Meta.ACTchange_<sh>[s]) \}}>
<endif>
<if(sharedIntVars)>
    <sharedIntVars : {sh | fact ResIntAx3 { all s:<name>Meta.nodes | all s':<name>Meta.ACTchange_<sh>[s] | Av_<sh>[<name>Meta,s] iff (not Av_<sh>[<name>Meta, s']) \}} >  
<endif>
<if(sharedIntVars)>
    <sharedIntVars : {sh | fact ResIntAx4 { all s:<name>Meta.nodes | all s':(<name>Meta.env[s] - <name>Meta.ACTchange_<sh>[s]) | Av_<sh>[<name>Meta,s] iff Av_<sh>[<name>Meta, s'] \}}> 
<endif>

-- and axioms stating that environment actions are not restricted for ints
<if(sharedIntVars)>
    <intSet : { i |
        <sharedIntVars : {sh | fact ResIntAx<i> { all s:<name>Meta.nodes | (not Own_<sh>[<name>Meta,s]) implies  (some s':<name>Meta.ACTchange_<sh>[s] | Val_<sh>[<name>Meta,s']=<i> \}} >  
    }>
<endif>

-- and similar for volatile ints
<if(sharedIntVars)>
    <intSet : { i |
        <sharedIntVars : {sh | fact ResIntAx<i> { all s:<name>Meta.nodes | (some s':<name>Meta.ACTchange_<sh>[s] | Val_<sh>[<name>Meta,s']=<i> \}} >  
    }>
<endif>


-- Resourse axioms for enums
<if(sharedEnumVars)>
    <sharedEnumVars :  {sh | fact ResEnumAx1 { all s:<name>Meta.nodes | Own_<sh>[<name>Meta, s] implies (not Av_<sh>[<name>Meta, s]) \} } >
<endif>
<if(sharedEnumVars)>
    <sharedEnumVars : {sh | fact ResEnumAx2 { all s:<name>Meta.nodes | (not Own_<sh>[<name>Meta,s]) implies (some <name>Meta.ACTchange_<sh>[s]) \}}>
<endif>
<if(sharedEnumVars)>
    <sharedEnumVars : {sh | fact ResEnumAx3 { all s:<name>Meta.nodes | all s':<name>Meta.ACTchange_<sh>[s] | Av_<sh>[<name>Meta,s] iff (not Av_<sh>[<name>Meta, s']) \}} >  
<endif>
<if(sharedEnumVars)>
    <sharedEnumVars : {sh | fact ResEnumAx4 { all s:<name>Meta.nodes | all s':(<name>Meta.env[s] - <name>Meta.ACTchange_<sh>[s]) | Av_<sh>[<name>Meta,s] iff Av_<sh>[<name>Meta, s'] \}}> 
<endif>

-- and similar for volatile enums
<if(sharedEnumVars)>
        <sharedEnumVars : {sh | 
                <sh.values : { v |  fact ResEnumAx<v> { all s:<name>Meta.nodes | (some s':<name>Meta.ACTchange_<sh>[s] | Val_<sh>                                                   [<name>Meta,s']=<v>) \}
                }>
        }>
<endif>


-- frame axioms for local  variables
<if(actions)> 
    <actions :  {a | fact FrameAxioms<a.name>{ 
         <if(a.singletonIntFrameComplement)> 
            all s:<name>Meta.nodes,  s':<name>Meta.ACT<a.name>[s] |<a.singletonIntFrameComplement:  {fr | (Val_<fr>[<name>Meta,s] = Val_<fr>[<name>Meta, s'])}; separator=" and " > 
        <endif>
        <if(a.intFrameComplement)>
            all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | <a.intFrameComplement: {fr | (Val_<fr>[<name>Meta,s] = Val_<fr>[<name>Meta, s'])}; separator=" and " >
            <if(a.lockFrameComplement)>
                and 
            <endif>
        <endif>
         <if(a.singletonEnumFrameComplement)> 
            all s:<name>Meta.nodes,  s':<name>Meta.ACT<a.name>[s] |<a.singletonEnumFrameComplement:  {fr | (Val_<fr>[<name>Meta,s] = Val_<fr>[<name>Meta, s'])}; separator=" and " > 
        <endif>
        <if(a.enumFrameComplement)>
            all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | <a.enumFrameComplement: {fr | (Val_<fr>[<name>Meta,s] = Val_<fr>[<name>Meta, s'])}; separator=" and " >
            <if(a.lockFrameComplement)>
                and 
            <endif>
        <endif>
        <if(a.singletonBooleanFrameComplement)> 
            all s:<name>Meta.nodes,  s':<name>Meta.ACT<a.name>[s] |<a.singletonBooleanFrameComplement:  {fr | (Prop_<fr>[<name>Meta,s] iff Prop_<fr>[<name>Meta, s'])}; separator=" and " > 
        <endif>
        <if(a.booleanFrameComplement)>
            all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | <a.booleanFrameComplement: {fr | (Prop_<fr>[<name>Meta,s] iff Prop_<fr>[<name>Meta, s'])}; separator=" and " >
            <if(a.lockFrameComplement)>
                and 
            <endif>
        <else>
            <if(a.lockFrameComplement)>
                all s:<name>Meta.nodes | all s':<name>Meta.ACT<a.name>[s] | 
            <endif>
        <endif>
        <if(a.lockFrameComplement)>
            <a.lockFrameComplement: {fr | (Av_<fr>[<name>Meta,s] iff Av_<fr>[<name>Meta, s'])}; separator=" and " > 
            and
            <a.lockFrameComplement: {fr | (Own_<fr>[<name>Meta,s] iff Own_<fr>[<name>Meta, s'])}; separator=" and " >
        <endif>
        \}
        }; separator="\n">

<endif>

-- frame axioms for locks (shared vars)
<if(locks)><locks :  {l | fact FrameAxioms<l.varName>{ 
    all s:<name>Meta.nodes | all s':<name>Meta.ACT<l.actionName>[s] | (Own_<l.varName>[<name>Meta,s] iff Own_<l.varName>[<name>Meta, s'])
    <if(boolProps)> 
        and 
        <boolProps: {v |  (Prop_<v>[<name>Meta,s] iff Prop_<v>[<name>Meta, s'])}; separator=" and " >
    <endif>
    <if(intVars)> 
        and 
        <intVars: {v |  (Val_<v>[<name>Meta,s] = Val_<v>[<name>Meta, s'])}; separator=" and " >
    <endif>
     <if(enumVars)> 
        and 
        <enumVars: {v |  (Val_<v>[<name>Meta,s] = Val_<v>[<name>Meta, s'])}; separator=" and " >
    <endif>
    <if(l.otherGlobalVarsWithLocks)>
            and
        <l.otherGlobalVarsWithLocks: {fr | (Av_<fr>[<name>Meta,s] iff Av_<fr>[<name>Meta, s'])}; separator=" and " > 
            and
        <l.otherGlobalVarsWithLocks: {fr |  (Own_<fr>[<name>Meta,s] iff Own_<fr>[<name>Meta, s'])}; separator="and" >
    <endif>
    <if(l.otherBooleanGlobalVars)>
        and
        <l.otherBooleanGlobalVars: {fr | (Prop_<fr>[<name>Meta,s] iff Prop_<fr>[<name>Meta, s'])}; separator=" and " >
    <endif>
    <if(l.otherIntGlobalVars)>
        and
        <l.otherIntGlobalVars: {fr | (Val_<fr>[<name>Meta,s] = Val_<fr>[<name>Meta, s'])}; separator=" and " >
    <endif>
    <if(l.otherEnumGlobalVars)>
        and
        <l.otherEnumGlobalVars: {fr | (Val_<fr>[<name>Meta,s] = Val_<fr>[<name>Meta, s'])}; separator=" and " >
    <endif>
    \}}; separator="\n">
<endif>

-- for volatile vars we just say that local and non-volatile vars are not changed by the environmental methods that change volatile vars
<if(sharedPrimBoolProps)><sharedPrimBoolProps: {s | fact FrameAxioms<s>{ 
    all s:<name>Meta.nodes | all s':<name>Meta.ACTchange_<s>[s] | true
    <if(boolProps)>
        and
        <boolProps: {v |  (Prop_<v>[<name>Meta,s] iff Prop_<v>[<name>Meta, s'])}; separator=" and " >
    <endif>
     <if(intVars)> 
        and 
        <intVars: {v |  (Val_<v>[<name>Meta,s] = Val_<v>[<name>Meta, s'])}; separator=" and " >
    <endif>
    <if(enumVars)> 
        and 
        <enumVars: {v |  (Val_<v>[<name>Meta,s] = Val_<v>[<name>Meta, s'])}; separator=" and " >
    <endif>
    <if(sharedBoolProps)>
        and
        <sharedBoolProps: {fr | (Av_<fr>[<name>Meta,s] iff Av_<fr>[<name>Meta, s'])}; separator=" and " > 
        and
        <sharedBoolProps: {fr |  (Own_<fr>[<name>Meta,s] iff Own_<fr>[<name>Meta, s'])}; separator="and" >
        and 
        <sharedBoolProps: {fr | (Prop_<fr>[<name>Meta,s] iff Prop_<fr>[<name>Meta, s'])}; separator=" and " >
    <endif>
    <if(sharedIntVars)>
        and
        <sharedIntVars: {fr | (Av_<fr>[<name>Meta,s] iff Av_<fr>[<name>Meta, s'])}; separator=" and " > 
        and
        <sharedIntVars: {fr |  (Own_<fr>[<name>Meta,s] iff Own_<fr>[<name>Meta, s'])}; separator="and" >
        and 
        <sharedIntVars: {fr | (Val_<fr>[<name>Meta,s] = Val_<fr>[<name>Meta, s'])}; separator=" and " >
    <endif>
     <if(sharedEnumVars)>
        and
        <sharedEnumVars: {fr | (Av_<fr>[<name>Meta,s] iff Av_<fr>[<name>Meta, s'])}; separator=" and " > 
        and
        <sharedEnumVars: {fr |  (Own_<fr>[<name>Meta,s] iff Own_<fr>[<name>Meta, s'])}; separator="and" >
        and 
        <sharedEnumVars: {fr | (Val_<fr>[<name>Meta,s] = Val_<fr>[<name>Meta, s'])}; separator=" and " >
    <endif>
\}
}
>
<endif>



<auxPreds: {p | <p>}; separator="\n">

-- Pred with inital condition and Invariants
pred Mod[s:<name>Meta.nodes]{ 
    -- all s':(*(<name>Meta.succs)[s]) | some <name>Meta.succs[s'] -- if ew want any node has a succ
    <if(invariants)> <invariants : {inv | <inv>}; separator="\n">  <endif>
    <init>
    
}

run Mod for <scope>

>>
