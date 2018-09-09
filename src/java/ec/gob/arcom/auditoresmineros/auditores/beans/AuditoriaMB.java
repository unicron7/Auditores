/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.auditores.beans;

import ec.gob.arcom.auditoresmineros.controllers.AuditoriaController;
import ec.gob.arcom.auditoresmineros.controllers.FileDownloadController;
import ec.gob.arcom.auditoresmineros.controllers.FileUploadController;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditoriaSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.CatalogoSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditoria;
import ec.gob.arcom.auditoresmineros.util.FacesUtilComun;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Will
 */
@Named(value = "auditoriaMB")
@SessionScoped
public class AuditoriaMB implements Serializable {
    @EJB
    private AuditoriaSBLocal auditoriaDao;
    @EJB
    private CatalogoSBLocal catalogoDao;
    
    private List<Auditoria> auditorias;
    private List<Auditoria> finalizadas;
    private Auditoria auditoria;
    
    /**
     * Creates a new instance of ConcesionAuditorMB
     */
    public AuditoriaMB() {
        auditorias= new ArrayList();
        finalizadas= new ArrayList();
    }
    
    @PostConstruct
    public void inicializar() {
        obtenerAuditorias();
        obtenerFinalizadas();
    }

    public List<Auditoria> getAuditorias() {
        return auditorias;
    }

    public void setAuditorias(List<Auditoria> auditorias) {
        this.auditorias = auditorias;
    }

    public List<Auditoria> getFinalizadas() {
        return finalizadas;
    }

    public void setFinalizadas(List<Auditoria> finalizadas) {
        this.finalizadas = finalizadas;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
    
    private void obtenerAuditorias() {
        auditorias= AuditoriaController.listarAuditoriaPorAuditor(auditoriaDao, 
                (Long)FacesUtilComun.getSession().getAttribute("id"), catalogoDao.findByNemonico(Auditoria.VIGENTE));
    }
    
    private void obtenerFinalizadas() {
        finalizadas= AuditoriaController.listarAuditoriaPorAuditor(auditoriaDao, 
                (Long)FacesUtilComun.getSession().getAttribute("id"), catalogoDao.findByNemonico(Auditoria.FINALIZADO));
    }
    
    public void setAuditoriaFinalizar(Integer row) {
        auditoria= auditorias.get(row);
    }
    
    public void endAuditoriaAction() {
        RequestContext.getCurrentInstance().execute("PF('infauditoria').show();");
    }
    
    private void finalizarAuditoria() {
        auditoria.setEstadoAuditoria(catalogoDao.findByNemonico(Auditoria.FINALIZADO));
        AuditoriaController.actualizarAuditoria(auditoriaDao, auditoria);
        obtenerAuditorias();
        obtenerFinalizadas();
        FacesUtilComun.showInfoMessage("", "Auditoria finalizada correctamente");
    }
    
    public void subirInfAuditoria(FileUploadEvent event) {
        boolean infCargado;
        try {
            FileUploadController.copyFile(FileUploadController.obtenerDestinoInfAuditoria(auditoria.getId().toString()),
                    event.getFile().getFileName(), event.getFile().getInputstream());
            infCargado= true;
            FacesUtilComun.showInfoMessage("Aviso", "Los archivos han sido cargados correctamente");
        } catch (IOException ex) {
            Logger.getLogger(LoginMB.class.getName()).log(Level.SEVERE, null, ex);
            infCargado= false;
            FacesUtilComun.showErrorMessage("Error", "Ocurrio un error al cargar los archivos");
        }
        
        if(infCargado) {
            finalizarAuditoria();
        }
    }
    
    public StreamedContent obtenerInfAuditoria(Integer row) {
        return FileDownloadController.obtenerStreamedInfAuditoria(finalizadas.get(row).getId());
    }
    
}
