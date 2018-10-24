import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;

public class RestSecurityFilter extends GenericFilterBean {

	private static final Logger logger = Logger.getLogger(RestSecurityFilter.class);

	private static boolean filterLogger = true;

	public static void setFilterLogger(boolean filterLogger) {
		RestSecurityFilter.filterLogger = filterLogger;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (filterLogger) {

			AuthenticationRequestWrapper authenticationRequestWrapper = new AuthenticationRequestWrapper((HttpServletRequest) request);
			AuthenticationResponseWrapper authenticationResponseWrapper = new AuthenticationResponseWrapper((HttpServletResponse) response);

			String headersString = "";
			Enumeration<String> e = authenticationRequestWrapper.getHeaderNames();
			while (e.hasMoreElements()) {
				String headers = e.nextElement();
				if (headers != null) {
					headersString += headers + " :: " + authenticationRequestWrapper.getHeader(headers) + " , ";
				}
			}

			boolean reqb = isContentJson(authenticationRequestWrapper.getContentType());

			logger.info("\n -----------------REST Request Detail-------------------------" + " \n RequestURI :: "
					+ authenticationRequestWrapper.getRequestURI() + " \n REMOTE ADDRESS :: " + authenticationRequestWrapper.getRemoteAddr()
					+ " \n HEADERS :: [ " + headersString + " ] " + " \n REQUEST BODY Size :: " + authenticationRequestWrapper.payload.length()
					+ " bytes" + " \n REQUEST BODY :: " + ((reqb) ? authenticationRequestWrapper.payload : "") + " \n HTTP METHOD :: "
					+ authenticationRequestWrapper.getMethod() + " \n ContentType :: " + authenticationRequestWrapper.getContentType());

			chain.doFilter(authenticationRequestWrapper, authenticationResponseWrapper);

			boolean resb = isContentJson(authenticationResponseWrapper.getContentType());

			logger.info("\n -----------------REST Response Detail-------------------------" + " \n Response BODY Size :: "
					+ authenticationResponseWrapper.getContent().length() + " bytes" + " \n Response BODY :: "
					+ ((resb) ? authenticationResponseWrapper.getContent() : "") + " \n Content Type :: "
					+ authenticationResponseWrapper.getContentType());
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isContentJson(String contentType) {
		if (contentType != null) {
			return contentType.startsWith("application/json");
		}
		return false;
	}
	
	

}











