variables
	ncs, try, cs

resources
	s

actions
	enterTry1 (ncs, try)
		ncs && !try
		try && !ncs
	
	enterTry3 (owns(s))
		try && available(s) && !owns(s)
		try && owns(s)
	
	enterNcs (cs, ncs, owns(s))
		cs
		ncs && !owns(s) && !cs
	
	enterCs (try, owns(s), cs)
		try && owns(s) && !cs
		cs && owns(s) && !try
	

/* Temporal Spec */
EF cs
EF try
EF ncs
ncs && !cs && !try && !owns(s) && !available(s)
