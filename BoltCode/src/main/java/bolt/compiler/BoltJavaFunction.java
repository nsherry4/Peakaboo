package bolt.compiler;


abstract class BoltJavaFunction {

	private static int functionCount = 1;
	
	private String classname;
	private String includeText = "";
	private String functionText = "return null;";
	private String otherText = "";
	
	private BoltJavaCompiler compiler;

	public BoltJavaFunction() {
		
		classname = "BoltFunction_" + functionCount++;
		
		compiler = new BoltJavaCompiler(classname);
				
		setFunctionText("");
		setIncludeText("");
	}
	
	public void setIncludeText(String includes)
	{
		includeText = includes;
		sourceCodeChanged();
	}
	public String getIncludeText()
	{
		return "import fava.signatures.*;\nimport java.util.function.*;\n" + includeText;
	}
	
	public void setFunctionText(String function)
	{
		functionText = function;
		sourceCodeChanged();
	}
	
	public String getFunctionText()
	{
		return functionText;
	}
	
	public void setOtherText(String other)
	{
		otherText = other;
		sourceCodeChanged();
	}
	public String getOtherText()
	{
		return otherText;
	}
	
	public String getClassName()
	{
		return classname;
	}
	
	protected String generateSourceCode(String interfaceName, String functionName, String interfaceTypes, String returnType, String functionSignature)
	{
		return getIncludeText() +
		"\n" +
		"public class " + getClassName() + " implements " + interfaceName + "<" + interfaceTypes + "> {\n" +
		"	@Override \n" +
		"	public " + returnType + " " + functionName + "(" + functionSignature + "){\n" +
		getFunctionText() +
		"\n" + 
		"	}\n" +
		"\n" +
		otherText +
		"\n}\n";
	}
	
	protected Object getFunctionObject()
	{
		
		compiler.setSourceCode(getSourceCode());
		compiler.compile();
		Class<?> mainClass = compiler.getMainClass();
		
		try {
			return mainClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new BoltCompilationException("Error instantiating class");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new BoltCompilationException("Error instantiating class");
		} catch (Exception e) {
			throw new BoltCompilationException("Error instantiating class");
		}
	}
	
	
	
	protected abstract String getSourceCode();
	protected abstract void sourceCodeChanged();
	
}
