/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Admin;

import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.licensing.base.LicenseKey;
import com.yoveri.apiFirmas.util.ParametrosGlobales;
import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
/**
 *
 * @author Administrador
 */
public class Escucha implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("\nINICIALIZANDO API_FIRMAS");
        //LicenseKey.loadLicenseFile(new File("/u01/Expedientes/licencia_itext_trial.json"));
           //These two lines of code allow us to disable the AGPL warning messages
        EventManager eventManager = EventManager.getInstance();
        //Note that you acknowledge and agree with the AGPL license requirements when you call this method
        eventManager.acknowledgeAgplUsageDisableWarningMessage();
        
        
        ServletContext sc = sce.getServletContext();
        ParametrosGlobales.setURL_API_JOB(sc.getInitParameter("URL_API_JOB"));
        ParametrosGlobales.setURL_API_RESULT(sc.getInitParameter("URL_API_RESULT"));
        ParametrosGlobales.setURL_API_SING(sc.getInitParameter("URL_API_SING"));
        ParametrosGlobales.setPOOL(sc.getInitParameter("POOL"));
        ParametrosGlobales.setRUTA_REPORTE(sc.getInitParameter("RUTA_REPORTE"));
        ParametrosGlobales.setVALIDA_ENLINEA(sc.getInitParameter("VALIDA_ENLINEA"));
        ParametrosGlobales.setRETORNA_DOCTO(sc.getInitParameter("RETORNA_DOCTO"));
        ParametrosGlobales.setURL_API_TOKEN(sc.getInitParameter("URL_API_TOKEN"));
    } 

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
