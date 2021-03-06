package nl.yeex.knowledge.online;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;


@WebServlet("/select")
public class FrontController extends HttpServlet {
    enum ALLOWED_PARAMETERS {ONTOLOGY, CLASS, INDIVIDUAL, OBJECTPROPERTY, DATAPROPERTY, GRAPHDEPTH, INFERRED};

    private static final Logger LOG = LoggerFactory.getLogger(FrontController.class);

    private static final String ATTRIBUTE_SESSIONDATA = "SESSIONDATA";
    private static final String documentRoot;

    private static final long serialVersionUID = 1L;


    static   {
        documentRoot = System.getProperty("DOCUMENT_ROOT");
        if (documentRoot == null)  {
            String errorMessage = "Enviroment variable 'DOCUMENT_ROOT' must be set";
            LOG.error(errorMessage);
            throw new ExceptionInInitializerError(errorMessage);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logRequestHeaders(request);
        Session session = getSessionData(request);

        String classParameter = request.getParameter(ALLOWED_PARAMETERS.CLASS.toString().toLowerCase());
        String individualParameter = request.getParameter(ALLOWED_PARAMETERS.INDIVIDUAL.toString().toLowerCase());
        String ontologyParameter = request.getParameter(ALLOWED_PARAMETERS.ONTOLOGY.toString().toLowerCase());
        String objectPropertyParameter = request.getParameter(ALLOWED_PARAMETERS.OBJECTPROPERTY.toString().toLowerCase());
        String dataPropertyParameter = request.getParameter(ALLOWED_PARAMETERS.DATAPROPERTY.toString().toLowerCase());
        String inferredParameter = request.getParameter(ALLOWED_PARAMETERS.INFERRED.toString().toLowerCase());
        String graphDepthParameter = request.getParameter(ALLOWED_PARAMETERS.GRAPHDEPTH.toString().toLowerCase());

        session.updateGraphDepthInSession(graphDepthParameter);

        if (classParameter!= null) {
            session.renderClass(classParameter, response.getOutputStream());
        } else if (individualParameter != null) {
            session.renderIndividual(individualParameter, response.getOutputStream());
        } else if (objectPropertyParameter != null) {
            session.renderObjectProperty(objectPropertyParameter, response.getOutputStream());
        } else if (dataPropertyParameter != null) {
            session.renderDataProperty(dataPropertyParameter, response.getOutputStream());
        } else if (ontologyParameter != null) {
            session.renderOntology(ontologyParameter, response.getOutputStream());
        } else if (inferredParameter != null) {
            Session.ActiveOntology activeOntology = ("true".equalsIgnoreCase(inferredParameter))? Session.ActiveOntology.INFERRED: Session.ActiveOntology.LOADED;
            session.toggleInferences(activeOntology, response.getOutputStream());
        } else {
            session.renderLastObjectAgain(response.getOutputStream());
        }
    }

    private void logRequestHeaders(HttpServletRequest request) {
        LOG.info(String.format("Content Negotiation - Request Accept Header: %s", request.getHeader("accept")));
        LOG.info(String.format("Preferred Requested Language: %s", request.getLocale()));
        Enumeration<Locale> locales = request.getLocales();
        while (locales.hasMoreElements()) {
            LOG.info(String.format("Alternative Language: %s", locales.nextElement()));
        }
    }

    private Session getSessionData(HttpServletRequest request) {
        Session session = (Session) request.getSession().getAttribute(ATTRIBUTE_SESSIONDATA);
        LOG.debug("Session Data: " + session);
        if (session == null) {
            LOG.info("Session Data is null, creating new sessiondata.");
            session = new Session(documentRoot);
            request.getSession().setAttribute(ATTRIBUTE_SESSIONDATA, session);
        }
        session.getContext().setLocale(request.getLocale());
        return session;
    }



}