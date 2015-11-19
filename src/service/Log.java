package service;

import seaitmmanager.Global;

public class Log {
	public static void print(int iDebugLevel, Object o){
	      if (Global.g_iDebugLevel >= iDebugLevel) {
	          String callerClass = getCallerClassNameWithLineNumber();
	          System.out.println(callerClass + " : " + o);
	      }
	  }
	 
	  /**
	   * Get caller class name and line number
	   *
	   * @return Class name and line number where this class invoked
	   */
	  private static String getCallerClassNameWithLineNumber() {
	    String className = null;
	    int lineNumber = 0;
	    long unixTime = System.currentTimeMillis() / 1000L;
	 
	    try {
	      // get elements from stack trace
	      Throwable t = new Throwable();
	      StackTraceElement[] elements = t.getStackTrace();
	 
	      if (elements != null && elements.length > 0) {
	        for (StackTraceElement element : elements){
	          // iterate through elements until we
	          // don't see this class, then it is
	          // a caller class which is what wanted
	          lineNumber = element.getLineNumber();
	          className = element.getClassName();
	          if (className != null
	              && !className.equals(Log.class.getName())) {
	            break;
	          }
	        }
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	 
	    if (className == null) {
	      // Couldn't find caller class within stack elements
	      return "!CallerClass";
	    } else {
	      return className.concat(" <line " + lineNumber + "> <t " + unixTime + ">");
	    }
	  }
}
