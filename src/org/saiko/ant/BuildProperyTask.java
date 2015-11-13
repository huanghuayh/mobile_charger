/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ant/BuildProperyTask.java,v $
 * $Id: BuildProperyTask.java,v 1.2 2005/08/23 23:18:05 dsaiko Exp $
 * $Date: 2005/08/23 23:18:05 $
 * $Revision: 1.2 $
 * $Author: dsaiko $
 *
 * Traveling Salesman Problem genetic algorithm.
 * This source is released under GNU public licence agreement.
 * dusan@saiko.cz
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: BuildProperyTask.java,v $
 * Revision 1.2  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.1  2005/08/13 14:41:35  dsaiko
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/13 14:38:06  dsaiko
 * BuildPropertyTask
 *
 * 
 */
package org.saiko.ant;

import java.lang.reflect.Method;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:05 $
 *
 * The ant task to set property into build environment
 * from execution of parametrically specified function
 */
public class BuildProperyTask extends Task {

	/** property name to set in ANT build environment **/
	String propertyName;

	/**
	 * funtion to call for the property value
	 * has to be static function of class in classpath
	 */
	String function;


	/**
	 * @return property name which will be set in ANT build environment
	 */
	public String getPropertyName() {
		return propertyName;
	}


	/**
	 * @param propertyName property name to set in ANT build environment
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	

	/**
	 * Funtion to call for the property value
	 * has to be static function of class in classpath
	 * @return class name + function name
	 */
	public String getFunction() {
		return function;
	}


	/**
	 * Funtion to call for the property value
	 * has to be static function of class in classpath
	 * @param function class name + function name
	 */
	public void setFunction(String function) {
		this.function = function;
	}


	/**
	 * Ant task which lists all the lasses which extends TestCase and writes the
	 * list in de.mgm.wub.test.AllTests.CONFIG_FILE.
	 * <p>
	 * 
	 * @throws BuildException
	 */
	@Override
	public void execute() throws BuildException {
		try {
			String className=function.substring(0,function.lastIndexOf('.'));
			String functionName=function.substring(function.lastIndexOf('.')+1);
			Class  functionClass=Class.forName(className);
			Method functionMethod=functionClass.getMethod(functionName,new Class[]{});
			String value=functionMethod.invoke(null,new Object[]{}).toString();

			getProject().setNewProperty(propertyName, value);
		} catch(Throwable e) {
			throw new BuildException(e);
		}
	}
}