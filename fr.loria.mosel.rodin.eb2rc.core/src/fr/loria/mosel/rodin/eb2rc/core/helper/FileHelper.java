package fr.loria.mosel.rodin.eb2rc.core.helper;



import ie.nuim.cs.eventb.ASTextension.bAction;
import ie.nuim.cs.eventb.ASTextension.bAxiom;
import ie.nuim.cs.eventb.ASTextension.bConstant;
import ie.nuim.cs.eventb.ASTextension.bContext;
import ie.nuim.cs.eventb.ASTextension.bEvent;
import ie.nuim.cs.eventb.ASTextension.bGuard;
import ie.nuim.cs.eventb.ASTextension.bInvariant;
import ie.nuim.cs.eventb.ASTextension.bMachine;
import ie.nuim.cs.eventb.ASTextension.bNamedElement;
import ie.nuim.cs.eventb.ASTextension.bParameter;
import ie.nuim.cs.eventb.ASTextension.bPredicateElement;
import ie.nuim.cs.eventb.ASTextension.bRefineProofUnit;
import ie.nuim.cs.eventb.ASTextension.bSet;
import ie.nuim.cs.eventb.ASTextension.bTypedElement;
import ie.nuim.cs.eventb.ASTextension.bVariable;
import ie.nuim.cs.eventb.ASTextension.bWitness;
import ie.nuim.cs.eventb.datastructure.bEventObject;
import ie.nuim.cs.eventb.datastructure.bMachineObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;


// note, on Mar 09, 2014, refactored, customized file path, created if not exists

public class FileHelper {
	//private static final String prefix = "E:/model/";
	//private static final String BAT = "E:/Eclipse_Modelling_Juno/work_bench/TemplateTest/model/model.xmi";
	
	public static JAXBContext getEventBContext() throws JAXBException{
		return JAXBContext.newInstance(
				bRefineProofUnit.class,
				bAction.class,
				bAxiom.class,
				bConstant.class,
				bContext.class,
				bEvent.class,
				bGuard.class,
				bInvariant.class,
				bMachine.class,
				bNamedElement.class,
				bParameter.class,
				bPredicateElement.class,
				bSet.class,
				bTypedElement.class,
				bVariable.class,
				bWitness.class,
				Formula.class,
				Assignment.class,
				BecomesEqualTo.class,
				BecomesMemberOf.class,
				BecomesSuchThat.class,
				Predicate.class,
				RelationalPredicate.class, 
				AssociativePredicate.class,
				BinaryPredicate.class,
				ExtendedPredicate.class,
				LiteralPredicate.class,
				MultiplePredicate.class,
				PredicateVariable.class,
				QuantifiedPredicate.class,
				SimplePredicate.class,
				UnaryPredicate.class,		
				Expression.class,
				AssociativeExpression.class,
				AtomicExpression.class,
				BinaryExpression.class,
				BoolExpression.class,
				ExtendedExpression.class,
				Identifier.class,
				BoundIdentifier.class,
				FreeIdentifier.class,
				IntegerLiteral.class,
				QuantifiedExpression.class,
				SetExtension.class,
				UnaryExpression.class,
				BoundIdentDecl.class,
				Type.class,
				BooleanType.class,
				GivenType.class,
				IntegerType.class,
				ParametricType.class,
				PowerSetType.class,
				ProductType.class,
				ProductType.class,
				bEvent.class,
				bMachine.class);
	}
	

	

	
	public static void printToFile(String content, String fileName, String basePath, String ext)
	{
			Writer writer = null;
			String genPath = basePath + "/gen/";
			
			
			try {
				File theDir = new File(genPath);

				  // if the directory does not exist, create it
				  if (!theDir.exists()) {
				    boolean result = theDir.mkdir();  
				  }
				  writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(genPath+fileName+"."+ext), "UTF-8"));
				  writer.write(content);
			} catch(Exception e){
				e.printStackTrace();
			}finally {
			    try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
	}
	
	public static String genXMItoFile(bMachine pu, String name, String filePath) 
			throws JAXBException, IOException{		
		StringWriter sw = new StringWriter();
		JAXBContext context = getEventBContext();
		String path = filePath + name + ".xmi";
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
		m.marshal(pu, new File(path));
		//m.marshal(pu, new File(BAT));
		return sw.toString();
		
		
	}
	

	
	public static void printToTex(String content, String fileName, String filePath)
	{
			Writer writer = null;
			try {
				File theDir = new File(filePath);

				  // if the directory does not exist, create it
				  if (!theDir.exists()) {
				    boolean result = theDir.mkdir();  

				    
				  }
				  
				writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filePath+fileName+".tex"), "UTF-16"));
				writer.write(content);
			} catch(Exception e){
				e.printStackTrace();
			}finally {
			    try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
	}
}
