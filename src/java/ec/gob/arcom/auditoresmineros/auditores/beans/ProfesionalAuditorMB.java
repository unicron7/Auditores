/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.auditores.beans;

import ec.gob.arcom.auditoresmineros.catalogos.Catalogo;
import ec.gob.arcom.auditoresmineros.controllers.LocalidadController;
import ec.gob.arcom.auditoresmineros.keygen.Keygen;
import ec.gob.arcom.auditoresmineros.mail.FromTo;
import ec.gob.arcom.auditoresmineros.mail.MailSender;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditorSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.CatalogoSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.LocalidadSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.ProfesionalAuditorSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditor;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Direccion;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Localidad;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Pago;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.ProfesionalAuditorExterno;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.TipoAuditor;
import ec.gob.arcom.auditoresmineros.util.Crypt;
import ec.gob.arcom.auditoresmineros.util.FacesUtilComun;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Will
 */
@Named(value = "profesionalAuditorMB")
@SessionScoped
public class ProfesionalAuditorMB implements Serializable {
    @EJB
    private ProfesionalAuditorSBLocal profesionalAuditorSB;
    @EJB
    private LocalidadSBLocal localidadSB;
    @EJB
    private AuditorSBLocal auditorSB;
    @EJB
    private CatalogoSBLocal catalogoSB;
    
     /*
    * Propiedades
    */
    private ProfesionalAuditorExterno profesionalAuditorExterno;
    //Cargar las provincias
    private List<Localidad> provincias;
    //Para subir archivos
    private UploadedFile upFile;
    private boolean showUploaded=true;
    private boolean showButtons=false;
    private Date fechaMaxPago;
    private String nombreEntidadFinanciera;
    private Catalogo selectedTipoAuditorValue;
    private boolean rucValido= false;
    private Pago pago;
    
    @Inject
    private AdjuntoMB adjuntoMB;

    /**
     * Creates a new instance of ProfesionalAuditorMB
     */
    public ProfesionalAuditorMB() {
    }
    
    @PostConstruct
    private void init() {
        this.profesionalAuditorExterno= new ProfesionalAuditorExterno();
        this.profesionalAuditorExterno.setDireccion(new Direccion());
        this.profesionalAuditorExterno.setActivo(true);
        this.profesionalAuditorExterno.setEstado(catalogoSB.findByNemonico(Auditor.REGISTRADO));
        this.profesionalAuditorExterno.setAniosExperiencia(5);
        this.pago= new Pago();
        this.profesionalAuditorExterno.setPagos(new ArrayList<>());
        this.profesionalAuditorExterno.setTiposAuditor(new ArrayList<>());
    }
    
    /*
     * Getters and Setters
     */
    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
    
    public AdjuntoMB getAdjuntoMB() {
        return adjuntoMB;
    }
    
    public void setAdjuntoMB(AdjuntoMB adjuntoMB) {
        this.adjuntoMB = adjuntoMB;
    }

    public Catalogo getSelectedTipoAuditorValue() {
        return selectedTipoAuditorValue;
    }

    public void setSelectedTipoAuditorValue(Catalogo selectedTipoAuditorValue) {
        this.selectedTipoAuditorValue = selectedTipoAuditorValue;
    }
    
    public ProfesionalAuditorExterno getProfesionalAuditorExterno() {
        return profesionalAuditorExterno;
    }

    public void setProfesionalAuditorExterno(ProfesionalAuditorExterno profesionalAuditorExterno) {
        this.profesionalAuditorExterno = profesionalAuditorExterno;
    }

    public boolean isShowUploaded() {
        return showUploaded;
    }

    public void setShowUploaded(boolean showUploaded) {
        this.showUploaded = showUploaded;
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }
    
    //Para subir archivos
    public UploadedFile getUpFile() {
        return upFile;
    }

    public void setUpFile(UploadedFile upFile) {
        this.upFile = upFile;
    }

    public List<Localidad> getProvincias() {
        this.provincias= LocalidadController.getProvincias(localidadSB);
        return provincias;
    }

    public void setProvincias(List<Localidad> provincias) {
        this.provincias = provincias;
    }
    
    public Date getFechaMaxPago() {
        fechaMaxPago= Calendar.getInstance().getTime();
        return fechaMaxPago;
    }

    public void setFechaMaxPago(Date fechaMaxPago) {
        this.fechaMaxPago = fechaMaxPago;
    }

    public String getNombreEntidadFinanciera() {
        nombreEntidadFinanciera= "BANCO PICHINCHA";
        return nombreEntidadFinanciera;
    }

    public void setNombreEntidadFinanciera(String nombreEntidadFinanciera) {
        this.nombreEntidadFinanciera = nombreEntidadFinanciera;
    }
    
    /*
    * Metodos
    */
    public void newPAEAction() {
        System.out.println("Nuevo PAE action");
        init();
    }
    
    public void sendMailAction(String key) {
        FromTo ft= new FromTo();
        MailSender ms= new MailSender();
        ft.setTo(this.profesionalAuditorExterno.getEmail());
        ft.setSubject("Info Auditores Técnicos Mineros - Registro Exitoso");
        ft.setMsg(ms.getCuentaCreadaMsg(this.profesionalAuditorExterno.getRuc(), key));
        ms.sendMailHTML(ft);
    }
    
    public String nextAction() {
        if(rucValido) {
            if(profesionalAuditorExterno.getPagos().size()>0) {
                adjuntoMB.setUpFileList(new ArrayList<>());
                return "cargaradjuntospn";
            } else {
                FacesUtilComun.showErrorMessage("Error", "Debe agregar almenos un pago");
            }
        }
        return "profesionalauditorform";
    }
    
    public boolean saveAction() {
        if(adjuntoMB.subirArchivos(profesionalAuditorExterno.getRuc())) {
            try {
                this.profesionalAuditorExterno.setFechaDeRegistro(Calendar.getInstance().getTime());
                this.profesionalAuditorExterno.setHoraDeRegistro(Calendar.getInstance().getTime());
                this.profesionalAuditorExterno.getTiposAuditor().add(generarTipoAuditor(selectedTipoAuditorValue));
                String key= Keygen.getPassword();
                this.profesionalAuditorExterno.setPasswd(Crypt.cryptMD5(key));
                this.profesionalAuditorSB.savePAE(this.profesionalAuditorExterno);
                sendMailAction(key);
                FacesUtilComun.showInfoMessage("Registro correcto", "Su clave ha sido enviada a su email");
                return true;
            } catch(Exception ex) {
                FacesUtilComun.showErrorMessage("Error", "Se presento un error al guardar los datos");
                System.out.println(ex.toString());
            }
        }
        return false;
    }
    
    public String cancelAction() {
        this.resetAll();
        return "login";
    }
    
    private void resetAll() {
        HttpSession session = FacesUtilComun.getSession();
        session.invalidate();
    }
    
    public String endAction() {
        if(saveAction()) {
            this.resetAll();
            return "login";
        }
        return null;
    }
    
    /*
    * Agregar pagos a la lista
    */    
    public void addPago() {
        if(pago.getNumeroDeposito().length()>0 && pago.getValorDeposito()>0 && pago.getFechaDeposito().toString().length()>0) {
            if(!comprobarPagoRepetido(pago)) {
                pago.setEntidadFinanciera(nombreEntidadFinanciera);
                profesionalAuditorExterno.getPagos().add(pago);
                pago= new Pago();
                FacesUtilComun.showInfoMessage("Aviso", "Pago agregado");
            } else {
                FacesUtilComun.showErrorMessage("Error", "Ya ha sido agregado este número de pago");
            }
        } else {
            FacesUtilComun.showErrorMessage("Error", "Debe llenar todos los campos para agregar un pago");
        }
    }
    
    public void removePago(Pago p) {
        profesionalAuditorExterno.getPagos().remove(p);
    }
    
    private boolean comprobarPagoRepetido(Pago p) {
        for (Pago pago1 : profesionalAuditorExterno.getPagos()) {
            if(pago1.getNumeroDeposito().equals(pago.getNumeroDeposito())) {
                return true;
            }
        }
        return false;
    }
    
    public void verificarRucRepetido(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {
        Auditor tmp= auditorSB.findByRuc(arg2.toString());
        if(tmp!=null) {
            rucValido= false;
            FacesUtilComun.showErrorMessage("Error", "El RUC ingresado ya se encuentra registrado");
            FacesUtilComun.updateComponent("growl");
        } else {
            rucValido= true;
        }
    }
    
    public TipoAuditor generarTipoAuditor(Catalogo c) {
        TipoAuditor tipo= new TipoAuditor();
        tipo.setCatalogo(c);
        return tipo;
    }
    
    public void sugerirCedula() {
        if(profesionalAuditorExterno.getRuc()!=null && profesionalAuditorExterno.getRuc().length()>0){
            String c= profesionalAuditorExterno.getRuc().substring(0, 10);
            profesionalAuditorExterno.setCedulaRepresentanteLegal(c);
        }
    }
}
