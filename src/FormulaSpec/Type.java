package FormulaSpec;

public enum Type {
	INT,
	BOOL,
	LOCK,
	PRIMINT, // this the type of primitive ints in JAVA, its elements do not have implicit locks
	PRIMBOOL, // and for primitive BOOL
	ERROR // for the case of type errors
}
