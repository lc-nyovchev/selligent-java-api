package com.nyo.selligent.java.api;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.tempuri.AutomationAuthHeader;
import org.tempuri.Individual;
import org.tempuri.IndividualSoap;

/**
 * A factory to obtain instances of a {@link IndividualSoap}
 * @author nyo
 */
public class SelligentServiceFactory {
	private static final String NAMESPACE = "http://tempuri.org/";
	/**
	 * Gets an authenticated Selligent instance of a service with the specified username/password/namespace combination
	 * @param endpoint
	 * @param username
	 * @param password
	 * @return 
	 */
	public IndividualSoap getInstance(final String endpoint, final String username, final String password) {
		Individual individual = new Individual();
		individual.setHandlerResolver(new HandlerResolver() {
			@Override
			public List<Handler> getHandlerChain(PortInfo portInfo) {
				return Arrays.<Handler>asList(
					new SoapHeaderHandler(username, password, NAMESPACE)
				);
			}
		});
		IndividualSoap individualSoap12 = individual.getIndividualSoap12();
		
		BindingProvider bindingProvider = (BindingProvider) individualSoap12;
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		
		return individualSoap12;
	}
	
	/**
	 * A class to handle augmenting the SOAP header of the message with the appropriate 
	 */
	@AllArgsConstructor
	private static class SoapHeaderHandler implements SOAPHandler<SOAPMessageContext> {
		
		@Setter @Getter private String username;
		@Setter @Getter private String password;
		@Setter @Getter private String namespace;

		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			try {
				SOAPMessage message = context.getMessage();
				SOAPHeader header = message.getSOAPHeader();
				SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
				if (header == null) {
					header = envelope.addHeader();
				}
				
				QName qNameUsername = new QName(namespace, "Login");
				SOAPHeaderElement soapHeaderUsername = header.addHeaderElement(qNameUsername);
				soapHeaderUsername.addTextNode(username);

				QName qNamePassword = new QName(namespace, "Password");
				SOAPHeaderElement soapHeaderPassword = header.addHeaderElement(qNamePassword);
				soapHeaderPassword.addTextNode(password);

				SOAPHeaderElement userCredentials = header.addHeaderElement(new QName(namespace, AutomationAuthHeader.class.getSimpleName()));
				userCredentials.addChildElement(soapHeaderUsername);
				userCredentials.addChildElement(soapHeaderPassword);

				message.saveChanges();
			} catch (SOAPException e) {
				throw new RuntimeException("SOAP Exception in Selligent", e);
			}
			return true;
		}

		@Override
		public void close(MessageContext context) {
			//do nothing
		}

		@Override
		public boolean handleFault(SOAPMessageContext context) {
			return true;
		}

		@Override
		public Set<QName> getHeaders() {
			return null;
		}
	}
}
