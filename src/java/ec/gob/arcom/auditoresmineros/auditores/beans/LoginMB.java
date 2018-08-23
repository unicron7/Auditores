/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.auditores.beans;

import ec.gob.arcom.auditoresmineros.catalogos.Catalogo;
import ec.gob.arcom.auditoresmineros.controllers.FileDownloadController;
import ec.gob.arcom.auditoresmineros.controllers.FileUploadController;
import ec.gob.arcom.auditoresmineros.controllers.LocalidadController;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditorSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.CatalogoSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.EmpresaConsultoraSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.LocalidadSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.ProfesionalAuditorSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditor;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.EmpresaConsultoraAuditoria;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.ProfesionalAuditorExterno;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.TipoAuditor;
import ec.gob.arcom.auditoresmineros.util.Crypt;
import ec.gob.arcom.auditoresmineros.util.FacesUtilComun;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Will
 */
@Named(value = "loginMB")
@SessionScoped
public class LoginMB implements Serializable {
    
    @EJB
    private AuditorSBLocal auditorSB;
    @EJB
    private ProfesionalAuditorSBLocal personaNaturalSB;
    @EJB
    private EmpresaConsultoraSBLocal personaJuridicaSB;
    @EJB
    private LocalidadSBLocal localidadSB;
    @EJB
    private CatalogoSBLocal catalogoSB;
    
    /**
     * Creates a new instance of LoginBean
     */
    public LoginMB() {
        //upFileList= new ArrayList<UploadedFile>();
        tiposAuditor= new ArrayList<>();
    }
    
    private String ruc;
    private String userPassword;
    private String razonSocial;
    private Catalogo estadoAuditor;
    private boolean logged = false;
    private boolean admin= false;
    private boolean showcursotab= false;
    private boolean showconcesiontab= false;
    private Auditor auditor;
    private String newPassword;
    private ProfesionalAuditorExterno pae;
    private EmpresaConsultoraAuditoria eca;
    //Cargar las provincias
    //private SelectItem[] provincias;
    private List<String> tiposAuditor;
    //Carga de archivos
    //private List<UploadedFile> upFileList;
    private List<StreamedContent> certificadoFiles;

    public Catalogo getEstadoAuditor() {
        return estadoAuditor;
    }

    public void setEstadoAuditor(Catalogo estadoAuditor) {
        this.estadoAuditor = estadoAuditor;
    }
    
    
    public List<StreamedContent> getCertificadoFiles() {
        cargarArchivosCertificados();
        return certificadoFiles;
    }

    public void setCertificadoFiles(List<StreamedContent> certificadoFiles) {
        this.certificadoFiles = certificadoFiles;
    }
    
    public void cargarArchivosCertificados() {
        certificadoFiles= FileDownloadController.obtenerStreamedCertificadoFiles((String) FacesUtilComun.getSession().getAttribute("ruc"));
    }
    
    public List<String> getTiposAuditor() {
        Auditor a= auditorSB.findByRuc((String) FacesUtilComun.getSession().getAttribute("ruc"));
        if(a!=null) {
            this.tiposAuditor= generarListaTipos(a.getTiposAuditor());
        }
        return tiposAuditor;
    }

    public void setTiposAuditor(List<String> tiposAuditor) {
        this.tiposAuditor = tiposAuditor;
    }
    
    /*
    public SelectItem[] getProvincias() {
        this.provincias= LocalidadController.getProvincias(localidadSB);
        return provincias;
    }
    
    public void setProvincias(SelectItem[] provincias) {
        this.provincias = provincias;
    }*/

    public ProfesionalAuditorExterno getPae() {
        return pae;
    }

    public void setPae(ProfesionalAuditorExterno pae) {
        this.pae = pae;
    }

    public EmpresaConsultoraAuditoria getEca() {
        return eca;
    }

    public void setEca(EmpresaConsultoraAuditoria eca) {
        this.eca = eca;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Auditor getAuditor() {
        return auditor;
    }

    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
    
    public boolean isLogged() {
        return logged;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isShowcursotab() {
        return showcursotab;
    }

    public void setShowcursotab(boolean showcursotab) {
        this.showcursotab = showcursotab;
    }

    public boolean isShowconcesiontab() {
        return showconcesiontab;
    }

    public void setShowconcesiontab(boolean showconcesiontab) {
        this.showconcesiontab = showconcesiontab;
    }
    
    /**
     * Acciones
     * @return login
     */
    public String logoutAction() {
        HttpSession session = FacesUtilComun.getSession();
        session.invalidate();
        return "login";
    }
    
    public String loginAction() {
        return validarLogin(ruc, userPassword);
    }
    
    private String validarLogin(String usr, String pwd) {
        boolean result= this.obtenerUsuario(usr, pwd);
        if(result) {
            FacesUtilComun.showInfoMessage("Bienvenid@",razonSocial);
            return "cuentausuario";
        } else {
            FacesUtilComun.showErrorMessage("Login invalido!", "Usuario o clave incorrectos!");
        }
        return "login";
    }
    
    private boolean obtenerUsuario(String ruc, String userPassword) {
        Auditor a= this.auditorSB.findByRuc(ruc);
        
        if (a!=null) {
            if(validarCredenciales(userPassword, a.getPasswd())) {
                this.logged= true;
                this.razonSocial= a.getRazonSocial();
                this.estadoAuditor= a.getEstado();
                HttpSession session = FacesUtilComun.getSession();
                session.setAttribute("ruc", ruc);
                session.setAttribute("logged", logged);
                session.setAttribute("nombre", razonSocial);
                session.setAttribute("id", a.getId());
                showTabs(a.getEstado());
                return true;
            }
        }
        return false;
    }
    
    private boolean validarCredenciales(String pwdUser, String pwdUserDB) {
        return pwdUserDB.equals(Crypt.cryptMD5(pwdUser));
    }
    
    private void showTabs(Catalogo estadoAuditor) {
        if(estadoAuditor.getNemonico().compareTo(Auditor.INSCRITO_CALIFICADO)==0) {
            showcursotab= false;
            showconcesiontab= true;
        } else if(estadoAuditor.getNemonico().compareTo(Auditor.REGISTRADO)==0) {
            showcursotab= true;
            showconcesiontab= false;
        } else if(estadoAuditor.getNemonico().compareTo(Auditor.TRAMITE)==0) {
            showcursotab= false;
            showconcesiontab= false;
        }
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        try {
            FileUploadController.copyFile(FileUploadController.obtenerDestinoCertificado((String)FacesUtilComun.getSession().getAttribute("ruc")),
                    event.getFile().getFileName(), event.getFile().getInputstream());
            FacesUtilComun.showInfoMessage("Aviso", "Los archivos han sido cargados correctamente");
        } catch (IOException ex) {
            Logger.getLogger(LoginMB.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtilComun.showErrorMessage("Error", "Ocurrio un error al cargar los archivos");
        }
    }
    
    public String passwordChangeAction() {
        this.userPassword= "";
        this.ruc= (String) FacesUtilComun.getSession().getAttribute("ruc");
        return "passwdchange";
    }
    
    public String savePasswordAction() {
        this.auditor= auditorSB.findByRuc((String) FacesUtilComun.getSession().getAttribute("ruc"));
        if(this.auditor.getPasswd().equals(Crypt.cryptMD5(this.userPassword))) {
            this.auditor.setPasswd(Crypt.cryptMD5(newPassword));
            auditorSB.update(this.auditor);
            FacesUtilComun.showInfoMessage("", "Clave actualizada");
            return "cuentausuario";
        }
        FacesUtilComun.showErrorMessage("La clave actual es incorrecta", "");
        return null;
    }
    
    public String cancelAction() {
        String nav= "cuentausuario";
        if(this.pae != null || this.eca != null) {
            nav= "canceledit";
            this.pae= null;
            this.eca= null;
        } else {
            this.auditor= null;
        }
        return nav;
    }
    
    public String editAuditorAction() {
        this.pae= personaNaturalSB.findByRuc((String) FacesUtilComun.getSession().getAttribute("ruc"));
        if(pae!=null) {
            return "/edit/profesionalauditoredit";
        } else {
            this.eca= personaJuridicaSB.findByRuc((String) FacesUtilComun.getSession().getAttribute("ruc"));
            if(eca!=null) {
                return "/edit/empresaconsultoraedit";
            }
        }
        return null;
    }
    
    public String saveAuditorAction() {
        if(pae!=null) {
            personaNaturalSB.updatePAE(pae);
            return "success";
        } else if(eca!=null) {
            personaJuridicaSB.updateECA(eca);
            return "success";
        }
        return null;
    }
    
    public List<String> generarListaTipos(List<TipoAuditor> tiposAuditor) {
        List<String> tiposTxt= new ArrayList<>();
        tiposAuditor.forEach((tipo) -> {
            tiposTxt.add(tipo.getCatalogo().getNombre());
        });
        return tiposTxt;
    }
    
    public boolean existeCertificado() {
        return this.certificadoFiles.size()>0;
    }
    
    public void showMsg() {
        System.out.println("Hello from remote command");
        FacesUtilComun.showInfoMessage("Una nueva clave ha sido enviada a su email", "");
    }
    
}
